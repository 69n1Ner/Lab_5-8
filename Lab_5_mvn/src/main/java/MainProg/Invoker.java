package MainProg;

import Commands.Command;
import Exceptions.InvalidInput;
import Exceptions.NoSuchCommandException;
import Exceptions.RecursionLimitReached;
import IO.InputManager;
import OrganizationObject.Organization;

import java.util.HashMap;

public class Invoker {
    private final HashMap<String , Command> commandHashMap = new HashMap<>();
    private final Container<Organization> container;
    private InputManager inputManager;

    public Invoker(Container<Organization> container){
        this.container = container;
    }

    public void setCommand(Command command){
        this.commandHashMap.put(command.getName(), command);
    }

    public Command defineCommand(String string, boolean isScript) throws InvalidInput, NoSuchCommandException {
        this.inputManager = new InputManager(this, isScript);
        inputManager.separate(string);
        if (inputManager.isValidCommand(inputManager.getCommand())) {
            return commandHashMap.get(inputManager.getCommand());
        }
        throw new NoSuchCommandException("Такой команды не существует");
    }


    public boolean contains(String command){
        if (this.commandHashMap.get(command) != null){
            return true;
        }
        return false;
    }

    public HashMap<String , Command> allCommands(){
        return new HashMap<>(commandHashMap);
    }

    public InputManager getInputManager(){
        return this.inputManager;
    }

    public Container getContainer() {
        return container;
    }
}
