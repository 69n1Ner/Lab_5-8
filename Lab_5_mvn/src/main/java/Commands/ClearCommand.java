package Commands;

import IO.InputManager;
import MainProg.InvalidInput;
import MainProg.Invoker;

public class ClearCommand extends Command{

    public ClearCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() throws InvalidInput {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        if (isValid(inputMan)){
            invokerFather.getContainer().clear();
            System.out.println("~~Коллекция успешно удалена~~");
        }
    }

    @Override
    public String describe() {
        return "clear";
    }
}
