package exceptions;

public class NoSuchEntityException extends Exception {
    public NoSuchEntityException(String message) {
        super(message);
    }
    public NoSuchEntityException(){
        super("Такой сущности нет");
    }

    public static String getMsg(){
        return "Такой сущности нет";
    }
}
