package exceptions;

public class NoSuchCommandException extends RuntimeException {
    public NoSuchCommandException() {
        super("Такой команды не существует");
    }
    public NoSuchCommandException(String message){
        super(message);
    }
}
