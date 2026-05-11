package net;

import java.io.IOException;
import java.net.PortUnreachableException;

public interface Messageable {
    Request receiveMessage() ;
    void sendMessage(Request request) ;
}
