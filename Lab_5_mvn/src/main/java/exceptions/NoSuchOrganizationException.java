package exceptions;

public class NoSuchOrganizationException extends Exception{
    public NoSuchOrganizationException(){
        super("Такой организации не существует");
    }
}
