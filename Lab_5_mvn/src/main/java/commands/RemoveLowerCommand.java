package commands;

import exceptions.EmptyContainerException;
import io.InputManager;
import io.XmlUtil;
import main.Container;
import exceptions.InvalidInput;
import main.Invoker;
import organization.Organization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RemoveLowerCommand extends Command{

    public RemoveLowerCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() throws IOException {
        Invoker invokerFather = getInvokerFather();
        Container<Organization> container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        try {
            if (!container.getAll().isEmpty()) {
                Organization newOrganization;
                if (isNotValidForScript(inputManager)) {
                    newOrganization = inputManager.inputOrganization(false);
                } else {
                    newOrganization = XmlUtil.readObjectFromString(inputManager.getXmlArgument());
                }

                Iterator<Organization> iterator = container.getAll().iterator();
                ArrayList<Organization> resultList = new ArrayList<>(container.getAll());
                int removedCount = 0;

                while (iterator.hasNext()) {
                    Organization org = iterator.next();
                    if (org.compareTo(newOrganization) < 0) {
                        resultList.remove(org);
                        System.out.println("~~Организация с ID " + org.getId() + " удалена~~");
                        removedCount++;
                    }
                }
                container.clear();
                container.addList(resultList);
                if (removedCount == 0) {
                    throw new NoSuchElementException("Нет организаций, меньше заданной");
                }
            }else throw new EmptyContainerException("Список пуст, не с чем сравнивать");
        }catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }
    }

    @Override
    public String describe() {
        return "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный. Сравнение идет по выручке и количеству сотрудников";
    }
}
