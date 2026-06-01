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
    protected final Invoker invoker;
    protected BufferedReader br;
    protected boolean isRunning;
    protected final UUID runnerId = UUID.randomUUID();
    protected boolean isUnreachable = false;
    private boolean silentConnectionError = false;


    protected Runner(int port, Invoker invoker) {
        this.port = port;
        this.invoker = invoker;
    }

    public abstract void connect();
    public abstract void run();
    public abstract void run(boolean isScript, String path);
    public abstract Closeable getTunnel();
    public abstract void setRunning(boolean condition);
    public abstract Invoker getInvokerFather();

    public void ping() throws PortUnreachableException {
        sendAndWait(Request.build().setRequestType(RequestType.PING).setRunnerId(runnerId));
    }

    public void sendAndWait(Request request) {
        long start = System.currentTimeMillis();
        long timeout = 300;
        while (System.currentTimeMillis() - start < timeout) {
            sendMessage(request);
            if (isUnreachable) break;
            try {
                Request response = receiveMessage();
                if (response != null && runnerId.equals(response.runnerId())
                        && response.requestType() == RequestType.PING) {
                    String runner;
                    if (this instanceof UdpServer) {
                        runner = "клиенту # " + request.runnerId();
                    } else runner = "серверу";
                    if (request.requestType() != RequestType.PING) {
                        getLogger().info("Сообщение отправлено {}", runner);
                    }
                    silentConnectionError = false;
                    return;
                }
                Thread.sleep(30);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (request.requestType() != RequestType.PING) runnerNotConnected();
        if (!silentConnectionError) {
            runnerNotConnected();
            silentConnectionError = true;
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
