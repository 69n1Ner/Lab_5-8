package commands;

import exceptions.InvalidInput;
import io.Validator;
import main.Container;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;

public class InfoCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(InfoCommand.class);

    public InfoCommand(String name, Invoker invoker) {
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

            Container<Organization> container = Container.getInstance();
            String t = String.join("\n",
                    "Информация:",
                    "-Тип:" + Arrays.stream(container
                                    .getClass()
                                    .getDeclaredFields())
                                    .findFirst()
                                    .get()
                                    .getType()
                                    .getSimpleName(),
                    "-Дата создания:" + container.getCreationDate(),
                    "-Количество элементов:" + container.size());
            logger.info(t);
            r= t;
        }catch (InvalidInput i){
            logger.warn(i);
            r= i.getMessage();
        }

        if (isRequest() && !(getInvokerFather().getRunner() instanceof UdpClient)) {
            return createRequest(r);
        }
        return null;
    }

    @Override
    public String describe() {
        return "info : выводит информацию о коллекции (тип, дата инициализации, количество элементов)";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
