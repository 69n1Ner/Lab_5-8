package commands;

import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import net.Runner;
import net.UdpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;

public class ExitCommand extends Command{
    private static final Logger logger = LogManager.getLogger(ExitCommand.class);
    private boolean isInterrupt;

    public ExitCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
        isInterrupt = false;
    }

    @Override
    public void execute() {
        try {
            Validator.isValidArgument(this);
            Runner runner = getInvokerFather().getRunner();
            runner.setRunning(false);

            if (!isInterrupt){
                return;
            }

            Closeable tunnel = runner.getTunnel();
            if (tunnel != null) {
                try {
                    if (runner instanceof UdpServer) {
                        runner.getInvokerFather().getAllCommands().get("save").execute();
                    }
                    tunnel.close();
                } catch (Exception e) {
                    getLogger().error(e);
                }finally {
                    logger.info("{} завершил работу", getInvokerFather().getRunner().toString());
                    LogManager.shutdown();
                }
            }

        }catch (InvalidInput i){
            logger.warn(i);

        }
    }

    @Override
    public String describe() {
        return "exit : завершить программу (без сохранения в файл)";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }


    public boolean isInterrupt() {
        return isInterrupt;
    }

    public Command setInterrupt(boolean interrupt) {
        isInterrupt = interrupt;
        return this;
    }



}
