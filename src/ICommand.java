public interface ICommand {
    String getName();
    void execute();
    default boolean isValidWithNoArguments(InputManager inputManager){
        return inputManager.getArguments().isEmpty();
    }
//    void unexecute();
}
