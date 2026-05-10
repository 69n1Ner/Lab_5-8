package net;

import java.io.IOException;

public interface Messageable {
    Request receiveMessage() ;
    void sendMessage(Request request) ;
}
