package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import io.Validator;
import db.OrganizationDao;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;
import security.User;
import thread.ThreadClient;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ShowCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(ShowCommand.class);

    public ShowCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public String describe() {
        return "show : вывести все элементы коллекции";
    }

    @Override
    public Request execute(User user) {
        String r ="непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient || getInvokerFather().getRunner() instanceof ThreadClient){

                return createRequest(this);
            }

            OrganizationDao organizationDao = OrganizationDao.getInstance();
            List<Organization> container = organizationDao.findAll();

            if (!container.isEmpty()) {
                String s = container.stream()
                        .map(Organization::toString)
                        .collect(Collectors.joining("\n"+delimiter+"\n",delimiter+"\n","\n"+delimiter));
                logger.info(s);
                getInvokerFather().getRunner().showUser();
                r = s;
            } else {
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                logger.debug(Arrays.toString(ec.getStackTrace()).replace(",","\n"));
                r = ec.getMessage();
            }
        }catch (InvalidInput i){
            logger.warn(i);
            r = i.getMessage();
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient || getInvokerFather().getRunner() instanceof ThreadClient)) {

            return createRequest(r);
        }
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
