package commands;

import exceptions.*;
import io.InputManager;
import io.Validator;
import io.XmlUtil;
import db.OrganizationDao;
import main.*;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.io.Serializable;
import java.util.List;

public class UpdateCommand extends Command  implements Serializable {
    private static final Logger logger = LogManager.getLogger(UpdateCommand.class);

    public UpdateCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.ID);
    }

    @Override
    public String describe() {
        return "update ID {element} : Обновляет значения элемента по ID, нужно ввести весь объект." +
                " Поля без значения не будут изменены.";
    }

    @Override
    public Request execute() {
        String r = "непредвиденная";

        try{
            Validator.isValidArgument(this);

            Organization parametrizedOrg;

            if((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()){
                parametrizedOrg = InputManager.inputOrganization(true);
            }else {
                Validator.isXmlOrgValid(this);

                parametrizedOrg = XmlUtil.readOrganizationFromString(getXmlArgument());
            }

            if (getInvokerFather().getRunner() instanceof UdpClient){
                String xmlOrg = XmlUtil.orgToXml(parametrizedOrg);
                Command command = this.setXmlArgument(xmlOrg);
                return createRequest(command);
            }

            OrganizationDao organizationDao = OrganizationDao.getInstance();
            List<Organization> container = organizationDao.findAll();


            if (!container.isEmpty()) {
                Long ID = Long.parseLong(getArgument());

                boolean isOk = organizationDao.update(parametrizedOrg, ID);
                String t;

                if (isOk) {
                    t = "Организация с ID " + ID + " успешно изменена";
                    logger.info(t);
                    r = t;
                } else {
                    t = "Организация с ID " + ID + " не изменена";
                    logger.info(t);
                    r = t;
                }

            } else{
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                r= ec.getMessage();
            }


        }catch (InvalidInput | NoSuchEntityException i){
            logger.warn(i);
            r=i.getMessage();
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
            return createRequest(r);
        }
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}