package service;

import domain.User;

import java.util.function.Predicate;

/**
 * Utility class offering search predicates for User entities.
 */
public class UserCriteria {

    /**
     * Returns a predicate testing if either first name or last name starts with prefix.
     *
     * @param prefix the string prefix
     * @return Predicate testing prefix
     */
    public static Predicate<User> nameStartsWith(String prefix) {
        return user -> user.getFirstName().startsWith(prefix) || user.getLastName().startsWith(prefix);
    }

    /**
     * Returns a predicate testing if a user's last name matches the given parameter.
     *
     * @param lastName target last name
     * @return Predicate testing last name
     */
    public static Predicate<User> hasLastName(String lastName) {
        return user -> user.getLastName().equalsIgnoreCase(lastName);
    }

    /**
     * Returns a predicate testing if a user's first name matches the given parameter.
     *
     * @param firstName target first name
     * @return Predicate testing first name
     */
    public static Predicate<User> hasFirstName(String firstName) {
        return user -> user.getFirstName().equalsIgnoreCase(firstName);
    }

    /**
     * Static method suitable for method reference UserCriteria::hasLongName.
     * Tests if first name has at least 5 characters.
     *
     * @param user the user to test
     * @return true if first name length >= 5, false otherwise
     */
    public static boolean hasLongName(User user) {
        return user != null && user.getFirstName() != null && user.getFirstName().length() >= 5;
    }
}