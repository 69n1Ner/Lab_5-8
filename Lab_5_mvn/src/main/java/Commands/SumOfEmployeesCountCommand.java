package Commands;

import IO.InputManager;
import MainProg.Container;
import ExceptionsL5.InvalidInput;
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
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        try {

            if (isValid(inputManager)){
                long total= 0;
                for (Object org: container.getAll()){
                    total+= ((Organization) org).getEmployeesCount();
                }
                System.out.println("Количество сотрудников во всех организациях: "+ total);
            }
        }catch (InvalidInput e){
        System.err.println(e.getMessage());
        }
    }

    @Override
    public String describe() {
        return "sum_of_employees_count : вывести сумму значений поля employeesCount для всех элементов коллекции";
    }
}
