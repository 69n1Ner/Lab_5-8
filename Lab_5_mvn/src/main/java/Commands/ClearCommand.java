package Commands;

import IO.InputManager;
import ExceptionsL5.InvalidInput;
import MainProg.Invoker;

public class ClearCommand extends Command{

    public ClearCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        try {

            if (isValid(inputMan)){
                invokerFather.getContainer().clear();
                System.out.println("~~Коллекция успешно удалена~~");
            }
        }catch (InvalidInput e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public String describe() {
        return "clear : очистить коллекцию";
    }
}
