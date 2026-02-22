import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Invoker {
    private HashMap<String ,ICommand> commandHashMap = new HashMap<>();

    public Invoker(){}

    public void setCommand(ICommand command){
        this.commandHashMap.put(command.getName(), command);
    }

    public ICommand defineCommand(String string) throws InvalidInput{
        InputManager inputManager = new InputManager(this);
        //todo VALIDATOR
        inputManager.separate(string);
        if (inputManager.isValid(inputManager.getCommand())){ //валидация для команды

            //todo exception если команды нет в списке
            //todo нужна валидация отдельно для команды и аргументов, для каждой команды свой валидатор

            return commandHashMap.get(inputManager.getCommand());
        }

       return null;
    }

    public boolean contains(String command){
        if (this.commandHashMap.get(command) != null){
            return true;
        }
        return false;
    }

    public List<String> allCommands(){
        return new ArrayList<>(commandHashMap.keySet());
    }
}
