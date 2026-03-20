package Commands;

import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;

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
    public void execute() throws InvalidInput, IOException {
        Invoker invokerFather = getInvokerFather();
        Container container = invokerFather.getContainer();
        InputManager inputManager = invokerFather.getInputManager();

        if (isValid(inputManager)) {
            if (!container.getAll().isEmpty()) {

                if (isXmlNotValid(inputManager)) {
                    Organization newOrganization = inputManager.inputOrganization(false);
                    try {
                        container.getById(newOrganization.getId());
                        throw new SameObjectExistsException("Такой объект уже есть");
                    } catch (NoSuchElementException e) {

                        Organization minOrg = (Organization) container.getAll().stream()
                                .min(Comparator.naturalOrder())
                                .orElse(null);

                        if (minOrg.compareTo(newOrganization) > 0) {
                            container.add(container.generateFields(newOrganization, false));

                            System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");
                        } else throw new InvalidInput("Сравнимая организация больше или равно мин. элемента");
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

                            Organization minOrg = (Organization) container.getAll().stream()
                                    .min(Comparator.naturalOrder())
                                    .orElse(null);

                            if (minOrg.compareTo(newOrganization) > 0) {
                                container.add(container.generateFields(newOrganization, false));

                                System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");
                            } else throw new InvalidInput("Сравнимая организация больше или равно мин. элемента");
                        }
                    }
                }
            } else throw new NullPointerException("Список пуст, не с чем сравнивать");
        }

    }

    @Override
    public String describe() {
        return "add_if_min";
    }
}
