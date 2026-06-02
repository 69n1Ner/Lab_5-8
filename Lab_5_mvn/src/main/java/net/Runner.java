package net;

import commands.GetLoggerable;
import io.InputManager;
import main.Invoker;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.BufferedReader;
import java.io.Closeable;
import java.net.PortUnreachableException;
import java.util.Map;
import java.util.UUID;

public abstract class Runner implements Messageable, GetLoggerable, Unique {
    protected static final String IP_ADDRESS = "localhost";
    protected static final int ARRAY_SIZE = 65000;
    protected final int port;
    protected static Logger logger;
    protected Invoker invoker;
    protected BufferedReader br;
    protected boolean isRunning;
    protected final UUID runnerId = UUID.randomUUID();
    protected boolean isUnreachable = false;
    private boolean silentConnectionError = false;
    private boolean silentConnection = false;

    public abstract void connect();
    public abstract void run();
    public abstract void run(boolean isScript, String path);
    public abstract Closeable getTunnel();
    public abstract void setRunning(boolean condition);
    public abstract Invoker getInvokerFather();

    protected Runner(int port, Invoker invoker) {
        this.port = port;
        this.invoker = invoker;
    }

    public void ping(UUID runnerId) throws PortUnreachableException {
        if (this instanceof UdpClient) {
            sendAndWait(Request.build().setRequestType(RequestType.PING).setRunnerId(runnerId));
        } else {
            sendMessage(Request.build().setRequestType(RequestType.PING).setRunnerId(runnerId));
        }
    }

    public void sendAndWait(Request request) {
        long start = System.currentTimeMillis();
        long timeout = 200;
        sendMessage(request);
        if (this instanceof UdpServer) {
            runnerSentMsg(request);
            return;
        }
        while (System.currentTimeMillis() - start < timeout) {
//            logger.debug("msg sent");
//            logger.debug("not unreachable");
            //receiving ping msg
            var response = receiveMessage();
//                logger.debug("before if");
                if (response != null) {
//                    logger.debug("{} {}", runnerId, response.runnerId());
                    if (runnerId.equals(response.runnerId())) {
                        if (!silentConnection) {
                            logger.info("сервер/клиент в сети");
                            silentConnection = true;
                        }
                        runnerSentMsg(request);
                        silentConnectionError = false;
                        return;
                    }
                }
//                logger.debug("after if");
            try {
                Thread.sleep(50);
                sendMessage(request);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //connection error cause
        silentConnection = false;
        if (request.requestType() != RequestType.PING) runnerNotConnected();
        if (!silentConnectionError) {
            runnerNotConnected();
            silentConnectionError = true;
        }
    }

    private void runnerSentMsg(Request request){
        String runner;
        if (this instanceof UdpServer) {
            runner = "клиенту # " + request.runnerId();
        } else runner = "серверу";

        if (request.requestType() != RequestType.PING) {
            logger.info("Сообщение отправлено {}", runner);
        }
    }

    private void runnerNotConnected(){
        String runner;
        if (this instanceof UdpServer) {
            runner = "клиент";
        } else runner = "сервер";
        getLogger().info("{} не подключен к сети1", runner);
    }

    public void applyParams(){
        String level = System.getProperty("log.level");
        Level l = InputManager.parseLevel(level);
        String console = System.getProperty("log.console");
        boolean isConsole = InputManager.parseConsole(console);
        String file = System.getProperty("log.file");
        boolean isFile = InputManager.parseFile(file);


        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) this.getLogger();
        LoggerConfig rootLogger = coreLogger.getContext().getConfiguration().getRootLogger();
        rootLogger.setLevel(l);

        if (!isFile || !isConsole) {
            Map<String, Appender> appenders = rootLogger.getAppenders();

            if (!isFile) {
                appenders.values().stream()
                        .filter(a -> a.getName().startsWith("File"))
                        .forEach(a -> rootLogger.removeAppender(a.getName()));
            }

            if (!isConsole) {
                appenders.values().stream()
                        .filter(a -> a.getName().startsWith("Console"))
                        .forEach(a -> rootLogger.removeAppender(a.getName()));
            }
        }

        coreLogger.getContext().updateLoggers();
    }
}
