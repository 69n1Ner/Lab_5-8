package Exceptions;

public class NullCommandException extends RuntimeException {
    public NullCommandException(String message) {
        super(message);
    }
}
