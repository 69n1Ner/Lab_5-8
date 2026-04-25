package net;

import java.io.IOException;

public interface Messagable {
    void sendMessage(Request msg) throws IOException;
    //todo изменить ошибку, добавиьт проверку
    Request receiveMessage() throws IOException, ClassNotFoundException;
}
