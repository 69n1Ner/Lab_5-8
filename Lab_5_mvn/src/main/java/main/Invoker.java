package main;

import commands.*;
import exceptions.NoSuchCommandException;
import exceptions.NoSuchEntityException;
import io.InputManager;
import io.Validator;
import net.Runner;

import java.util.HashMap;

public class Invoker {
    private final HashMap<String , Command> commandHashMap = new HashMap<>();
    private Runner runner;

    public Invoker(){
        setCommand(new HelpCommand("help", this));
        setCommand(new UpdateCommand("update", this));
        setCommand(new AddCommand("add", this));
        setCommand(new ShowCommand("show", this));
        setCommand(new InfoCommand("info", this));
        setCommand(new RemoveByIDCommand("remove_by_id", this));
        setCommand(new ClearCommand("clear", this));
        setCommand(new ExitCommand("exit", this));
        setCommand(new AddIfMinCommand("add_if_min", this));
        setCommand(new RemoveGreaterCommand("remove_greater", this));
        setCommand(new RemoveLowerCommand("remove_lower", this));
        setCommand(new SumOfEmployeesCountCommand("sum_of_employees_count", this));
        setCommand(new FilterGreaterThanPostalAddress("filter_greater_than_postal_address", this));
        setCommand(new PrintFieldAscendingTypeCommand("print_field_ascending_type", this));
        setCommand(new ExecuteScriptCommand("execute_script",this));
    }

    public void setCommand(Command command){
        this.commandHashMap.put(command.getCommandName(), command);
    }

    public Command defineCommand(String string, boolean isScript) throws NoSuchEntityException {
        InputManager inputManager = new InputManager();
        inputManager.separateCommand(string);
        if (Validator.isCommandExists(inputManager.getCommandName(),this)) {
            Command command = commandHashMap
                                .get(inputManager.getCommandName())
                                .setArgument(inputManager.getMainArgument())
                                .setXmlArgument(inputManager.getXmlArgument())
                                .setIsScript(isScript);
            if (command.isRequest()){
                command = command.setRequest(true);
            }
            return command;
        }
        throw new NoSuchCommandException();
    }


    public boolean contains(String command){
        return this.commandHashMap.get(command) != null;
    }

    public HashMap<String , Command> getAllCommands(){
        return new HashMap<>(commandHashMap);
    }


    public Runner getRunner() {
        return runner;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }
}
