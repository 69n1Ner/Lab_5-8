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
    public boolean isValid(InputManager inputManager) throws InvalidInput {
        try {
            getInvokerFather().getContainer().getById(Long.parseLong(getInvokerFather().getInputManager().getMainArgument()));
            return true;
        } catch (NumberFormatException e) {
            throw new InvalidInput("Неверно задан ID");
        }
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        try {
            if (isValid(inputMan)){
                invokerFather.getContainer().removeById(Long.parseLong(inputMan.getMainArgument()));
                System.out.println("~~Организация успешно удалена~~");
            }
        } catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }

    }

    @Override
    public String describe() {
        return "remove_by_id id : удалить элемент из коллекции по его id";
    }
}
