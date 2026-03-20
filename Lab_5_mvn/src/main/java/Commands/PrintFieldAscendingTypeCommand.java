package Commands;

import IO.InputManager;
import MainProg.Container;
import MainProg.InvalidInput;
import MainProg.Invoker;
import MainProg.Organization;
import Sorts.SortByType;

import java.util.ArrayList;
import java.util.List;

public class PrintFieldAscendingTypeCommand extends Command{

    public PrintFieldAscendingTypeCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() throws InvalidInput {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();
        if (isValid(inputManager)){
            if (!container.getAll().isEmpty()) {
                ArrayList<Organization> sortedOrgs = new ArrayList<>(container.getAll());
                sortedOrgs.sort(new SortByType());
                System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
                for (Organization org: sortedOrgs){
                    System.out.println(org);
                    System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");

                }

            } else throw new NullPointerException("Список пуст, не с чем сравнивать");
        }
    }

    @Override
    public String describe() {
        return "print_field_ascending_type ";
    }
}
