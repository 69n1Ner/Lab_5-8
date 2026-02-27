package lab_5to8;

public class HelpCommand extends Command {

    public HelpCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public void execute() {
        if (this.isValid(this.getInvokerFather().lastCall())) {
            for (String command : this.getInvokerFather().allCommands()) {
                System.out.println(command);
            }
        }
    }



}
