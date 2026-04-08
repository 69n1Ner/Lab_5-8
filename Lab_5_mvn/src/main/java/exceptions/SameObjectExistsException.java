package exceptions;

public class SameObjectExistsException extends RuntimeException {
    public SameObjectExistsException(String message) {
        super(message);
    }
}
