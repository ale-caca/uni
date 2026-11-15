package repository;

import domain.Entity;
import domain.validator.Validation;
import domain.validator.ValidationException;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of the generic Repository interface.
 *
 * @param <ID> the type of the identifier for entities
 * @param <E>  the type of the stored entity
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private final Validation<E> validator;
    protected Map<ID, E> entitati;

    public InMemoryRepository(Validation<E> validator) {
        this.validator = validator;
        entitati = new HashMap<>();
    }

    @Override
    public E findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id nu poate fi NULL");
        }
        return entitati.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entitati.values();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E save(E entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("entitatea nu poate fi NULL");
        }
        validator.validate(entity);

        if (entity.getId() == null) {
            long nextId = entitati.keySet().stream()
                    .filter(Long.class::isInstance)
                    .mapToLong(k -> (Long) k)
                    .max()
                    .orElse(0L) + 1L;
            entity.setId((ID) Long.valueOf(nextId));
        }

        if (entitati.containsKey(entity.getId())) {
            return entity;
        } else {
            entitati.put(entity.getId(), entity);
            return null;
        }
    }

    @Override
    public E delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id nu poate fi NULL");
        }
        return entitati.remove(id);
    }

    @Override
    public E update(E entity) throws ValidationException {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("entitatea si id-ul nu pot fi NULL");
        }
        validator.validate(entity);
        if (!entitati.containsKey(entity.getId())) {
            return entity;
        }
        entitati.put(entity.getId(), entity);
        return null;
    }
}