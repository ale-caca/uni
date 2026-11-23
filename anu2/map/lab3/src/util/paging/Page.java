package util.paging;

import java.util.List;

/**
 * Represents a paginated query result containing the elements on the current page
 * and the total number of matching elements in the data source.
 *
 * @param <E> the type of elements stored in the page
 */
public class Page<E> {
    private final List<E> elementsOnPage;
    private final int totalNumberOfElements;

    /**
     * Constructs a new page result.
     *
     * @param elementsOnPage        the list of elements belonging to the requested page
     * @param totalNumberOfElements the total count of elements across all pages
     */
    public Page(List<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    /**
     * Returns the elements contained on this page.
     *
     * @return a list of elements on the current page
     */
    public List<E> getElementsOnPage() {
        return elementsOnPage;
    }

    /**
     * Returns the total number of elements available across all pages.
     *
     * @return the total number of elements
     */
    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}