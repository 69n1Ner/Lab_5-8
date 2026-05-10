package net;

import commands.Command;
import io.InputManager;
import main.Invoker;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.UUID;

public record Request (
        //todo можно добавить id request'а и время, чтобы выполнять команды последовательно, а не в хаосе + отсекать по времени повторки
        boolean isScript,
        RequestType requestType,
        UUID id,
        String feedback,
        Command command
        ) implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Request build(){
        return new Request(false,null,UUID.randomUUID(),null,null);
    }

    public Request setRequestType(RequestType requestType){
        return new Request(isScript,requestType,id,feedback,command);
    }
    public Request setCommand(Command command){
        return new Request(isScript,requestType,id,feedback,command);
    }

    public Request setFeedback(String feedback){
        return new Request(isScript,requestType,id,feedback,command);
    }


    @Override
    public String toString() {
        return "Request{" +
                "\n requestType=" + requestType +
                "\n id=" + id +
                "\n feedback='" + feedback + '\'' +
                "\n "+ command+
                '}';
    }
}
