package seminar7.repository;

/**
 * Unchecked exception thrown when the storage of a repository cannot be accessed
 * (e.g. the database is not running). It wraps the original checked exception
 * (SQLException), like UncheckedIOException wraps IOException in FileRepository,
 * so the Repository interface does not depend on the storage type.
 */
public class RepositoryException extends RuntimeException {

    /**
     *
     * @param message - the description of the error
     * @param cause   - the original exception (exception chaining)
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
