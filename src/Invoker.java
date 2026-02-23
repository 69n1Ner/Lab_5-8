import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Invoker {
    private HashMap<String ,ICommand> commandHashMap = new HashMap<>();
    //todo сделать стэк последних InputManager'ов для передачи в isValid()
    private List<InputManager> stack;

    public Invoker(){
        this.stack = new ArrayList<>();
    }

    public void setCommand(ICommand command){
        this.commandHashMap.put(command.getName(), command);
    }

    public ICommand defineCommand(String string) throws InvalidInput{
        InputManager inputManager = new InputManager(this);

        inputManager.separate(string);
        if (inputManager.isValid(inputManager.getCommand())){ //валидация для команды


            //todo нужна валидация отдельно для команды и аргументов, для каждой команды свой валидатор
            //todo нужны валидации отдельно для единичных аргументов, а также отдельные для RunTime
            this.stack.add(inputManager);
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

    public InputManager lastCall(){
        return this.stack.get(this.stack.size() - 1);
    }
}
