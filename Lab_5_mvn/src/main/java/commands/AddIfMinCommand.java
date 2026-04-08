package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import exceptions.SameObjectExistsException;
import io.InputManager;
import io.XmlUtil;
import main.*;
import organization.Organization;

import java.io.IOException;
import java.util.NoSuchElementException;

public class AddIfMinCommand extends Command{

    public AddIfMinCommand(String name, Invoker invoker){
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

                try {
                    container.getById(newOrganization.getId());
                    throw new SameObjectExistsException("Такой объект уже есть");
                } catch (NoSuchElementException e) {

                    boolean seen = false;
                    Organization best = null;
                    for (Organization organization : container
                            .getAll()) {
                        if (!seen || organization.compareTo(best) < 0) {
                            seen = true;
                            best = organization;
                        }
                    }
                    Organization minOrg = seen ? best : null;

                    if (minOrg != null && minOrg.compareTo(newOrganization) > 0) {
                        container.add(container.generateFields(newOrganization, false));
                        System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");
                    }
                }
            } else throw new EmptyContainerException("Список пуст, не с чем сравнивать");

        } catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }

    }

    @Override
    public String describe() {
        return "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции. Сравнение идет по выручке количеству сотрудников";
    }
}
