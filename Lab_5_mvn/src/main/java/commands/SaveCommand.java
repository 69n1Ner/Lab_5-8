package commands;

import io.InputManager;
import io.XmlUtil;
import main.Invoker;
import organization.Organization;

import java.util.ArrayList;

public class SaveCommand extends Command{
    static int counter = 0;
    public SaveCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();


            if (isValid(inputMan)){
                XmlUtil.writeListToFile((ArrayList<Organization>) invokerFather.getContainer().getAll(),"collection"+getCounter()+".xml");
                SaveCommand.addCounter();
            }

    }

    @Override
    public String describe() {
        return "save : сохранить коллекцию в файл";
    }

    public static int getCounter(){
        return counter;
    }

    public static void addCounter(){
        counter+=1;
    }
}
