package commands;

import exceptions.InvalidInput;
import io.InputManager;
import io.ObjWithFeedback;
import io.Validator;
import io.XmlUtil;
import db.OrganizationDao;
import main.*;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import org.apache.logging.log4j.LogManager;
import security.User;

import java.io.Serializable;
import java.util.List;
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
    public Request execute(User user) {
        String response = "непредвиденная";

        try {
            Validator.isValidArgument(this);

            Organization newOrganization;

//            logger.debug("before xmls");
            if ((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()) {
                newOrganization = InputManager.inputOrganization();
//                logger.debug("it's input");
            } else {
//                logger.debug("it's request");
                Validator.isValidForScript(this);
//                logger.debug("it's server");
                newOrganization = XmlUtil.readOrganizationFromString(getXmlArgument());
            }

            if (getInvokerFather().getRunner() instanceof UdpClient){
//                logger.debug("it's client");
                String xmlOrg = XmlUtil.orgToXml(newOrganization);
                Command command = this.setXmlArgument(xmlOrg);
                return createRequest(command);
            }

            StringBuilder feedback = new StringBuilder();
            ObjWithFeedback<Organization> organizationWithFeedback = InputManager.generateOrganizationFields(newOrganization, isScript());
            newOrganization = organizationWithFeedback.object();
            feedback.append(organizationWithFeedback
                    .feedback()
                    .stream()
                    .collect(Collectors.joining("\n","","\n")));

            OrganizationDao organizationDao = OrganizationDao.getInstance();

            ObjWithFeedback<Integer> oId = organizationDao.save(newOrganization, user);
            int id = oId.object();
            logger.debug("id={}",id);
            List<String> loId = oId.feedback();
            if (!loId.isEmpty()){
                logger.debug("loId={}",loId);
                for (String s:loId){
                    feedback.append(s);
                }
                logger.warn(feedback);
                return createRequest(feedback.toString());
            }

            String text = "ID созданной организации: " + id;
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
