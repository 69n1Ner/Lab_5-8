package net;

import commands.GetLoggerable;
import commands.SaveCommand;
import io.InputManager;
import main.Invoker;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.BufferedReader;
import java.io.Closeable;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Map;

public interface Runner extends Messageable, GetLoggerable ,Unique{
    void run();
    void run(boolean isScript, String path);
    Closeable getTunnel();
    void setRunning(boolean condition);
    default void applyParams(){
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
    Invoker getInvokerFather();
}
