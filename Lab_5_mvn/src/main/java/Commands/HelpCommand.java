package Commands;

import MainProg.Invoker;

public class HelpCommand extends Command {

    public HelpCommand(String name, Invoker invoker){
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public String describe(){
        return "help : Выводит справку по командам.";
    }

    @Override
    public void execute() {
        if (isValid(getInvokerFather().getInputManager())) {
            System.out.println("Доступные команды:\n" +
                    "-------------------------------------------------------------------------------------");
            for (Command command : this.getInvokerFather().allCommands().values()) {
                System.out.println(command.describe());
            }
            System.out.println("-------------------------------------------------------------------------------------");
        }
    }



}
