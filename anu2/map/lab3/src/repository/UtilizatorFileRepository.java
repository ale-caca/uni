package repository;

import domain.User;
import domain.validator.Validation;

/**
 * File-based repository implementation for User entities.
 */
public class UtilizatorFileRepository extends AbstractFileRepository<Long, User> {

    /**
     * Constructor for the user file repository.
     *
     * @param validator the validator for user entities
     * @param fileName the name of the file where users are stored
     */
    public UtilizatorFileRepository(Validation<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected User extractEntity(String line) {
        String[] fields = line.split(";", -1);
        User user = new User(fields[1], fields[2]);
        user.setId(Long.parseLong(fields[0]));
        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}