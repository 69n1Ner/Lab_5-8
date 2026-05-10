package main;

import commands.*;
import exceptions.NoSuchCommandException;
import io.InputManager;
import io.Validator;
import net.Runner;
import organization.Organization;

import java.util.HashMap;

public class Invoker {
    private final HashMap<String , Command> commandHashMap = new HashMap<>();
    private final Container<Organization> container;
    private InputManager inputManager;
    private Runner runner;

    public Invoker(Container<Organization> container){

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
        this.container = container;
    }

    public void setCommand(Command command){
        this.commandHashMap.put(command.getCommandName(), command);
    }

    public Command defineCommand(String string, boolean isScript) throws NoSuchCommandException {
        this.inputManager = new InputManager();
        inputManager.separate(string);
        if (Validator.isCommandExists(inputManager.getCommandName(),this)) {
            return commandHashMap
                    .get(inputManager.getCommandName())
                    .setArgument(inputManager.getMainArgument())
                    .setXmlArgument(inputManager.getXmlArgument())
                    .setIsScript(isScript);
        }
        throw new NoSuchCommandException();
    }


    public boolean contains(String command){
        return this.commandHashMap.get(command) != null;
    }

    public HashMap<String , Command> getAllCommands(){
        return new HashMap<>(commandHashMap);
    }

    public InputManager getInputManager(){
        return this.inputManager;
    }

    public Container<Organization> getContainer() {
        return container;
    }

    public Runner getRunner() {
        return runner;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }
}
