package seminar7.domain.validators;

import java.util.function.Predicate;

/**
 * Validation strategy for entities.
 * Functional interface: it has a single abstract method (validate), so a
 * validator can be written as a lambda expression or as a method reference.
 *
 * @param <T> - type of the entities to be validated
 */
@FunctionalInterface
public interface Validator<T> {

    /**
     *
     * @param entity - the entity to be validated
     * @throws ValidationException if the entity is not valid
     */
    void validate(T entity);

    /**
     * composes two validations: first this one, then the other one;
     * if this validation fails, the other one is not applied
     *
     * @param other - the validation applied after this one; must not be null
     * @return a validator that applies this validation, then the other one
     * @throws IllegalArgumentException if other is null
     */
    default Validator<T> andThen(Validator<T> other) {
        if (other == null)
            throw new IllegalArgumentException("other must be not null");
        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }

    /**
     * static factory method in an interface (Java 8): builds a validator from a
     * condition (Predicate) and the message reported when the condition is false
     *
     * @param condition - the condition a valid entity must satisfy; must not be null
     * @param message    - the message of the ValidationException
     * @param <T>      - type of the entities to be validated
     * @return a validator that throws ValidationException(mesaj) if conditie is false
     * @throws IllegalArgumentException if conditie is null
     */
    static <T> Validator<T> of(Predicate<T> condition, String message) {
        if (condition == null)
            throw new IllegalArgumentException("condition must be not null");
        return entity -> {
            if (!condition.test(entity))
                throw new ValidationException(message);
        };
    }
}