package seminar7.repository;


import seminar7.domain.Entity;
import seminar7.domain.validators.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * CRUD operations repository interface
 * Curs 5: the results that may be missing are Optional&lt;E&gt; instead of null,
 * and the repository can be traversed as a Stream&lt;E&gt;.
 *
 * @param <ID> - type E must have an attribute of type ID
 * @param <E>  -  type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>> {

    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return an Optional with the entity with the specified id
     * or Optional.empty() - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    Optional<E> findOne(ID id);

    /**
     *
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     * Default method: every implementation of Repository receives it without
     * being modified. Iterable has no stream() method (only Collection has),
     * so the stream is built from the spliterator of findAll().
     *
     * @return a sequential stream over all the entities
     */
    default Stream<E> stream() {
        return StreamSupport.stream(findAll().spliterator(), false);
    }

    /**
     * Default method: every implementation of Repository receives it without
     * being modified; an implementation may override it (e.g. a database
     * repository could filter with a WHERE clause instead of in memory).
     * Seminar 5 wrote it with a for loop; here it is a stream pipeline.
     *
     * @param filter - the condition the returned entities must satisfy
     *               filter must not be null
     * @return the entities that satisfy the filter
     * (an empty list - if there is no such entity)
     * @throws IllegalArgumentException if filter is null.
     */
    default List<E> findAll(Predicate<E> filter) {
        if (filter == null)
            throw new IllegalArgumentException("filter must be not null");
        return stream().filter(filter).toList();
    }

    /**
     *
     * @param entity entity must be not null
     * @return Optional.empty() - if the given entity is saved
     * otherwise returns an Optional with the entity (id already exists)
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.
     */
    Optional<E> save(E entity);

    /**
     * removes the entity with the specified id
     *
     * @param id id must be not null
     * @return an Optional with the removed entity
     * or Optional.empty() - if there is no entity with the given id
     * @throws IllegalArgumentException if the given id is null.
     */
    Optional<E> delete(ID id);

    /**
     *
     * @param entity entity must not be null
     * @return Optional.empty() - if the entity is updated,
     * otherwise returns an Optional with the entity - (e.g id does not exist).
     * @throws IllegalArgumentException if the given entity is null.
     * @throws ValidationException      if the entity is not valid.
     */
    Optional<E> update(E entity);
}
