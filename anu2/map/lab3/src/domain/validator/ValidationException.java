package domain.validator;
//clasa proprie de exceptii
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

}
