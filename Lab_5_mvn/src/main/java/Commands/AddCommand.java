package Commands;

import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;

import java.io.IOException;
import java.util.NoSuchElementException;

public class AddCommand extends Command{

    public AddCommand(String name,Invoker invoker){
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
            if (isXmlNotValid(inputManager)) {
                Organization newOrganization = inputManager.inputOrganization(false);
                try {
                    container.getById(newOrganization.getId());
                    throw new SameObjectExistsException("Такой объект уже есть");
                } catch (NoSuchElementException e){
                    container.add(container.generateFields(newOrganization,false));

                    System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");
                }

            }else {
                Organization newOrganization = XmlUtil.readObjectFromString(inputManager.getXmlArgument());
                if (newOrganization != null) {
                    if (newOrganization.getId() == null || newOrganization.getCreationDate() == null){
                        throw new InvalidInput("Не указан ID или дата создания объекта");
                    }
                    try {
                        container.getById(newOrganization.getId());
                        throw new SameObjectExistsException("Такой объект уже есть");
                    } catch (NoSuchElementException e){
                        container.add(container.generateFields(newOrganization,false));

                        System.out.println("~~ID созданной организации: " + container.getIdBy(newOrganization) + "~~");
                    }
                }
            }
        }


    }
    @Override
    public String describe() {
        return "add {element} : добавить новый элемент в коллекцию. Поля введенные неверно будут сгенерированы";
    }
}
