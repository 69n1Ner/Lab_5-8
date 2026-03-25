package Commands;

import ExceptionsL5.InvalidInput;
import ExceptionsL5.SameObjectExistsException;
import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;
import OrganizationObject.Organization;

import java.io.IOException;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class AddIfMinCommand extends Command{

    public AddIfMinCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public boolean isValid(InputManager inputManager) throws InvalidInput {
        if (inputManager.getMainArgument() != null){
            throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
        }
        return true;
    }

    @Override
    public void execute() throws IOException {
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

                try {
                    container.getById(newOrganization.getId());
                    throw new SameObjectExistsException("Такой объект уже есть");
                } catch (NoSuchElementException e) {

                    Organization minOrg = (Organization) container
                            .getAll().stream()
                            .min(Comparator.naturalOrder())
                            .orElse(null);

                    if (minOrg.compareTo(newOrganization) > 0) {
                        container.add(container.generateFields(newOrganization, false));
                        System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");
                    } else {
                        throw new NoSuchElementException("Нет организаций, меньше заданной");
                    }
                }
            } else throw new NullPointerException("Список пуст, не с чем сравнивать");

        } catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }

    }

    @Override
    public String describe() {
        return "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции. Сравнение идет по выручке количеству сотрудников";
    }
}
