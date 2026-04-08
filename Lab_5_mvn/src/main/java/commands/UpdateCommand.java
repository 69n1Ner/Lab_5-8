package commands;

import exceptions.*;
import io.InputManager;
import io.XmlUtil;
import main.*;
import organization.Organization;

import java.io.IOException;

public class UpdateCommand extends Command {
    public UpdateCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public String describe() {
        return "update id {element} : Обновляет значения элемента по id, нужно ввести весь объект." +
                " Поля без значения не будут изменены.";
    }


    @Override
    public boolean isNotValidForScript(InputManager inputManager) throws InvalidInput{
        if (inputManager.isScript()){
            if (inputManager.getXmlArgument() != null) {
                try {
                    getInvokerFather().getContainer().getById(Long.parseLong(getInvokerFather().getInputManager().getMainArgument()));
                    return false;
                } catch (NumberFormatException e) {
                    throw new InvalidInput("Неверно задан id");
                }
            }throw new InvalidInput("Команда "+ this.getName() +" должна иметь XML строку при исполнении скрипта");
        } return true;
    }

    @Override
    public void execute() throws IOException{
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        Container<Organization> container = invokerFather.getContainer();

        try {
            if (!container.getAll().isEmpty()) {
                Organization oldOrg;
                Organization parametrizedOrg;
                if (isNotValidForScript(inputMan)) {
                    oldOrg = invokerFather.getContainer().getById(Long.parseLong(inputMan.getMainArgument()));
                    parametrizedOrg = inputMan.inputOrganization(true);
                }else {
                    Long ID = Long.parseLong(inputMan.getMainArgument());
                    oldOrg = invokerFather.getContainer().getById(ID);
                    parametrizedOrg = XmlUtil.readObjectFromString(inputMan.getXmlArgument());
                    parametrizedOrg.setId(ID);
                }

                    invokerFather.getContainer().update(parametrizedOrg, oldOrg);
                    System.out.println("~~Организация с ID " + oldOrg.getId() + " успешно изменена~~");

            } else throw new EmptyContainerException("Список пуст, не с чем сравнивать");
        }catch (InvalidInput e){
            System.err.println("!! "+e.getMessage()+" !!");
        }
    }

}