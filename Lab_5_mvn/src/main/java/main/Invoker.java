package main;

import commands.Command;
import exceptions.NoSuchCommandException;
import io.InputManager;
import io.Validator;
import organization.Organization;

import java.util.HashMap;

public class Invoker {
    private final HashMap<String , Command> commandHashMap = new HashMap<>();
    private final Container<Organization> container;
    private InputManager inputManager;

    public Invoker(Container<Organization> container){
        this.container = container;
    }

    public void setCommand(Command command){
        this.commandHashMap.put(command.getCommandName(), command);
    }

    public Command defineCommand(String string, boolean isScript) throws NoSuchCommandException {
        this.inputManager = new InputManager(this, isScript);
        inputManager.separate(string);
        if (Validator.isValidCommand(inputManager.getCommandName(),this)) {
            return commandHashMap
                    .get(inputManager.getCommandName())
                    .setArgument(inputManager.getMainArgument())
                    .setXmlArgument(inputManager.getXmlArgument());
        }
        throw new NoSuchCommandException("Такой команды не существует");
    }


    public boolean contains(String command){
        return this.commandHashMap.get(command) != null;
    }

    public HashMap<String , Command> allCommands(){
        return new HashMap<>(commandHashMap);
    }

    public InputManager getInputManager(){
        return this.inputManager;
    }

    public Container<Organization> getContainer() {
        return container;
    }
}
