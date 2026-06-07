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
import sorts.SortByType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrintFieldAscendingTypeCommand extends Command implements Serializable {
    private final static Logger logger = LogManager.getLogger(PrintFieldAscendingTypeCommand.class);

    public PrintFieldAscendingTypeCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public Request execute(User user) {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                return createRequest(this);
            }

            OrganizationDao organizationDao = OrganizationDao.getInstance();
            List<Organization> container = organizationDao.findAll();

            if (!container.isEmpty()) {
                ArrayList<Organization> sortedOrgs = new ArrayList<>(container);
                String s = sortedOrgs.stream()
                                .sorted(new SortByType())
                                .map(Organization::toString)
                                .collect(Collectors.joining("\n"));
                logger.info(s);
                r= s;

            } else {
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                r= ec.getMessage();
            }
        }catch (InvalidInput i){
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
        return "print_field_ascending_type : вывести значения поля type всех элементов в порядке возрастания";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
