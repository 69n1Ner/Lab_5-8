package Commands;

import Exceptions.EmptyContainerException;
import IO.InputManager;
import MainProg.Container;
import Exceptions.InvalidInput;
import MainProg.Invoker;
import OrganizationObject.Organization;
import Sorts.SortByType;

import java.util.ArrayList;

public class PrintFieldAscendingTypeCommand extends Command{

    public PrintFieldAscendingTypeCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        try {

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
        }catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }
    }

    @Override
    public String describe() {
        return "print_field_ascending_type : вывести значения поля type всех элементов в порядке возрастания";
    }
}
