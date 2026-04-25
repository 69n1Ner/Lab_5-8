package net;

import java.io.IOException;

public interface Connectable {
    void connect() throws IOException;
    void reconnect();
}
