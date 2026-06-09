package commands;

import db.OrganizationDao;
import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;

import java.io.Serializable;

public class InfoCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(InfoCommand.class);

    public InfoCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public Request execute(User user) {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                return createRequest(this);
            }


            String t = String.join("\n",
                    "Информация:",
                    "-Тип:" + OrganizationDao.getContainerCollectionName().getSimpleName(),
                    "-Дата создания:" + OrganizationDao.getContainerCreationDate(),
                    "-Количество элементов:" + OrganizationDao.getContainerSize());
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
