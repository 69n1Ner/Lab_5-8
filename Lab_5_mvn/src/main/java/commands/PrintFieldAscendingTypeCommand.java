package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import io.Validator;
import main.Container;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;
import sorts.SortByType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PrintFieldAscendingTypeCommand extends Command implements Serializable {
    private final static Logger logger = LogManager.getLogger(PrintFieldAscendingTypeCommand.class);

    public PrintFieldAscendingTypeCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public Request execute() {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                return createRequest(this);
            }

            Invoker invokerFather = getInvokerFather();
            Container<Organization> container = invokerFather.getContainer();
            if (!container.getAll().isEmpty()) {
                ArrayList<Organization> sortedOrgs = new ArrayList<>(container.getAll());
                String s = sortedOrgs.stream()
                                .sorted(new SortByType())
                                .map(Organization::toString)
                                .collect(Collectors.joining("\n"));
                logger.info(s);
                r= s;

            } else {
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                r= ec.getMessage();
            }
        }catch (InvalidInput i){
            logger.warn(i);
            r= i.getMessage();
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
            return createRequest(r);
        }
        return null;
    }

    @Override
    public String describe() {
        return "print_field_ascending_type : вывести значения поля type всех элементов в порядке возрастания";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
