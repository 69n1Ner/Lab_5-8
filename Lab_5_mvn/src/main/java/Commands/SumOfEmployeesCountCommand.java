package Commands;

import IO.InputManager;
import MainProg.Container;
import MainProg.InvalidInput;
import MainProg.Invoker;
import MainProg.Organization;

public class SumOfEmployeesCountCommand extends Command {

    public SumOfEmployeesCountCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() throws InvalidInput {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        if (isValid(inputManager)){
            long total= 0;
            for (Object org: container.getAll()){
                total+= ((Organization) org).getEmployeesCount();
            }
            System.out.println("Количество сотрудников во всех организациях: "+ total);
        }
    }

    @Override
    public String describe() {
        return "sum_of_employees_count";
    }
}
