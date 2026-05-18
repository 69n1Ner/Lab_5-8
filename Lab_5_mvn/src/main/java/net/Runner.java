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
    void setRunning(boolean condition);
    Invoker getInvokerFather();
}
