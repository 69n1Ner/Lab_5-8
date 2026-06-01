package commands;

import exceptions.InvalidInput;
import exceptions.RecursionLimitReached;
import io.Validator;
import main.Invoker;
import net.Request;
import net.UdpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ExecuteScriptCommand extends Command{
    private int currentRecursion = 0;
    private static final Logger logger = LogManager.getLogger(ExecuteScriptCommand.class);

    public ExecuteScriptCommand(String name, Invoker invoker) {
        super(name,invoker,ArgumentType.FILE);
    }

    public void decrementCurrentRecursion() {
        if (currentRecursion > 0) {
            currentRecursion -= 1;
        }
    }

    public void incrementCurrentRecursion() throws RecursionLimitReached {
        int recursionLimit = 3;
        if (currentRecursion <= recursionLimit) {
            currentRecursion += 1;
        } else {
            throw new RecursionLimitReached("Достигнут предел рекурсии: " + recursionLimit);
        }
    }

    //TODO при посылке реквеста не должна посылаться команда, а сразу выполняться и посылать другие команды. Т.е. добавить флаг isScript и в этот execute
    @Override
    public Request execute() {
        String r;
        try {
            Validator.isValidArgument(this);
            incrementCurrentRecursion();


            getInvokerFather().getRunner().run(true,getArgument());
            String t = "Скрипт " +getArgument()+  " выполнен";
            logger.info(t);
            r = t;

        }catch (InvalidInput | RecursionLimitReached i){
            logger.warn(i);
            logger.debug(Arrays.toString(i.getStackTrace()).replace(",","\n"));
            r = i.getMessage();
        }

//        if (isRequest() &&!(getInvokerFather().getRunner() instanceof UdpClient)) {
//            return createRequest(r);
//        }
        return null;
    }

    @Override
    public String describe() {
        return "execute_script file_name : считать и исполнить скрипт из указанного файла. Команды которым необходимы на вход организации или адрес должны вводится в xml формате в строке с командой";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
