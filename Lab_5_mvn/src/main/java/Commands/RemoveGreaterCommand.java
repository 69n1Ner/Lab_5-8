package Commands;

import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RemoveGreaterCommand extends Command {

    public RemoveGreaterCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }


    @Override
    public void execute() throws InvalidInput, IOException {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        if (isValid(inputManager)) {
            if (!container.getAll().isEmpty()) {
                Organization newOrganization;
                if (isXmlNotValid(inputManager)) {
                    newOrganization = inputManager.inputOrganization(false);
                } else {
                    newOrganization = XmlUtil.readObjectFromString(inputManager.getXmlArgument());
                }

                Iterator<Organization> iterator = container.getAll().iterator();
                int removedCount = 0;

                while (iterator.hasNext()) {
                    Organization org = iterator.next();
                    if (org.compareTo(newOrganization) > 0) {
                        iterator.remove();
                        System.out.println("Организация с ID " + org.getId() + " удалена");
                        removedCount++;
                    }
                }

                if (removedCount == 0) {
                    throw new NoSuchElementException("Нет организаций, больших заданной");
                }
            } else throw new NullPointerException("Список пуст, не с чем сравнивать");
        }
    }

    @Override
    public String describe() {
        return "remove_greater";
    }
}
