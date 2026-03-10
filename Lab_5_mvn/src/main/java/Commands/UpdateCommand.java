package Commands;

import IO.InputManager;
import MainProg.*;

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
            return isXmlValid(getInvokerFather().getInputManager());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Неверно задан id");
        }
    }

    @Override
    public void execute(){
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        if (isValid(inputMan)) {

            //todo где то здесь сделать развилку, где выбирать уже готовый объект от xml
            Organization oldOrg = invokerFather.getContainer().getById(Long.parseLong(inputMan.getMainArgument()));
            Organization parametrizedOrg = inputMan.inputOrganization(true);

            invokerFather.getContainer().update(oldOrg,parametrizedOrg);
            System.out.println("~~Организация с ID " + oldOrg.getId()+ " успешно изменена~~");
        }
    }

}