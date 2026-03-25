package Commands;

import ExceptionsL5.InvalidInput;
import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;
import OrganizationObject.Organization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RemoveGreaterCommand extends Command {

    public RemoveGreaterCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }


    @Override
    public void execute() throws  IOException {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        try {
            if (!container.getAll().isEmpty()) {
                Organization newOrganization;
                if (!isValidForScript(inputManager)) {
                    newOrganization = inputManager.inputOrganization(false);
                } else {
                    newOrganization = XmlUtil.readObjectFromString(inputManager.getXmlArgument());
                }

                Iterator<Organization> iterator = container.getAll().iterator();
                ArrayList<Organization> resultList = new ArrayList<>(container.getAll());
                int removedCount = 0;

                while (iterator.hasNext()) {
                    Organization org = iterator.next();
                    if (org.compareTo(newOrganization) > 0) {
                        resultList.remove(org);
                        System.out.println("~~Организация с ID " + org.getId() + " удалена~~");
                        removedCount++;
                    }
                }
                container.clear();
                container.addList(resultList);

                if (removedCount == 0) {
                    throw new NoSuchElementException("Нет организаций, больших заданной");
                }
            }else throw new NullPointerException("Список пуст, не с чем сравнивать");
        }catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }
    }

    @Override
    public String describe() {
        return "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный. Сравнение идет по выручке и количеству сотрудников";
    }
}
