package Main;

import Commands.Command;
import Commands.HelpCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Invoker {
    private HashMap<String , Command> commandHashMap = new HashMap<>();
    private Container container;
    //todo сделать из стека обычный "последний" InputManager
    private InputManager inputManager;

    public Invoker(Container container){
        this.container = container;
    }

    public void setCommand(Command command){
        this.commandHashMap.put(command.getName(), command);
    }


    public Command defineCommand(String string) throws InvalidInput, NoSuchCommandException{
        this.inputManager = new InputManager(this);
        inputManager.separate(string);
        if (inputManager.isValidCommand(inputManager.getCommand())) { //валидация для команды

            //todo нужна валидация отдельно для команды и аргументов, для каждой команды свой валидатор
            //todo нужны валидации отдельно для единичных аргументов, а также отдельные для RunTime
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
