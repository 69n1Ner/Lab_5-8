package MainProg;

import Commands.Command;
import Exceptions.InvalidInput;
import Exceptions.NoSuchCommandException;
import Exceptions.RecursionLimitReached;
import IO.InputManager;

import java.util.HashMap;

public class Invoker {
    private HashMap<String , Command> commandHashMap = new HashMap<>();
    private Container container;
    private InputManager inputManager;
    private final int recursionLimit = 1;
    private int currentRecursion = 0;

    public Invoker(Container container){
        this.container = container;
    }

    public void setCommand(Command command){
        this.commandHashMap.put(command.getName(), command);
    }

    public void decrementCurrentRecursion() {
        if (currentRecursion > 0) {
            currentRecursion -= 1;
        }
    }

    public void incrementCurrentRecursion() {
        if (currentRecursion < recursionLimit) {
            currentRecursion += 1;
        } else {
            throw new RecursionLimitReached("!! Достигнут предел рекурсии: " + recursionLimit+ " !!");
        }
    }

    public int getCurrentRecursion() {
        return currentRecursion;
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
