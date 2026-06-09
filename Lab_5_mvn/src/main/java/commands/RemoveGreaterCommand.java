package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import exceptions.NoSuchEntityException;
import io.InputManager;
import io.ObjWithFeedback;
import io.Validator;
import io.XmlUtil;
import db.OrganizationDao;
import main.*;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;
import security.User;

import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoveGreaterCommand extends Command  implements Serializable {
    private static final Logger logger = LogManager.getLogger(RemoveGreaterCommand.class);

    public RemoveGreaterCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENT);
    }


    @Override
    public Request execute(User user) {
        String r = "непредвиденная";

        try {
            Validator.isValidArgument(this);

            Organization newOrganization;
            if ((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()) {
                newOrganization = InputManager.inputOrganization();
            } else {
                newOrganization = XmlUtil.readOrganizationFromString(getXmlArgument());
            }

            if (getInvokerFather().getRunner() instanceof UdpClient){
                String xmlOrg = XmlUtil.orgToXml(newOrganization);
                Command command = this.setXmlArgument(xmlOrg);
                return createRequest(command);
            }

            OrganizationDao organizationDao = OrganizationDao.getInstance();
            List<Organization> container = organizationDao.findAll();

            if (!container.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                AtomicBoolean isOneDeleted = new AtomicBoolean(false);

                container.forEach(organization -> {
                    if (organization.compareTo(newOrganization) > 0){
                        try {
                            ObjWithFeedback<Boolean> b = organizationDao.delete(organization.getId(), user,true);
                            boolean isDeleted = b.object();
                            List<String> lb = b.feedback();
                            if (!lb.isEmpty()){
                                for (String s:lb){
                                    if (s.isEmpty()) break;

                                    sb.append(s);
                                }
                            }
                            if (isDeleted) {
                                logger.info("Организация с ID {} удалена", organization.getId());
                                sb.append("Организация с ID ").append(organization.getId()).append(" удалена\n");
                                isOneDeleted.set(true);
                            }
                        } catch (NoSuchEntityException ignored) {
                        }
                    }
                });

                if (!isOneDeleted.get()) {
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
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
            return createRequest(r);
        }
        return null;
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
