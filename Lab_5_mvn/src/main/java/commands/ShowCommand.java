package commands;

import exceptions.EmptyContainerException;
import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ShowCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(ShowCommand.class);

    public ShowCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public String describe() {
        return "show : вывести все элементы коллекции";
    }

    @Override
    public void execute() {
        //todo пофиксить вывод первой строки
        String r ="непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequest();
                return;
            }

            List<Organization> container = getInvokerFather().getContainer().getAll();

            if (!container.isEmpty()) {
                String s = container.stream()
                        .map(Organization::toString)
                        .collect(Collectors.joining("\n"));
                logger.info(s);
                r = s;
            } else {
                EmptyContainerException ec = new EmptyContainerException();
                logger.warn(ec);
                logger.debug(Arrays.toString(ec.getStackTrace()).replace(",","\n"));
                r = ec.getMessage();
            }
        }catch (InvalidInput i){
            logger.warn(i);
            r = i.getMessage();
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
