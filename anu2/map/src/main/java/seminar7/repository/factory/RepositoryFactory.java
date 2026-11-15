package seminar7.repository.factory;


import seminar7.domain.Movie;
import seminar7.domain.validators.Validator;
import seminar7.repository.InMemoryRepository;
import seminar7.repository.Repository;
import seminar7.repository.db.MovieDBRepository;

import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Simple Factory: the only place that knows the concrete repository classes.
 * The type of storage and its parameters are read from the configuration,
 * so changing the storage means changing the configuration, not the code.
 * Seminar 5: the switch is replaced by a map storage type -&gt; Supplier.
 */
public class RepositoryFactory {
    private final Properties props;

    /**
     *
     * @param props - the configuration (repository.type, file.utilizatori,
     *             db.url, db.user, db.password)
     */
    public RepositoryFactory(Properties props) {
        this.props = props;
    }


    public Repository<Long, Movie> createMovieRepository(Validator<Movie> validator) {
        String value = props.getProperty("repository.type");
        if (value == null)
            throw new IllegalArgumentException("Missing property repository.type");
        RepositoryType type = RepositoryType.valueOf(value.trim().toUpperCase());

        Map<RepositoryType, Supplier<Repository<Long, Movie>>> creatori = Map.of(
                RepositoryType.MEMORY, () -> new InMemoryRepository<>(validator),
                RepositoryType.DATABASE, () -> new MovieDBRepository(validator,
                        props.getProperty("db.url"),
                        props.getProperty("db.user"),
                        props.getProperty("db.password"))
        );

        Supplier<Repository<Long, Movie>> creator = creatori.get(type);
        if (creator == null)
            throw new IllegalArgumentException("There is no such repo type: " + type);
        return creator.get();
    }


}
