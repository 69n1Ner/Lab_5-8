package Exceptions;

public class RecursionLimitReached extends RuntimeException {
    public RecursionLimitReached(String message) {
        super(message);
    }
}
