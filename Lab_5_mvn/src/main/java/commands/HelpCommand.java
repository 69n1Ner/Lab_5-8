package commands;

import io.Validator;
import main.Invoker;

import java.util.Comparator;

public class HelpCommand extends Command {

    public HelpCommand(String name, Invoker invoker){
        this.setCommandName(name);
        setInvokerFather(invoker);
        setArgumentType(ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public String describe(){
        return "help : Выводит справку по командам.";
    }

    @Override
    public void execute(){

        if (Validator.isValidArgument(this)) {
            System.out.println("-------------------------------------------------------------------------------------");
            for (Command command : this.getInvokerFather()
                    .allCommands()
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(Command::getCommandName,String.CASE_INSENSITIVE_ORDER))
                    .toList()) {
                System.out.println(command.describe());
            }
            System.out.println("-------------------------------------------------------------------------------------");
        }

    }



}
