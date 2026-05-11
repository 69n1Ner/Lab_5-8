package commands;

import exceptions.InvalidInput;
import exceptions.NoSuchOrganizationException;
import exceptions.SameOrganizationExistsException;
import io.InputManager;
import io.Validator;
import io.XmlUtil;
import main.*;
import net.UdpClient;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import org.apache.logging.log4j.LogManager;

import java.io.Serializable;

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
    public void execute() {
        String response = "непредвиденная";

        try {
            Validator.isValidArgument(this);

            Invoker invokerFather = getInvokerFather();
            Organization newOrganization;

            if ((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()) {
                newOrganization = InputManager.inputOrganization();
            } else {
                Validator.isXmlOrgValid(this);
                newOrganization = XmlUtil.readOrganizationFromString(invokerFather.getInputManager().getXmlArgument());
            }
//---
            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequestWith(newOrganization);
                return;
            }

            Container<Organization> container =  invokerFather.getContainer();

            container.add(InputManager.generateOrganizationFields(newOrganization, isScript()));
            String text = "ID созданной организации: " + container.getIdBy(newOrganization);
            logger.info(text);
            response = text;

        }catch (InvalidInput i){
            logger.warn(i);
            response = i.getMessage();
        }finally {
            if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
                createResponse(response);
            }
        }
    }

    @Override
    public String describe() {
        return "add {element} : добавить новый элемент в коллекцию. Поля введенные неверно будут сгенерированы";
    }
}
