package Commands;

import IO.InputManager;
import IO.XmlUtil;
import MainProg.*;

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

    //todo добавить ошибку на наличие элемента
    @Override
    public boolean isValid(InputManager inputManager) throws NullPointerException {
        try {
            getInvokerFather().getContainer().getById(Long.parseLong(getInvokerFather().getInputManager().getMainArgument()));
            return true;
        } catch (NumberFormatException e) {
            System.err.println("Неверно задан id");
        }
        return false;
    }

    @Override
    public void execute() throws IOException, InvalidInput {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        if (isValid(inputMan)) {
            if (isXmlNotValid(inputMan)) {
                Organization oldOrg = invokerFather.getContainer().getById(Long.parseLong(inputMan.getMainArgument()));
                Organization parametrizedOrg = inputMan.inputOrganization(true);

                invokerFather.getContainer().update(parametrizedOrg,oldOrg);
                System.out.println("~~Организация с ID " + oldOrg.getId() + " успешно изменена~~");
            }else {
                Long ID = Long.parseLong(inputMan.getMainArgument());
                Organization oldOrg = invokerFather.getContainer().getById(ID);
                Organization parametrizedOrg = XmlUtil.readObjectFromString(inputMan.getXmlArgument());
                parametrizedOrg.setId(ID);
                invokerFather.getContainer().update(parametrizedOrg, oldOrg);
                System.out.println("~~Организация с ID " + oldOrg.getId() + " успешно изменена~~");
            }
        }else {
            System.out.println("update Ничего не произошло");
        }

    }

}