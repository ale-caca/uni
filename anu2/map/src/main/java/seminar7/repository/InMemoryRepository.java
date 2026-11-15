package seminar7.repository;

import seminar7.domain.Entity;
import seminar7.domain.validators.ValidationException;
import seminar7.domain.validators.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repository that keeps the entities in memory, in a map id -&gt; entity.
 * Strategy: the validator (the strategy) is injected through the constructor;
 * it can be a class, a lambda or a method reference.
 *
 * @param <ID> - type E must have an attribute of type ID
 * @param <E>  - type of entities saved in repository
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private final Validator<E> validator;
    private final Map<ID, E> entities;

    /**
     *
     * @param validator - the validation strategy for the entities
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<ID, E>();
    }

    /**
     *
     * @param id - the id of the entity to be returned; must not be null
     * @return an Optional with the entity with the specified id
     * or Optional.empty() - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    @Override
    public Optional<E> findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("id must be not null");
        return Optional.ofNullable(entities.get(id));   // get poate intoarce null
    }

    /**
     *
     * @return all entities
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     *
     * @param entity - entity must be not null
     * @return Optional.empty() - if the given entity is saved
     * otherwise returns an Optional with the entity (id already exists)
     * @throws ValidationException if the entity is not valid
     * @throws IllegalArgumentException                   if the given entity or its id is null.
     */
    @Override
    public Optional<E> save(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        if (entity.getId() == null)          // in memorie si in fisier id-ul nu se genereaza
            throw new IllegalArgumentException("id must be not null");
        validator.validate(entity);
        if (entities.containsKey(entity.getId()))
            return Optional.of(entity);
        entities.put(entity.getId(), entity);
        return Optional.empty();
    }

    /**
     *
     * @param id - id must be not null
     * @return an Optional with the removed entity
     * or Optional.empty() - if there is no entity with the given id
     */
    @Override
    public Optional<E> delete(ID id) {
        return Optional.ofNullable(entities.remove(id));
    }

    /**
     *
     * @param entity - entity must not be null
     * @return Optional.empty() - if the entity is updated,
     * otherwise returns an Optional with the entity - (e.g id does not exist).
     * @throws IllegalArgumentException                   if the given entity is null.
     * @throws ValidationException if the entity is not valid.
     */
    @Override
    public Optional<E> update(E entity) {

        if (entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        if (!entities.containsKey(entity.getId()))
            return Optional.of(entity);
        entities.put(entity.getId(), entity);
        return Optional.empty();
    }
}
