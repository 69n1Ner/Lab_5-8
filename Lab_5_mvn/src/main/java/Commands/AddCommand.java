package Commands;

import ExceptionsL5.InvalidInput;
import ExceptionsL5.SameObjectExistsException;
import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;
import OrganizationObject.Organization;

import java.io.IOException;
import java.util.NoSuchElementException;

public class AddCommand extends Command {

    public AddCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() throws IOException {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        try {

            if (!isValidForScript(inputManager)) {

                Organization newOrganization = inputManager.inputOrganization(false);
                try {
                    container.getById(newOrganization.getId());
                    throw new SameObjectExistsException("Такой объект уже есть");

                } catch (NoSuchElementException e) {
                    container.add(container.generateFields(newOrganization, false));
                    System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");
                }

            } else {
                Organization newOrganization = XmlUtil.readObjectFromString(inputManager.getXmlArgument());
                if (newOrganization != null) {

                    if (newOrganization.getId() == null || newOrganization.getCreationDate() == null) {
                        throw new InvalidInput("Не указан ID или дата создания объекта");
                    }

                    try {
                        container.getById(newOrganization.getId());
                        throw new SameObjectExistsException("Такой объект уже есть");

                    } catch (NoSuchElementException e) {
                        container.add(container.generateFields(newOrganization, false));
                        System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");
                    }
                }
            }
        } catch (InvalidInput e) {
            System.err.println("!! "+e.getMessage()+" !!");
        }
    }

    @Override
    public String describe() {
        return "add {element} : добавить новый элемент в коллекцию. Поля введенные неверно будут сгенерированы";
    }
}
