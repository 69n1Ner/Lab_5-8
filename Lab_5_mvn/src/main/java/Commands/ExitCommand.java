package Commands;

import MainProg.InvalidInput;
import MainProg.Invoker;

public class ExitCommand extends Command{

    public ExitCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() throws InvalidInput {
        if (isValid(getInvokerFather().getInputManager())) {
            System.exit(0);
        }
    }

    @Override
    public String describe() {
        return "exit";
    }
}
