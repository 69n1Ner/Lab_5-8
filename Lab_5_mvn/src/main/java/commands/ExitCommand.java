package commands;

import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import net.Runner;
import net.UdpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;

public class ExitCommand extends Command{
    private static final Logger logger = LogManager.getLogger(ExitCommand.class);

    public ExitCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public void execute() {
        try {
            Validator.isValidArgument(this);
            System.out.println("\n"+getInvokerFather().getRunner().toString()+" завершил работу");
            Runner runner = getInvokerFather().getRunner();
            runner.shutdown(false);

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
