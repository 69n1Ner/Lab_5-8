package net;

import commands.GetLoggerable;
import commands.SaveCommand;
import main.Invoker;

import java.io.BufferedReader;
import java.io.Closeable;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

public interface Runner extends Messageable, GetLoggerable ,Unique{
    void run();
    void run(boolean isScript, String path);
    Closeable getTunnel();
    default void shutdown(boolean isInterrupt){
        this.setRunning(false);
        Closeable tunnel = this.getTunnel();
        if (tunnel != null) {
            try {
                if (this instanceof UdpServer) {
                    ((SaveCommand) this.getInvokerFather().getAllCommands().get("save")).setSilent(isInterrupt).execute();
                }
                tunnel.close();
            } catch (Exception e) {
                getLogger().error(e);
            }
        }
    }
    void setRunning(boolean condition);
    Invoker getInvokerFather();
}
