package commands;

import exceptions.InvalidInput;
import io.InputManager;
import io.Validator;
import main.Container;
import main.Invoker;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfoCommand extends Command{
    private static final Logger logger = LogManager.getLogger(InfoCommand.class);

    public InfoCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public void execute() {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                createRequest();
            }

            Invoker invokerFather = getInvokerFather();
            Container<Organization> container = invokerFather.getContainer();
            String t = String.join("\n",
                    "Информация:",
                    "-Тип:" + Arrays.stream(container
                                    .getClass()
                                    .getDeclaredFields())
                            .findFirst()
                            .map(Field::getType)
                            .map(Class::getSimpleName),
                    "-Дата создания:" + container.getCreationDate(),
                    "-Количество элементов:" + container.size());
            logger.info(t);
            r= t;
        }catch (InvalidInput i){
            logger.warn(i);
            r= i.getMessage();
        }finally {
            createResponse(r);
        }
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
