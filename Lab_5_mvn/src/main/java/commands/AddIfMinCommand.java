package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import exceptions.NoSuchOrganizationException;
import exceptions.SameOrganizationExistsException;
import io.InputManager;
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

                    if (getInvokerFather().getRunner() instanceof UdpClient){
                        return createRequest(this);
                    }
                    newOrganization = XmlUtil.readOrganizationFromString(getXmlArgument());
                }



            if (!container.getAll().isEmpty()) {

                List<Organization> list = container.getAll().stream()
                        .filter(o -> o.compareTo(newOrganization) <= 0)
                        .toList();

                if (list.isEmpty()){
                    container.add(InputManager.generateOrganizationFields(newOrganization,isScript()));
                    String text = "ID созданной организации: " + container.getIdBy(newOrganization);
                    logger.info(text);
                    response = text;
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
