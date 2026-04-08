package exceptions;

public class NullCommandException extends RuntimeException {
    public NullCommandException(String message) {
        super(message);
    }
}
