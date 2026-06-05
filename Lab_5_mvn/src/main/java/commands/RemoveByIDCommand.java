package commands;

import exceptions.InvalidInput;
import exceptions.NoSuchOrganizationException;
import io.Validator;
import io.db.OrganizationDao;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

public class RemoveByIDCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(RemoveByIDCommand.class);

    public RemoveByIDCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.ID_ONLY);
    }


    @Override
    public Request execute() {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                return createRequest(this);
            }

            Long ID = Long.parseLong(getArgument());

            OrganizationDao organizationDao = OrganizationDao.getInstance();
            organizationDao.delete(ID);

            String text = "Организация с ID "+ID+" успешно удалена";
            logger.info(text);
            r= text;
        } catch (InvalidInput | NoSuchOrganizationException i){
            logger.warn(i);
            r= i.getMessage();
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
            return createRequest(r);
        }
        return null;
    }

    @Override
    public String describe() {
        return "remove_by_id ID : удалить элемент из коллекции по его ID";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
