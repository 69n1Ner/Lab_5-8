package Commands;

import ExceptionsL5.InvalidInput;
import MainProg.Invoker;

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
    public void execute() throws NullPointerException {
        try {

        if (isValid(getInvokerFather().getInputManager()) && !getInvokerFather().getContainer().getAll().isEmpty()){
            System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
            for (Object org: getInvokerFather().getContainer().getAll()){
                System.out.println(org.toString());
                System.out.println("____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____-____");
            }
        } else {
            throw new NullPointerException("Пустой контейнер");
        }
        }catch (InvalidInput e){
            System.err.println(e.getMessage());
        }
    }
}
