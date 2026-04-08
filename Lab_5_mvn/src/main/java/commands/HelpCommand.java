package commands;

import exceptions.InvalidInput;
import main.Invoker;

import java.util.Comparator;

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
    public void execute(){

        if (isValid(getInvokerFather().getInputManager())) {
            System.out.println("-------------------------------------------------------------------------------------");
            for (Command command : this.getInvokerFather()
                    .allCommands()
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(Command::getName,String.CASE_INSENSITIVE_ORDER))
                    .toList()) {
                System.out.println(command.describe());
            }
            System.out.println("-------------------------------------------------------------------------------------");
        }

    }



}
