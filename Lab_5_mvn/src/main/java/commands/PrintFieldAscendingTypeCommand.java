package commands;

import exceptions.EmptyContainerException;
import io.InputManager;
import main.Container;
import exceptions.InvalidInput;
import main.Invoker;
import organization.Organization;
import sorts.SortByType;

import java.util.ArrayList;

public class PrintFieldAscendingTypeCommand extends Command{

    public PrintFieldAscendingTypeCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        Container<Organization> container = invokerFather.getContainer();
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

                } else throw new EmptyContainerException("Список пуст, не с чем сравнивать");
            }

    }

    @Override
    public String describe() {
        return "print_field_ascending_type : вывести значения поля type всех элементов в порядке возрастания";
    }
}
