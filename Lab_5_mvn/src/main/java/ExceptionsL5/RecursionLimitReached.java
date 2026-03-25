package ExceptionsL5;

public class RecursionLimitReached extends RuntimeException {
    public RecursionLimitReached(String message) {
        super(message);
    }
}
