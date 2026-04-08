package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import main.Invoker;

public class ShowCommand extends Command{

    public ShowCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public String describe() {
        return "show : вывести все элементы коллекции";
    }

    @Override
    public void execute() {
        //todo пофиксить вывод первой строки
        if (isValid(getInvokerFather().getInputManager()) && !getInvokerFather().getContainer().getAll().isEmpty()){
            boolean hatFlag = true;
            for (Object org: getInvokerFather().getContainer().getAll()){
                if (hatFlag) {
                    System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
                    hatFlag = false;
                }
                System.out.println(org.toString());
                System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
            }
        } else {
            throw new EmptyContainerException("Пустой контейнер");
        }

    }
}
