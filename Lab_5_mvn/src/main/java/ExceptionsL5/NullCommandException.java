package ExceptionsL5;

public class NullCommandException extends RuntimeException {
    public NullCommandException(String message) {
        super(message);
    }
}
