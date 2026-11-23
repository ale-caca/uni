package repository;


import domain.Entity;
import util.paging.Page;
import util.paging.Pageable;
/**
 * Extension of Repository supporting paginated retrieval of entities.
 *
 * @param <ID> the type of the entity identifier
 * @param <E>  the type of the stored entity, extending Entity
 */
public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    /**
     * Retrieves a single page of entities according to the given pagination specification.
     *
     * @param pageable the pagination request specifying page index and page size
     * @return a Page containing the entities on the requested page and the total element count
     * @throws IllegalArgumentException if pagination parameters are invalid
     * @throws RepositoryException      if a data access error occurs
     */
    Page<E> findAllOnPage(Pageable pageable);
}
