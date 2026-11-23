package repository;

import domain.Friendship;
import domain.User;
import domain.validator.Validation;
import domain.validator.ValidationException;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * JDBC repository implementation for managing Friendship entities in a PostgreSQL database.
 */
public class PrietenieDbRepository implements Repository<Long, Friendship> {
    private final Validation<Friendship> validator;
    private final String url;
    private final String user;
    private final String password;

    /**
     * Constructs a new database repository for friendships.
     *
     * @param validator the validator used to validate Friendship entities
     * @param url       the JDBC connection URL
     * @param user      the database username
     * @param password  the database password
     */
    public PrietenieDbRepository(Validation<Friendship> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Retrieves a paginated list of friends for the specified user directly in SQL
     * using a single JOIN query and computes the total friend count over the same connection.
     *
     * @param userId   the identifier of the user whose friends are queried
     * @param pageable the pagination parameters (zero-based page number and positive page size)
     * @return a Page containing the user's friends on the requested page and the total number of friends
     * @throws IllegalArgumentException if userId is null or pagination parameters are invalid
     * @throws RepositoryException if a database access error occurs
     */
    public Page<User> findFriendsOnPage(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("ID-ul utilizatorului nu poate fi null!");
        }
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Parametrii de paginare sunt invalizi (pageNumber >= 0, pageSize > 0)!");
        }

        List<User> friends = new ArrayList<>();
        String countSql = "SELECT COUNT(*) AS total " +
                "FROM friendships f " +
                "JOIN users u ON u.id = CASE WHEN f.id1 = ? THEN f.id2 ELSE f.id1 END " +
                "WHERE f.id1 = ? OR f.id2 = ?";

        String pageSql = "SELECT u.id, u.first_name, u.last_name " +
                "FROM friendships f " +
                "JOIN users u ON u.id = CASE WHEN f.id1 = ? THEN f.id2 ELSE f.id1 END " +
                "WHERE f.id1 = ? OR f.id2 = ? " +
                "ORDER BY u.id LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            int totalCount = 0;
            try (PreparedStatement countStmt = connection.prepareStatement(countSql)) {
                countStmt.setLong(1, userId);
                countStmt.setLong(2, userId);
                countStmt.setLong(3, userId);
                try (ResultSet countRs = countStmt.executeQuery()) {
                    if (countRs.next()) {
                        totalCount = countRs.getInt("total");
                    }
                }
            }

            try (PreparedStatement pageStmt = connection.prepareStatement(pageSql)) {
                pageStmt.setLong(1, userId);
                pageStmt.setLong(2, userId);
                pageStmt.setLong(3, userId);
                pageStmt.setInt(4, pageable.getPageSize());
                pageStmt.setInt(5, pageable.getPageNumber() * pageable.getPageSize());
                try (ResultSet pageRs = pageStmt.executeQuery()) {
                    while (pageRs.next()) {
                        User friend = new User(
                                pageRs.getString("first_name"),
                                pageRs.getString("last_name")
                        );
                        friend.setId(pageRs.getLong("id"));
                        friends.add(friend);
                    }
                }
            }

            return new Page<>(friends, totalCount);
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la paginarea prietenilor din baza de date: " + e.getMessage(), e);
        }
    }


    /**
     * Finds a friendship by its assigned identifier.
     *
     * @param id the identifier of the friendship
     * @return the matching {@link Friendship}, or {@code null} if not found
     * @throws IllegalArgumentException if the given id is null
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Friendship findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all friendships from the database.
     *
     * @return an {@link Iterable} of all friendships
     * @throws RepositoryException if a database access error occurs
     */
    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT id1, id2, friends_from FROM friendships ORDER BY id1, id2";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            long currentId = 1L;
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();

                User u1 = new User("", "");
                u1.setId(id1);
                User u2 = new User("", "");
                u2.setId(id2);

                Friendship friendship = new Friendship(u1, u2);
                friendship.setDate(friendsFrom);
                friendship.setId(currentId++);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la citirea prieteniilor din baza de date: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a new friendship in the database enforcing {@code id1 < id2}.
     *
     * @param entity the friendship to save
     * @return {@code null} if saved successfully, or the entity if the friendship already exists
     * @throws IllegalArgumentException if the entity is null
     * @throws ValidationException      if the entity violates validation rules
     * @throws RepositoryException      if a database error or constraint violation occurs
     */
    @Override
    public Friendship save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        }
        validator.validate(entity);

        Long rawId1 = entity.getFriend1().getId();
        Long rawId2 = entity.getFriend2().getId();
        Long id1 = Math.min(rawId1, rawId2);
        Long id2 = Math.max(rawId1, rawId2);

        if (existsByUserIds(id1, id2)) {
            return entity;
        }

        String sql = "INSERT INTO friendships (id1, id2, friends_from) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0 ? null : entity;
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la salvarea prieteniei in baza de date: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a friendship by its identifier.
     *
     * @param id the identifier of the friendship to delete
     * @return the deleted {@link Friendship}, or {@code null} if no such friendship exists
     * @throws IllegalArgumentException if the id is null
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Friendship delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        Friendship existing = findOne(id);
        if (existing == null) {
            return null;
        }

        Long rawId1 = existing.getFriend1().getId();
        Long rawId2 = existing.getFriend2().getId();
        Long id1 = Math.min(rawId1, rawId2);
        Long id2 = Math.max(rawId1, rawId2);

        String sql = "DELETE FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0 ? existing : null;
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la stergerea prieteniei din baza de date: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing friendship timestamp in the database.
     *
     * @param entity the friendship with updated attributes
     * @return {@code null} if updated successfully, or the entity if it does not exist
     * @throws IllegalArgumentException if the entity is null
     * @throws ValidationException      if the entity is invalid
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Friendship update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        }
        validator.validate(entity);

        Long rawId1 = entity.getFriend1().getId();
        Long rawId2 = entity.getFriend2().getId();
        Long id1 = Math.min(rawId1, rawId2);
        Long id2 = Math.max(rawId1, rawId2);

        String sql = "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setLong(2, id1);
            statement.setLong(3, id2);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0 ? null : entity;
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la actualizarea prieteniei in baza de date: " + e.getMessage(), e);
        }
    }

    private boolean existsByUserIds(Long id1, Long id2) {
        String sql = "SELECT 1 FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la verificarea existentei prieteniei: " + e.getMessage(), e);
        }
    }
}