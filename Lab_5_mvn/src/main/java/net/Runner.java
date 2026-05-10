package net;

import main.Invoker;

public interface Runner extends Messageable{
    void run();
    void run(boolean isScript, String path);
}
