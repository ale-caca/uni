package domain.validator;
import java.util.Objects;

/**
 * Functional interface for validating entities
 *
 * @param <T> the type of entity to be validated
 */
@FunctionalInterface
public interface Validation<T>{
    /**
     * Validates an entity.
     *
     * @param entity the entity to validate
     * @throws ValidationException if the entity is invalid
     */
    void validate(T entity) throws ValidationException;

    /**
     * Chains this validator with another validator in sequence
     *
     * @param other the other validator to execute
     * @return a composed validator that applies both validations
     * @throws NullPointerException if other is null
     */
    default Validation<T> andThen(Validation<T> other){
        Objects.requireNonNull(other,"Validatorul secundar nu poate fi null");
        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}
