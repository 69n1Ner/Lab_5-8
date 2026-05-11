package commands;

import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Comparator;
import java.util.stream.Collectors;

public class HelpCommand extends Command  implements Serializable {
    private static final Logger logger = LogManager.getLogger(HelpCommand.class);

    public HelpCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public String describe(){
        return "help : Выводит справку по командам.";
    }

    @Override
    public void execute() {
        try {
            Validator.isValidArgument(this);

            logger.info(getInvokerFather()
                    .getAllCommands()
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(Command::getCommandName, String.CASE_INSENSITIVE_ORDER))
                    .map(Describable::describe)
                    .collect(Collectors.joining("\n","\n",""))
            );
            }catch (InvalidInput i){
            logger.warn(i);
        }
    }

    @Override
    public void createRequest(){
        RuntimeException ex = new RuntimeException("Этот метод не должен использоваться");
        getLogger().fatal(ex);
        throw ex;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
