package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import io.InputManager;
import io.Validator;
import io.XmlUtil;
import main.*;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.util.NoSuchElementException;

public class RemoveGreaterCommand extends Command {
    private static final Logger logger = LogManager.getLogger(RemoveGreaterCommand.class);

    public RemoveGreaterCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENT);
    }


    @Override
    public void execute() {
        String r = "непредвиденная";

        try {
            Validator.isValidArgument(this);

            Organization newOrganization;
            if (getXmlArgument() == null) {
                newOrganization = InputManager.inputOrganization();
            } else {
                newOrganization = XmlUtil.readOrganizationFromString(getXmlArgument());
            }

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequestWith(newOrganization);
            }

            Invoker invokerFather = getInvokerFather();
            Container<Organization> container = invokerFather.getContainer();

            if (!container.getAll().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                boolean isOneDeleted = container.removeIf(organization -> {
                    if (organization.compareTo(newOrganization) > 0){
                        logger.info("Организация с ID {} удалена",organization.getId());
                        sb.append("Организация с ID ").append(organization.getId()).append(" удалена\n");
                        return true;
                    }
                    return false;
                });
                if (!isOneDeleted) {
                    NoSuchElementException ns = new NoSuchElementException("Нет организаций, больших заданной");
                    logger.warn(ns);
                    r= ns.getMessage();
                }else r = sb.toString().strip();
            }else {
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                r =ec.getMessage();
            }
        }catch (InvalidInput e){
            logger.warn(e);
            r = e.getMessage();
        }finally {
            createResponse(r);
        }
    }

    @Override
    public String describe() {
        return "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный. Сравнение идет по выручке и количеству сотрудников";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
