package commands;

import exceptions.InvalidInput;
import exceptions.NoSuchOrganizationException;
import io.InputManager;
import io.Validator;
import main.Invoker;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoveByIDCommand extends Command{
    private static final Logger logger = LogManager.getLogger(RemoveByIDCommand.class);

    public RemoveByIDCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.ID_ONLY);
    }


    @Override
    public void execute() {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequest();
            }
            Long ID = Long.parseLong(getArgument());
            getInvokerFather().getContainer().removeById(ID);
            String text = "Организация с ID "+ID+" успешно удалена";
            logger.info(text);
            r= text;
        } catch (InvalidInput | NoSuchOrganizationException i){
            logger.warn(i);
            r= i.getMessage();
        }finally {
            createResponse(r);
        }
    }

    @Override
    public String describe() {
        return "remove_by_id id : удалить элемент из коллекции по его id";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
