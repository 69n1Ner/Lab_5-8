package commands;

import exceptions.InvalidInput;
import main.Invoker;

public class ExitCommand extends Command{

    public ExitCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute(){
        if (isValid(getInvokerFather().getInputManager())) {
            System.exit(0);
        }

    }

    @Override
    public String describe() {
        return "exit : завершить программу (без сохранения в файл)";
    }
}
