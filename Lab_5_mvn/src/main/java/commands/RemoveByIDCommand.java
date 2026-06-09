package commands;

import exceptions.InvalidInput;
import exceptions.NoSuchEntityException;
import io.ObjWithFeedback;
import io.Validator;
import db.OrganizationDao;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;

import java.io.Serializable;
import java.util.List;

public class RemoveByIDCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(RemoveByIDCommand.class);

    public RemoveByIDCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.ID_ONLY);
    }


    @Override
    public Request execute(User user) {
        String r = "непредвиденная";
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient){
                return createRequest(this);
            }

            Long ID = Long.parseLong(getArgument());

            OrganizationDao organizationDao = OrganizationDao.getInstance();

            ObjWithFeedback<Boolean> b = organizationDao.delete(ID, user);
            StringBuilder feedback = new StringBuilder();
            boolean isDeleted = b.object();
            List<String> lb = b.feedback();
            if (!lb.isEmpty()){
                for (String s:lb){
                    feedback.append(s);
                }
                return createRequest(feedback.toString());
            }

            String text;
            if (isDeleted){
                text = "Организация с ID "+ID+" успешно удалена";
            }else {
                text = "Организация с ID "+ID+" не удалена удалена";
            }
            logger.info(text);
            r= text;


        } catch (InvalidInput | NoSuchEntityException i){
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
        return "remove_by_id ID : удалить элемент из коллекции по его ID";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
