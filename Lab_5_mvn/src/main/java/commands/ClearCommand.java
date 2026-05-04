package commands;

import io.InputManager;
import io.Validator;
import main.Invoker;

public class ClearCommand extends Command{

    public ClearCommand(String name, Invoker invoker){
        this.setCommandName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        invokerFather.getContainer().clear();
        System.out.println("~~Коллекция успешно удалена~~");

    }

    @Override
    public String describe() {
        return "clear : очистить коллекцию";
    }
}
