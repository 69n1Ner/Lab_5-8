package exceptions;

public class NoSuchOrganizationException extends NoSuchEntityException{
    public NoSuchOrganizationException(){
        super("Такой организации не существует");
    }
}
