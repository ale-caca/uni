package repository;

import domain.Entity;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
/**
 * Interface for CRUD operations on a repository.
 *
 * @param <ID> the type of the identifier
 * @param <E> the type of the entity (must extend Entity)
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * Finds an entity by its ID.
     *
     * @param id the identifier of the entity to be found
     * @return the entity with the specified ID, or null if it does not exist
     * @throws IllegalArgumentException if the given id is null
     */
    E findOne(ID id);
    /**
     * Returns all entities.
     *
     * @return an iterable object containing all entities
     */
    Iterable<E> findAll();
    /**
     * Saves an entity.
     *
     * @param entity the entity to be saved
     * @return null if the save was successful, or the entity if it already exists
     * @throws IllegalArgumentException if the entity is null
     * @throws domain.validator.ValidationException if the entity attributes are invalid
     */
    E save(E entity);
    /**
     * Deletes an entity by its ID.
     *
     * @param id the identifier of the entity to be deleted
     * @return the deleted entity, or null if no entity with this ID exists
     * @throws IllegalArgumentException if the given id is null
     */
    E delete(ID id);
    /**
     * Filters all entities using a functional predicate
     *
     * @param filter predicate applied to filter entities
     * @return a list containing entities that satisfy the predicate
     * @throws NullPointerException if filter is null
     */
    default List<E> findAll(Predicate<E> filter) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(filter)
                .collect(Collectors.toList());
    }
}

