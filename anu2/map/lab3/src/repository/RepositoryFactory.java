package repository;

import domain.Friendship;
import domain.User;
import domain.validator.Validation;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;


/**
 * Factory class creating repository instances based on config.properties using Map and functional interfaces.
 */
public class RepositoryFactory {
    public enum RepositoryType { MEMORY, FILE, DATABASE }

    private RepositoryType type;
    private final Properties properties = new Properties();

    private final Map<RepositoryType, Function<Validation<User>, Repository<Long, User>>> userRepoFactories = new EnumMap<>(RepositoryType.class);
    private final Map<RepositoryType, Function<Validation<Friendship>, Repository<Long, Friendship>>> friendshipRepoFactories = new EnumMap<>(RepositoryType.class);

    public RepositoryFactory() {
        loadProperties();
        initUserFactories();
        initFriendshipFactories();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RepositoryException("Fisierul config.properties nu a fost gasit!");
            }
            properties.load(input);
            String configType = properties.getProperty("repository.type");
            if (configType == null) {
                throw new RepositoryException("Proprietatea repository.type lipseste!");
            }
            this.type = RepositoryType.valueOf(configType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Tipul de repository din config.properties este invalid!");
        } catch (Exception e) {
            throw new RepositoryException("Eroare la incarcarea configuratiei: " + e.getMessage(), e);
        }
    }

    private void initUserFactories() {
        // Referinta la constructor: InMemoryRepository::new
        userRepoFactories.put(RepositoryType.MEMORY, InMemoryRepository::new);
        userRepoFactories.put(RepositoryType.FILE, validator -> {
            String file = properties.getProperty("utilizatori.file");
            if (file == null) {
                throw new RepositoryException("Calea utilizatori.file lipseste!");
            }
            return new FileRepository<>(validator, file, User::fromString, User::toFileLine);
        });
        userRepoFactories.put(RepositoryType.DATABASE, validator ->
                new UtilizatorDbRepository(validator,
                        properties.getProperty("db.url"),
                        properties.getProperty("db.user"),
                        properties.getProperty("db.password")));
    }

    private void initFriendshipFactories() {
        friendshipRepoFactories.put(RepositoryType.MEMORY, InMemoryRepository::new);
        friendshipRepoFactories.put(RepositoryType.FILE, validator -> {
            String file = properties.getProperty("prietenii.file");
            if (file == null) {
                throw new RepositoryException("Calea prietenii.file lipseste!");
            }

            // Contor local doar pentru incarcarea initiala a fisierului, cu ID-uri simple: 1, 2, 3...
            AtomicLong counter = new AtomicLong(1L);

            return new FileRepository<>(
                    validator,
                    file,
                    line -> {
                        String[] fields = line.split(";", -1);
                        Long id1 = Long.parseLong(fields[0]);
                        Long id2 = Long.parseLong(fields[1]);
                        LocalDateTime date = LocalDateTime.parse(fields[2]);

                        User u1 = new User("", "");
                        u1.setId(id1);
                        User u2 = new User("", "");
                        u2.setId(id2);

                        Friendship f = new Friendship(u1, u2);
                        f.setDate(date);
                        f.setId(counter.getAndIncrement()); // 1, 2, 3... normal, fara numere mari
                        return f;
                    },
                    f -> f.getFriend1().getId() + ";" + f.getFriend2().getId() + ";" + f.getDate()
            );
        });
    }

    /**
     * Creates a User repository instance according to the configured repository type.
     *
     * @param validator the validator to use
     * @return the repository instance
     */
    public Repository<Long, User> createUtilizatorRepository(Validation<User> validator) {
        return Optional.ofNullable(userRepoFactories.get(type))
                .map(factory -> factory.apply(validator))
                .orElseThrow(() -> new RepositoryException("Tip de repository necunoscut pentru utilizatori: " + type));
    }

    /**
     * Creates a Friendship repository instance according to the configured repository type.
     *
     * @param validator the validator to use
     * @return the repository instance
     */
    public Repository<Long, Friendship> createPrietenieRepository(Validation<Friendship> validator) {
        return Optional.ofNullable(friendshipRepoFactories.get(type))
                .map(factory -> factory.apply(validator))
                .orElseThrow(() -> new RepositoryException("Tip de repository necunoscut pentru prietenii: " + type));
    }
}