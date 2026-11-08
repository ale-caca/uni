package repository;

import domain.Friendship;
import domain.validator.Validation;

public class PrietenieDbRepository implements Repository<Long, Friendship> {

    public PrietenieDbRepository(Validation<Friendship> validator, String url, String user, String password) {
    }

    @Override
    public Friendship findOne(Long id) { throw new UnsupportedOperationException(); }

    @Override
    public Iterable<Friendship> findAll() { throw new UnsupportedOperationException(); }

    @Override
    public Friendship save(Friendship entity) { throw new UnsupportedOperationException(); }

    @Override
    public Friendship delete(Long id) { throw new UnsupportedOperationException(); }
}