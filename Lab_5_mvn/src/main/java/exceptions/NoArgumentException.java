package exceptions;

public class NoArgumentException extends RuntimeException {
    public NoArgumentException() {
        super("Нет необходимого аргумента");
    }
}
