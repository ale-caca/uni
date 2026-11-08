package service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Generic interface defining common service operations.
 *
 * @param <E> the entity type
 */
public interface Service<E> {
    /**
     * Deletes an entity based on its ID.
     *
     * @param id the ID of the entity to be deleted
     * @return an Optional containing the deleted entity, or empty Optional if not found
     */
    Optional<E> delete(Long id);

    /**
     * Generates a unique incremented ID for a new entity.
     *
     * @return generated ID
     */
    Long genererateID();

    /**
     * Returns all entities managed by the service.
     *
     * @return an iterable collection containing all entities
     */
    Iterable<E> findAll();

    /**
     * Filters entities based on a predicate.
     *
     * @param filter predicate used to filter entities
     * @return list of matching entities
     */
    List<E> filter(Predicate<E> filter);
}