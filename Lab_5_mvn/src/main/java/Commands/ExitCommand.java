package Commands;

import Exceptions.InvalidInput;
import MainProg.Invoker;

public class ExitCommand extends Command{

    public ExitCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute(){
        try {
        if (isValid(getInvokerFather().getInputManager())) {
            System.exit(0);
        }
        }catch (InvalidInput e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public String describe() {
        return "exit : завершить программу (без сохранения в файл)";
    }
}
