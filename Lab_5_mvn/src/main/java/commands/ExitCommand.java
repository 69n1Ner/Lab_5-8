package commands;

import exceptions.InvalidInput;
import io.Validator;
import main.Invoker;
import net.Request;
import net.Runner;
import net.UdpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.User;
import thread.ThreadServer;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public class ExitCommand extends Command{
    private static final Logger logger = LogManager.getLogger(ExitCommand.class);
    private boolean isInterrupt;

    public ExitCommand(String name, Invoker invoker){
        super(name,invoker,ArgumentType.NO_ARGUMENTS);
        isInterrupt = false;
    }

    @Override
    public Request execute(User user) {
        try {
            Validator.isValidArgument(this);
            Runner runner = getInvokerFather().getRunner();
            runner.setRunning(false);
            runner.getReadPool().shutdown();
            runner.getProcessPool().shutdown();
            runner.getSendPool().shutdown();

            try {
                if (!runner.getReadPool().awaitTermination(1, TimeUnit.SECONDS)) {
                    runner.getReadPool().shutdownNow();
                }
                if (!runner.getProcessPool().awaitTermination(1, TimeUnit.SECONDS)) {
                    runner.getProcessPool().shutdownNow();
                }
                if (!runner.getSendPool().awaitTermination(1, TimeUnit.SECONDS)) {
                    runner.getSendPool().shutdownNow();
                }
            } catch (InterruptedException e) {
                runner.getReadPool().shutdownNow();
                runner.getProcessPool().shutdownNow();
                runner.getSendPool().shutdownNow();
                Thread.currentThread().interrupt();
            }

            if (!isInterrupt){
                return null;
            }

            Closeable tunnel = runner.getTunnel();
            if (tunnel != null) {
                try {
                    if (runner instanceof  UdpServer || runner instanceof ThreadServer) {
                        if (!runner.isLab7()) {
                            runner.getInvokerFather().getAllCommands().get("save").execute(null);
                        }
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
        return null;
    }

    @Override
    public String describe() {
        return "exit : завершить программу";
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
