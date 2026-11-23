package service;

import domain.Entity;
import domain.Friendship;
import domain.User;
import repository.Repository;
import repository.RepositoryException;
import domain.validator.ValidationException;
import repository.PagingRepository;
import repository.PrietenieDbRepository;
import util.paging.Page;
import util.paging.Pageable;

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
            //extrag ID-urile prieteniilor
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
        boolean alreadyExists = StreamSupport.stream(repository.findAll().spliterator(), false)
                .anyMatch(u -> u.getFirstName().equalsIgnoreCase(firstName.trim()) &&
                        u.getLastName().equalsIgnoreCase(lastName.trim()));
        if (alreadyExists) {
            throw new RepositoryException("Exista deja un utilizator cu numele " + firstName + " " + lastName + "!");
        }

        User newUser = new User(firstName, lastName);
        User existing = repository.save(newUser);
        if (existing != null) {
            throw new RepositoryException("Utilizatorul nu a putut fi salvat (exista deja)!");
        }
        return newUser;
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

    /**
     * Deletes a user identified by their first and last name, along with their friendships.
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @return an {@link Optional} containing the deleted user, or empty if not found
     * @throws IllegalArgumentException if firstName or lastName is null
     * @throws RepositoryException      if a database access error occurs
     */
    public Optional<User> deleteByName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("Prenumele si numele nu pot fi null!");
        }

        Optional<User> userFound = StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(u -> u.getFirstName().equalsIgnoreCase(firstName.trim()) &&
                        u.getLastName().equalsIgnoreCase(lastName.trim()))
                .findFirst();

        return userFound.flatMap(u -> delete(u.getId()));
    }

    private void validatePageable(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Obiectul Pageable nu poate fi null!");
        }
        if (pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Numarul paginii nu poate fi negativ!");
        }
        if (pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Marimea paginii trebuie sa fie strict pozitiva!");
        }
    }

    /**
     * Retrieves a page of users from the repository.
     *
     * @param pageable the pagination specification (zero-based page number and positive page size)
     * @return a Page of users for the requested page
     * @throws IllegalArgumentException if pageable is null,code pageNumber < 0, or code pageSize <= 0
     * @throws UnsupportedOperationException if the active repository does not support pagination
     */
    public Page<User> findAllOnPage(Pageable pageable) {
        validatePageable(pageable);
        if (repository instanceof PagingRepository<Long, User> pagingRepo) {
            return pagingRepo.findAllOnPage(pageable);
        }
        throw new UnsupportedOperationException("Paginarea este suportata doar pentru repository.type=DATABASE!");
    }

    /**
     * Retrieves a page of friends for a given user.
     *
     * @param userId   the identifier of the user whose friends are requested
     * @param pageable the pagination specification (zero-based page number and positive page size)
     * @return a Page containing the friends on the requested page and the total friend count
     * @throws IllegalArgumentException if code userId is null or pagination parameters are invalid
     * @throws ValidationException if no user exists with the given code userId
     * @throws UnsupportedOperationException if the active friendship repository does not support SQL pagination
     */
    public Page<User> findFriendsOnPage(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("ID-ul utilizatorului nu poate fi null!");
        }
        validatePageable(pageable);

        if (repository.findOne(userId) == null) {
            throw new ValidationException("Utilizatorul cu ID " + userId + " nu exista!");
        }

        if (friendshipRepository instanceof PrietenieDbRepository dbRepo) {
            return dbRepo.findFriendsOnPage(userId, pageable);
        }
        throw new UnsupportedOperationException("Paginarea prietenilor este suportata doar pentru repository.type=DATABASE!");
    }
}