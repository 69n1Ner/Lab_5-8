package commands;

import io.InputManager;
import io.Validator;
import main.Invoker;

public class ClearCommand extends Command{

    public ClearCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {

            Invoker invoker = getInvokerFather();

            if (Validator.isValid(invoker)){
                invoker.getContainer().clear();
                System.out.println("~~Коллекция успешно удалена~~");
            }

    }

    @Override
    public String describe() {
        return "clear : очистить коллекцию";
    }
}
