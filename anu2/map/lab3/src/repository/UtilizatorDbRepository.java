package repository;

import domain.User;
import domain.validator.Validation;

/**
 * Database-based repository implementation for User entities.
 * Currently acts as a skeleton for future JDBC implementation.
 */
public class UtilizatorDbRepository implements Repository<Long, User> {

    public UtilizatorDbRepository(Validation<User> validator, String url, String user, String password) {
        // Configurarea conexiunii se va face în Lab 4
    }

    @Override
    public User findOne(Long id) { throw new UnsupportedOperationException(); }

    @Override
    public Iterable<User> findAll() { throw new UnsupportedOperationException(); }

    @Override
    public User save(User entity) { throw new UnsupportedOperationException(); }

    @Override
    public User delete(Long id) { throw new UnsupportedOperationException(); }
}