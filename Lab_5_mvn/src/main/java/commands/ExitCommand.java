package commands;

import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExitCommand extends Command{
    private static final Logger logger = LogManager.getLogger(ExitCommand.class);

    public ExitCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public void execute() {
        try {
            Validator.isValidArgument(this);
            String t = "Программа завершила работу";
            logger.info(t);
            System.exit(0);
        }catch (InvalidInput i){
            logger.warn(i);

        }
    }

    @Override
    public String describe() {
        return "exit : завершить программу (без сохранения в файл)";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
