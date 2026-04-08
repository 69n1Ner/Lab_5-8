package commands;

import io.InputManager;
import main.Container;
import exceptions.InvalidInput;
import main.Invoker;
import organization.Organization;

import java.util.Arrays;

public class InfoCommand extends Command{
    public InfoCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        Container<Organization> container = invokerFather.getContainer();

        try{

            if (isValid(inputMan)){
                System.out.println("-------------------------------------------------------------------------------------");
                //noinspection OptionalGetWithoutIsPresent
                System.out.println("Информация: "+
                        "\n-Тип: "+ Arrays.stream(container.getClass().getDeclaredFields()).findFirst().get().getType().getSimpleName()+
                        "\n-Дата создания: "+container.getCreationDate()+
                        "\n-Количество элементов: "+container.size());
                System.out.println("-------------------------------------------------------------------------------------");
            }
        }catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }
    }

    @Override
    public String describe() {
        return "info : выводит информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}
