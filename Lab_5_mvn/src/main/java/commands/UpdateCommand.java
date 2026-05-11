package commands;

import exceptions.*;
import io.InputManager;
import io.Validator;
import io.XmlUtil;
import main.*;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.io.Serializable;

public class UpdateCommand extends Command  implements Serializable {
    private static final Logger logger = LogManager.getLogger(UpdateCommand.class);

    public UpdateCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.ID);
    }

    @Override
    public String describe() {
        return "update id {element} : Обновляет значения элемента по id, нужно ввести весь объект." +
                " Поля без значения не будут изменены.";
    }

    @Override
    public void execute() {
        String r = "непредвиденная";

        try{
            Validator.isValidArgument(this);

            Organization parametrizedOrg;

            if((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()){
                parametrizedOrg = InputManager.inputOrganization(true);
            }else {
                parametrizedOrg = XmlUtil.readOrganizationFromString(getXmlArgument());
            }

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequestWith(parametrizedOrg);
                return;
            }

            Invoker invokerFather = getInvokerFather();
            Container<Organization> container = invokerFather.getContainer();

            if (!container.getAll().isEmpty()) {
                Long ID = Long.parseLong(getArgument());
                Organization oldOrg = container.getById(ID);

                container.update(parametrizedOrg, oldOrg);
                String t = "Организация с ID " + oldOrg.getId() + " успешно изменена";
                logger.info(t);
                r = t;

            } else{
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                r= ec.getMessage();
            }


        }catch (InvalidInput | NoSuchOrganizationException i){
            logger.warn(i);
            r=i.getMessage();
        }finally {
            if (isRequest() && !(getInvokerFather().getRunner() instanceof UdpClient)){
                createResponse(r);
            }
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}