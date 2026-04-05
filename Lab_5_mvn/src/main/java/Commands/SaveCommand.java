package Commands;

import IO.InputManager;
import IO.XmlUtil;
import Exceptions.InvalidInput;
import MainProg.Invoker;
import OrganizationObject.Organization;

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

        try {

            if (isValid(inputMan)){
                XmlUtil.writeListToFile((ArrayList<Organization>) invokerFather.getContainer().getAll(),"collection"+getCounter()+".xml");
                SaveCommand.addCounter();
            }
        }catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
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
