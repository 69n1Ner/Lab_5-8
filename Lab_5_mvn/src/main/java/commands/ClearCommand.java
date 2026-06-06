package commands;

import exceptions.InvalidInput;
import io.Validator;
import db.OrganizationDao;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

public class ClearCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(ClearCommand.class);

    public ClearCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public Request execute() {
        String r;
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                return createRequest(this);
            }

            OrganizationDao organizationDao = OrganizationDao.getInstance();
            int counter = organizationDao.clear();
            String t = "Коллекция успешно удалена. Удалено "+counter+" организаций";
            logger.info(t);
            r = t;

        }catch (InvalidInput i){
            logger.warn(i);
            r = i.getMessage();
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
            return createRequest(r);
        }
        return null;
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
