package exceptions;

public class EmptyContainerException extends RuntimeException {
    public EmptyContainerException() {
        super("Контейнер пуст");
    }
}
