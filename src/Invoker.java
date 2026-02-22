import java.util.ArrayList;
import java.util.HashMap;

public class Invoker {
    private HashMap<String ,ICommand> commandHashMap = new HashMap<>();

    public Invoker(){}

    public void setCommand(ICommand command){
        this.commandHashMap.put(command.getName(), command);
    }

    public ICommand defineCommand(String string){
        InputManager inputManager = new InputManager();
        inputManager.separate(string);

        //todo VALIDATOR

        String stringCommand = inputManager.getCommand();

        System.out.println(inputManager.getCommand());

        return commandHashMap.get(stringCommand);
    }
}
