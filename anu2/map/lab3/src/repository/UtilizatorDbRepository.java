package repository;

import domain.User;
import domain.validator.Validation;
import domain.validator.ValidationException;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC repository implementation for managing User entities in a PostgreSQL database.
 */
public class UtilizatorDbRepository implements PagingRepository<Long, User> {
    private final Validation<User> validator;
    private final String url;
    private final String user;
    private final String password;

    /**
     * Constructs a new database repository for users.
     *
     * @param validator the validator used to validate User entities before saving or updating
     * @param url       the JDBC connection URL
     * @param user      the database username
     * @param password  the database password
     */
    public UtilizatorDbRepository(Validation<User> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }


    /**
     * Retrieves a page of users from the database using SQL pagination and computes
     * the total number of users over the same JDBC connection.
     *
     * @param pageable the pagination parameters (zero-based page number and positive page size)
     * @return a Page containing the users on the requested page and the total user count
     * @throws IllegalArgumentException if pageable is null, pageNumber < 0, or pageSize <= 0
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Parametrii de paginare sunt invalizi (pageNumber >= 0, pageSize > 0)!");
        }

        List<User> users = new ArrayList<>();
        String countSql = "SELECT COUNT(*) AS total FROM users";
        String pageSql = "SELECT id, first_name, last_name FROM users ORDER BY id LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            int totalCount = 0;
            try (PreparedStatement countStmt = connection.prepareStatement(countSql);
                 ResultSet countRs = countStmt.executeQuery()) {
                if (countRs.next()) {
                    totalCount = countRs.getInt("total");
                }
            }

            try (PreparedStatement pageStmt = connection.prepareStatement(pageSql)) {
                pageStmt.setInt(1, pageable.getPageSize());
                pageStmt.setInt(2, pageable.getPageNumber() * pageable.getPageSize());
                try (ResultSet pageRs = pageStmt.executeQuery()) {
                    while (pageRs.next()) {
                        users.add(extractUser(pageRs));
                    }
                }
            }

            return new Page<>(users, totalCount);
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la paginarea utilizatorilor din baza de date: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a user by its unique identifier in the database.
     *
     * @param id the identifier of the user to find
     * @return the found {@link User}, or {@code null} if no user exists with the given id
     * @throws IllegalArgumentException if the given id is null
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public User findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        String sql = "SELECT id, first_name, last_name FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractUser(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la cautarea utilizatorului in baza de date: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all users stored in the database.
     *
     * @return an {@link Iterable} containing all users
     * @throws RepositoryException if a database access error occurs
     */
    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name FROM users ORDER BY id";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(extractUser(resultSet));
            }
            return users;
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la citirea utilizatorilor din baza de date: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a new user into the database and assigns the database-generated ID to the entity.
     *
     * @param entity the user entity to save
     * @return {@code null} if the user was successfully saved, or the entity if it already exists
     * @throws IllegalArgumentException if the entity is null
     * @throws ValidationException      if the entity is invalid
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public User save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        }
        validator.validate(entity);

        if (entity.getId() != null && findOne(entity.getId()) != null) {
            return entity;
        }

        String sql = "INSERT INTO users (first_name, last_name) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return entity;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la salvarea utilizatorului in baza de date: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user from the database by its ID.
     *
     * @param id the identifier of the user to delete
     * @return the deleted {@link User} entity, or {@code null} if no user with the given ID exists
     * @throws IllegalArgumentException if the id is null
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public User delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        User existingUser = findOne(id);
        if (existingUser == null) {
            return null;
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0 ? existingUser : null;
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la stergerea utilizatorului din baza de date: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user in the database.
     *
     * @param entity the user entity containing updated data
     * @return {@code null} if the user was updated, or the entity itself if the ID does not exist
     * @throws IllegalArgumentException if the entity or its ID is null
     * @throws ValidationException      if the entity is invalid
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public User update(User entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entitatea si ID-ul nu pot fi null!");
        }
        validator.validate(entity);

        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0 ? null : entity;
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la actualizarea utilizatorului in baza de date: " + e.getMessage(), e);
        }
    }

    private User extractUser(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        User u = new User(firstName, lastName);
        u.setId(id);
        return u;
    }
}