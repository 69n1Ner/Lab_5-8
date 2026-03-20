package Commands;

import IO.InputManager;
import MainProg.Container;
import MainProg.InvalidInput;
import MainProg.Invoker;
import MainProg.Organization;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.function.Predicate;

public class InfoCommand extends Command{
    public InfoCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() throws InvalidInput {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        Container<Organization> container = invokerFather.getContainer();
        if (isValid(inputMan)){
            System.out.println("Информация: "+
                    "\n-Тип: "+ Arrays.stream(container.getClass().getDeclaredFields()).findFirst().get().getType().getSimpleName()+
                    "\n-Дата создания: "+container.getCreationDate()+
                    "\n-Количество элементов: "+container.size());
        }
    }

    @Override
    public String describe() {
        return "info : выводит информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}
