public class HelpCommand extends ICommand{

    public HelpCommand(String name, Invoker invokerFather){
        this.setName(name);
        setInvokerFather(invokerFather);
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
