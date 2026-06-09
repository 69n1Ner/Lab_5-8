package exceptions;

public class EmptyContainerException extends Exception {
    public EmptyContainerException() {
        super("Контейнер пуст");
    }
}
