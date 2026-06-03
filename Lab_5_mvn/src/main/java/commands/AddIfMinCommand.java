package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import exceptions.NoSuchOrganizationException;
import exceptions.SameOrganizationExistsException;
import io.InputManager;
import io.OrganizationWithFeedback;
import io.Validator;
import io.XmlUtil;
import main.*;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class AddIfMinCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(AddIfMinCommand.class);

    public AddIfMinCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENT);
    }


    @Override
    public Request execute() {
        String response = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            Invoker invokerFather = getInvokerFather();
            Container<Organization> container = invokerFather.getContainer();
            Organization newOrganization;



                if ((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()) {
                    newOrganization = InputManager.inputOrganization();
                } else {
                    Validator.isXmlOrgValid(this);
                    newOrganization = XmlUtil.readOrganizationFromString(getXmlArgument());
                }

                if (getInvokerFather().getRunner() instanceof UdpClient){
                    String xmlOrg = XmlUtil.orgToXml(newOrganization);
                    Command command = this.setXmlArgument(xmlOrg);
                    return createRequest(command);
                }


            if (!container.getAll().isEmpty()) {

                List<Organization> list = container.getAll().stream()
                        .filter(o -> o.compareTo(newOrganization) <= 0)
                        .toList();

                if (list.isEmpty()){
                    OrganizationWithFeedback organizationWithFeedback = InputManager.generateOrganizationFields(newOrganization, isScript());
                    Organization newOrganization1 = organizationWithFeedback.organization();

                    String feedback = organizationWithFeedback
                            .feedback()
                            .stream()
                            .collect(Collectors.joining("\n","","\n"));
                    container.add(newOrganization1);
                    String text = "ID созданной организации: " + container.getIdBy(newOrganization1);
                    logger.info(text);
                    response = feedback + text;
                }else {
                    String text = "Значение введенной организации больше минимальной";
                    logger.info(text);
                    response = text;
                }
            } else{
                EmptyContainerException ex = new EmptyContainerException();
                logger.warn(ex);
                response = ex.getMessage();
            }

        } catch (InvalidInput e){
            logger.warn(e);
            response = e.getMessage();
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
            return createRequest(response);
        }
        return null;
    }

    @Override
    public String describe() {
        return "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции. Сравнение идет по выручке и количеству сотрудников";
    }


    @Override
    public Logger getLogger() {
        return logger;
    }
}
