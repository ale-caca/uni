package util.paging;

/**
 * Represents a pagination request specifying the zero-based page index and page size.
 */
public class Pageable {
    private final int pageNumber;
    private final int pageSize;

    /**
     * Constructs a new pagination request.
     *
     * @param pageNumber the zero-based index of the requested page
     * @param pageSize   the maximum number of elements per page
     */
    public Pageable(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * Returns the zero-based page number.
     *
     * @return the page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Returns the maximum number of elements per page.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }
}