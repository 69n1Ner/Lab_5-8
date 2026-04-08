package Commands;

import Exceptions.EmptyContainerException;
import IO.InputManager;
import MainProg.Container;
import Exceptions.InvalidInput;
import MainProg.Invoker;
import OrganizationObject.Organization;

public class SumOfEmployeesCountCommand extends Command {

    public SumOfEmployeesCountCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        Container<Organization> container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        try {

            if (isValid(inputManager)){
                long total= 0;
                if (!container.getAll().isEmpty()){
                for (Object org: container.getAll()){
                    total+= ((Organization) org).getEmployeesCount();
                }
                System.out.println("Количество сотрудников во всех организациях: "+ total);
                }
                else {
                    throw new EmptyContainerException("Пустой контейнер");
                }
            }
        }catch (InvalidInput e){
        System.err.println("!! "+e.getMessage()+" !!");
        }
    }

    @Override
    public String describe() {
        return "sum_of_employees_count : вывести сумму значений поля employeesCount для всех элементов коллекции";
    }
}
