package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import exceptions.XmlUtilException;
import io.InputManager;
import io.Validator;
import io.XmlUtil;
import main.*;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Address;
import organization.Organization;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class FilterGreaterThanPostalAddress extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(FilterGreaterThanPostalAddress.class);

    public FilterGreaterThanPostalAddress(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENT);
    }

    @Override
    public void execute() {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            Invoker invokerFather = getInvokerFather();
            Address address;

            if ((getXmlArgument() == null || getXmlArgument().isEmpty()) && !isScript()) {
                address = InputManager.inputAddress();
            }else {
                address = XmlUtil.readAddressFromString(getXmlArgument());
            }

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequestWith(address);
                return;
            }

            List<Organization> container = invokerFather.getContainer().getAll();

            if (!container.isEmpty()) {


                //todo mb add if errors
                InputManager.generateAddressFields(address);
                String s = container.stream()
                        .filter(o -> o.getPostalAddress().compareTo(address) >= 1)
                        .map(Organization::toString).collect(Collectors.joining("\n"));
                if (s.isEmpty()) {
                    String t = "Нет организаций, с большим адресом";
                    logger.warn(t);
                    r= t;
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
        }finally {
            if (isRequest() && !(getInvokerFather().getRunner() instanceof UdpClient)){
                createResponse(r);
            }
        }
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
