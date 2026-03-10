package Commands;

import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;

public class AddCommand extends Command{

    public AddCommand(String name,Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public boolean isValid(InputManager inputManager){
        if (inputManager.getMainArgument() != null){
            throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
        }
        return true;
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        if (isValid(inputManager)) {
            if (!isXmlValid(inputManager)) {
                Organization newOrganization = inputManager.inputOrganization(false);
                container.add(container.generateFields(newOrganization));
                System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");

            }else {
                System.out.println("'"+inputManager.getXmlArgument()+"'");
                Organization newOrganization = XmlUtil.readObjectFromString(inputManager.getXmlArgument());
                container.add(container.generateFields(newOrganization));
                System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");

            }
        }
    }
    @Override
    public String describe() {
        return "add";
    }
}
