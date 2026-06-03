package commands;

import exceptions.InvalidInput;
import exceptions.NoSuchOrganizationException;
import exceptions.SameOrganizationExistsException;
import io.InputManager;
import io.OrganizationWithFeedback;
import io.Validator;
import io.XmlUtil;
import main.*;
import net.Request;
import net.RequestType;
import net.UdpClient;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import org.apache.logging.log4j.LogManager;

import java.io.Serializable;
import java.util.stream.Collectors;

public class AddCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(AddCommand.class);

    @Override
    public Logger getLogger(){
        return logger;
    }

    public AddCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENT);
    }

    @Override
    public Request execute() {
        String response = "непредвиденная";

        try {
            Validator.isValidArgument(this);

            Invoker invokerFather = getInvokerFather();
            Organization newOrganization;

//            logger.debug("before xmls");
            if ((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()) {
                newOrganization = InputManager.inputOrganization();
//                logger.debug("it's input");
            } else {
//                logger.debug("it's request");
                Validator.isXmlOrgValid(this);
//                logger.debug("it's server");
                newOrganization = XmlUtil.readOrganizationFromString(getXmlArgument());
            }

            if (getInvokerFather().getRunner() instanceof UdpClient){
//                logger.debug("it's client");
                String xmlOrg = XmlUtil.orgToXml(newOrganization);
                Command command = this.setXmlArgument(xmlOrg);
                return createRequest(command);
            }


            Container<Organization> container =  invokerFather.getContainer();
            OrganizationWithFeedback organizationWithFeedback = InputManager.generateOrganizationFields(newOrganization, isScript());
            newOrganization = organizationWithFeedback.organization();
            String feedback = organizationWithFeedback
                    .feedback()
                    .stream()
                    .collect(Collectors.joining("\n","","\n"));
            container.add(newOrganization);
            String text = "ID созданной организации: " + container.getIdBy(newOrganization);
            logger.info(text);
            response = feedback + text;
        }catch (InvalidInput i) {
            logger.warn(i);
            response = i.getMessage();
        }
        logger.debug("isRequest={}",isRequest());
        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
                return createRequest(response);
        }
        return null;
    }

    @Override
    public String describe() {
        return "add {element} : добавить новый элемент в коллекцию. Поля введенные неверно будут сгенерированы";
    }
}
