package service;

import domain.Entity;
import domain.Friendship;
import domain.User;
import repository.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service class responsible for operations regarding users.
 */
public class UserSv implements Service<User> {
    private final Repository<Long, User> repository;
    private final Repository<Long, Friendship> friendshipRepository;

    public UserSv(Repository<Long, User> repository, Repository<Long, Friendship> friendshipRepository) {
        this.repository = repository;
        this.friendshipRepository = friendshipRepository;
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
    public Optional<User> delete(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID-ul utilizatorului nu poate fi null!");
        }

        Optional<User> userOptional = Optional.ofNullable(repository.findOne(userId));

        userOptional.ifPresent(u -> {
            // Extragem ID-urile prieteniilor fara efecte secundare in filter/map
            List<Long> friendshipsToDelete = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                    .filter(f -> f.getFriend1().getId().equals(userId) || f.getFriend2().getId().equals(userId))
                    .map(Entity::getId)
                    .collect(Collectors.toList());

            // Stergere prin referinta la metoda de instanta a unui obiect anume
            friendshipsToDelete.forEach(friendshipRepository::delete);

            repository.delete(userId);
        });

        return userOptional;
    }

    public User save(String firstName, String lastName) {
        Long newId = genererateID();
        User newUser = new User(firstName, lastName);
        newUser.setId(newId);
        return repository.save(newUser);
    }

    @Override
    public Iterable<User> findAll() {
        return repository.findAll();
    }

    @Override
    public List<User> filter(Predicate<User> filter) {
        return repository.findAll(filter);
    }

    public List<User> getFriends(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        return StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(f -> f.getFriend1().getId().equals(id) || f.getFriend2().getId().equals(id))
                .map(f -> f.getFriend1().getId().equals(id) ? f.getFriend2().getId() : f.getFriend1().getId())
                .map(repository::findOne)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<User> findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        return Optional.ofNullable(repository.findOne(id));
    }
}