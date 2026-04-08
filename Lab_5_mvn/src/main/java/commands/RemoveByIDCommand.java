package commands;

import exceptions.InvalidInput;
import io.InputManager;
import main.Invoker;

public class RemoveByIDCommand extends Command{

    public RemoveByIDCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public boolean isValid(InputManager inputManager) {
        try {
            getInvokerFather().getContainer().getById(Long.parseLong(getInvokerFather().getInputManager().getMainArgument()));
            return true;
        } catch (NumberFormatException e) {
            System.err.println("Неверно задан ID");
            return false;
        }
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
            if (isValid(inputMan)){
                invokerFather.getContainer().removeById(Long.parseLong(inputMan.getMainArgument()));
                System.out.println("~~Организация успешно удалена~~");
            }


    }

    @Override
    public String describe() {
        return "remove_by_id id : удалить элемент из коллекции по его id";
    }
}
