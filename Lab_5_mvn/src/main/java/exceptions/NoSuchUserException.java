package exceptions;

public class NoSuchUserException extends NoSuchEntityException {
    public NoSuchUserException() {
        super("Такого пользователя не существует");
    }
}
