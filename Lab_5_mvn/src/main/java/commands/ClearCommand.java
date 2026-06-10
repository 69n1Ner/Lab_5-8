package commands;

import exceptions.InvalidInput;
import io.ObjWithFeedback;
import io.Validator;
import db.OrganizationDao;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;
import thread.ThreadClient;

import java.io.Serializable;
import java.util.List;

public class ClearCommand extends Command implements Serializable {
    private static final Logger logger = LogManager.getLogger(ClearCommand.class);

    public ClearCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
    }

    @Override
    public Request execute(User user) {
        String r;
        try {
            Validator.isValidArgument(this);

            if (getInvokerFather().getRunner() instanceof UdpClient || getInvokerFather().getRunner() instanceof ThreadClient){

                return createRequest(this);
            }

            OrganizationDao organizationDao = OrganizationDao.getInstance();
            ObjWithFeedback<Integer> co = organizationDao.clear(user);
            StringBuilder feedback = new StringBuilder();
            int counter = co.object();
            logger.debug("counter={}",counter);
            List<String> lco = co.feedback();
            boolean isBroke = false;
            if (!lco.isEmpty()){
                for (String s:lco){
                    if (s.equals("")) {
                        isBroke =true;
                        break;
                    };
                    logger.debug("lco={}",lco);

                    feedback.append(s);
                }
                if (!isBroke) return createRequest(feedback.toString());
            }

            String t = "Удалено "+counter+" организаций";
            logger.info(t);
            r = t;

        }catch (InvalidInput i){
            logger.warn(i);
            r = i.getMessage();
        }

        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient || getInvokerFather().getRunner() instanceof ThreadClient)) {

            return createRequest(r);
        }
        return null;
    }

    @Override
    public String describe() {
        return "clear : очистить коллекцию";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
