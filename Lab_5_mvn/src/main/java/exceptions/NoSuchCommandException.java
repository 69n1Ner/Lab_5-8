package exceptions;

public class NoSuchCommandException extends NoSuchEntityException {
    public NoSuchCommandException() {
        super("Такой команды не существует, введите help для справки");
    }
    public NoSuchCommandException(String message){
        super(message);
    }
}
