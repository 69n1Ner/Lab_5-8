package net;

import commands.GetLoggerable;

import java.net.PortUnreachableException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Messageable extends GetLoggerable {
    Request receiveMessage() ;
    void sendMessage(Request request);

}
