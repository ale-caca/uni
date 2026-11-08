package domain.validator;
//interfata pt validator
public interface Validation<T>{
    void validate(T entity) throws ValidationException;
}