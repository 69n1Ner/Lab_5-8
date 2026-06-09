package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import io.InputManager;
import io.Validator;
import io.XmlUtil;
import db.OrganizationDao;
import main.*;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Address;
import organization.Organization;
import security.User;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class FilterGreaterThanPostalAddress extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(FilterGreaterThanPostalAddress.class);

    public FilterGreaterThanPostalAddress(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENT);
    }

    @Override
    public Request execute(User user) {
        String r;
        try {
            Validator.isValidArgument(this);

            Address address = getAddress();

            if (getInvokerFather().getRunner() instanceof UdpClient){
                String xmlOrg = XmlUtil.adrToXml(address);
                Command command = this.setXmlArgument(xmlOrg);
                return createRequest(command);
            }


            OrganizationDao organizationDao = OrganizationDao.getInstance();
            List<Organization> container = organizationDao.findAll();

            if (!container.isEmpty()) {


                //todo mb add if errors
                InputManager.generateAddressFields(address);
                String s = container.stream()
                        .filter(o -> o.getPostalAddress().compareTo(address) >= 1)
                        .map(Organization::toString).collect(Collectors.joining("\n"));
                if (s.isEmpty()) {
                    String t = "Нет организаций, с большим адресом";
                    r= t;
                    logger.warn(t);
                }
                logger.info(s);
                r= s;
            }else {
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                r= ec.getMessage();
            }
        }catch (InvalidInput e){
            logger.warn(e);
            r= e.getMessage();
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
            return createRequest(r);
        }
        return null;
    }

    private Address getAddress() throws InvalidInput {
        Address address;
        if ((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()) {
            address = InputManager.inputAddress();
        }else {
            Validator.isValidForScript(this);

            address = XmlUtil.readAddressFromString(getXmlArgument());
        }
        return address;
    }

    @Override
    public String describe() {
        return "filter_greater_than_postal_address {postalAddress} : вывести элементы, значение поля postalAddress которых больше заданного. Сравнение идет по почтовому индексу";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
