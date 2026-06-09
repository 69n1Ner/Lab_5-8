package net;

import commands.GetLoggerable;
import io.InputManager;
import main.Invoker;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import security.User;

import java.io.BufferedReader;
import java.io.Closeable;
import java.net.PortUnreachableException;
import java.util.Map;
import java.util.UUID;

public abstract class Runner implements Messageable, GetLoggerable, Unique {
    protected static final String IP_ADDRESS = "localhost";
    protected static final int ARRAY_SIZE = 65000;
    private static final Logger log = LogManager.getLogger(Runner.class);
    protected final int port;
    protected Invoker invoker;
    protected BufferedReader br;
    protected boolean isRunning;
    protected final UUID runnerId = UUID.randomUUID();
    protected boolean isUnreachable = false;
    private boolean silentConnectionError = false;
    private boolean silentConnection = false;
    protected boolean initialOnlineShowUser = true;
    protected boolean initialRunShowUser = true;
    protected final boolean isLab7;
    protected User user;


    public abstract void connect();
    public abstract void run();
    public abstract void run(boolean isScript, String path, boolean isLab7);
    public abstract Closeable getTunnel();
    public abstract void setRunning(boolean condition);
    public abstract Invoker getInvokerFather();

    protected Runner(int port, Invoker invoker,boolean isLab7) {
        this.port = port;
        this.invoker = invoker;
        this.isLab7 = isLab7;
    }

    public boolean isLab7() {
        return isLab7;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void ping(Request request) throws PortUnreachableException {
        Request request1 = Request.build()
                .setRequestType(RequestType.PING)
                .setRunnerId(request.runnerId())
                .setRequestId(request.requestId());

        sendAndWait(request1);
    }

    public Request sendAndWait(Request request) {
        long start = System.currentTimeMillis();
        long timeout = 1000;
        log.debug("посланный request={}",request);
        sendMessage(request);

        if (this instanceof UdpServer && !this.runnerId.equals(request.requestId())) {
            runnerSentMsg(request);
            return null;
        }

        runnerSentMsg(request);
        while (System.currentTimeMillis() - start < timeout) {
//            logger.debug("msg sent");
//            logger.debug("not unreachable");

            //receiving ping msg
            var response = receiveMessage();
//                logger.debug("before if");
                if (response != null) {
//                    logger.debug("{} {}", runnerId, response.runnerId());

                    //online
                    if (runnerId.equals(response.runnerId())) {
//                        log.debug("condition passed");
                        if (!silentConnection) {
                            serverOnline();
                            silentConnection = true;
                        }
                        silentConnectionError = false;
                        return response;
                    }
                }
//                logger.debug("after if");
            try {
                ///Может возникать ошибка, если время сна здесь будет ниже, чем время сна у сервера
                ///Важно ставить время сна больше (или столько же) чем у сервера
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("таймаут");

        //connection error cause
        silentConnection = false;
        if (request.requestType() != RequestType.PING) {
            runnerNotConnected();
        }
        if (!silentConnectionError) {
            runnerNotConnected();
            silentConnectionError = true;
        }
        return null;
    }

    private void runnerSentMsg(Request request){
        String runner;
        if (this instanceof UdpServer) {
            runner = "клиенту #" + request.user();
        } else runner = "серверу";

//        if (request.requestType() == RequestType.PING) log.debug("Отправлен пинг");

        if (request.requestType() != RequestType.PING) {
            log.info("Сообщение отправлено {}", runner);
        }
    }

    private void serverOnline(){
        if (initialOnlineShowUser) {
            if (isRunning) {
                showUser();
            }
            initialOnlineShowUser = false;
            return;
        }else {
            log.debug("пробел2");
            System.out.println();
        }
        log.info("сервер в сети");
        if (isRunning) {
            showUser();
        }
    }

    public void showUser(){
        System.out.print("$"+this.getUser()+": ");
    }

    private void runnerNotConnected(){
        if (initialOnlineShowUser) {
            initialOnlineShowUser = false;
        }else {
            log.debug("пробел");
            System.out.println();
        }
        String runner;
        if (this instanceof UdpServer) {
            runner = "клиент";
        } else runner = "сервер";
        log.info("{} не подключен к сети", runner);
        if (isRunning) {
            showUser();
        }
    }

    public void applyParams(boolean isServer){
        String level = System.getProperty("log.level");
        Level l = InputManager.parseLevel(level);
        String console = System.getProperty("log.console");
        boolean isConsole = InputManager.parseConsoleLogger(console);
        String file = System.getProperty("log.file");
        boolean isFile = InputManager.parseFile(file);

        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) log;
        LoggerConfig rootLogger = coreLogger.getContext().getConfiguration().getRootLogger();
        rootLogger.setLevel(l);

        Map<String, Appender> appenders = rootLogger.getAppenders();

        String appName;
        if (isServer){
            appName = "Client";
        } else appName = "Server";
        appenders.values().stream()
                .filter(a -> a.getName().startsWith(appName))
                .forEach(a -> rootLogger.removeAppender(a.getName()));

        if (!isFile || !isConsole) {
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
