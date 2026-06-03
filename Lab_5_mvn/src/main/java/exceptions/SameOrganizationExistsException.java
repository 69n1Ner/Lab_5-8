package exceptions;

public class SameOrganizationExistsException extends RuntimeException {
    public SameOrganizationExistsException() {
        super("Такая организация уже есть");
    }
}
