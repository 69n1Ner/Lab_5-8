package commands;

import exceptions.InvalidInput;
import exceptions.RecursionLimitReached;
import io.Validator;
import main.Invoker;
import net.Request;
import net.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;

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

    @Override
    public Request execute(User user) {
        try {
            String file = getArgument();
            if (file.contains("\"")) {
                file = file.replace("\"", "");
                Command command = this.setArgument(file);
                Validator.isValidArgument(command);
            } else {
                Validator.isValidArgument(this);
            }

            incrementCurrentRecursion();
            Runner runner = getInvokerFather().getRunner();
            runner.run(true, file, runner.isLab7()); // клиент вызовет runScript()
            decrementCurrentRecursion();             // важно: сбрасываем после выполнения
            logger.info("Скрипт {} выполнен", file);

        } catch (InvalidInput | RecursionLimitReached i) {
            logger.warn(i);
        }
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
