package service;

import domain.Entity;
import domain.Friendship;
import domain.User;
import domain.validator.ValidationException;
import repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

/**
 * Service class responsible for operations specific to friendship relations.
 */
public class FriendshipSv implements Service<Friendship> {
    private final Repository<Long, Friendship> repository;
    private final Repository<Long, User> userRepository;

    public FriendshipSv(Repository<Long, Friendship> repository, Repository<Long, User> userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Long genererateID() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(Entity::getId)
                .max(Long::compareTo)
                .map(id -> id + 1L)
                .orElse(1L);
    }

    @Override
    public Optional<Friendship> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        return Optional.ofNullable(repository.delete(id));
    }

    public Friendship save(Long id1, Long id2) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("ID-urile nu pot fi null!");
        }
        Long firstId = Math.min(id1, id2);
        Long secondId = Math.max(id1, id2);

        User user1 = Optional.ofNullable(userRepository.findOne(firstId))
                .orElseThrow(() -> new ValidationException("Utilizatorul cu ID " + firstId + " nu exista!"));
        User user2 = Optional.ofNullable(userRepository.findOne(secondId))
                .orElseThrow(() -> new ValidationException("Utilizatorul cu ID " + secondId + " nu exista!"));

        boolean alreadyFriends = StreamSupport.stream(repository.findAll().spliterator(), false)
                .anyMatch(f -> (f.getFriend1().getId().equals(firstId) && f.getFriend2().getId().equals(secondId)) ||
                        (f.getFriend1().getId().equals(secondId) && f.getFriend2().getId().equals(firstId)));

        if (alreadyFriends) {
            throw new ValidationException("Prietenia intre acesti utilizatori exista deja!");
        }

        Friendship friendship = new Friendship(user1, user2);
        friendship.setId(genererateID());
        return repository.save(friendship);
    }

    @Override
    public Iterable<Friendship> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Friendship> filter(Predicate<Friendship> filter) {
        return repository.findAll(filter);
    }

    public Friendship deleteByNames(String firstName1, String lastName1, String firstName2, String lastName2) {
        User user1 = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(u -> u.getFirstName().equalsIgnoreCase(firstName1) && u.getLastName().equalsIgnoreCase(lastName1))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Utilizatorul " + firstName1 + " " + lastName1 + " nu a fost gasit!"));

        User user2 = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(u -> u.getFirstName().equalsIgnoreCase(firstName2) && u.getLastName().equalsIgnoreCase(lastName2))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Utilizatorul " + firstName2 + " " + lastName2 + " nu a fost gasit!"));

        Long id1 = user1.getId();
        Long id2 = user2.getId();

        Friendship friendshipToDelete = StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(f -> (f.getFriend1().getId().equals(id1) && f.getFriend2().getId().equals(id2)) ||
                        (f.getFriend1().getId().equals(id2) && f.getFriend2().getId().equals(id1)))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Nu exista o prietenie intre acesti doi utilizatori!"));

        return repository.delete(friendshipToDelete.getId());
    }
}