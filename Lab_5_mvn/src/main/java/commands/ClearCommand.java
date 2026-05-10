package commands;

import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClearCommand extends Command{
    private static final Logger logger = LogManager.getLogger(ClearCommand.class);

    public ClearCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public void  execute() {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequest();
            }

            getInvokerFather().getContainer().clear();
            String t = "Коллекция успешно удалена";
            logger.info(t);
            r = t;

        }catch (InvalidInput i){
            logger.warn(i);
            r = i.getMessage();
        }finally {
            createResponse(r);
        }
    }

    @Override
    public String describe() {
        return "clear : очистить коллекцию";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
