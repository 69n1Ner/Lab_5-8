package net;

import commands.Command;

import java.io.Serializable;
import java.util.UUID;

public record Request (
        //todo можно добавить runnerId request'а и время, чтобы выполнять команды последовательно, а не в хаосе + отсекать по времени повторки
        boolean isScript,
        RequestType requestType,
        UUID runnerId,
        String feedback,
        Command command,
        UUID requestId
        ) implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Request build(){
        return new Request(false,null,null,"",null,UUID.randomUUID());
    }

    public Request setRequestType(RequestType requestType){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId);
    }

    public Request setCommand(Command command){
        command = command.setRequest(true);
        return new Request(isScript,requestType, runnerId,feedback,command,requestId);
    }

    public Request setFeedback(String feedback){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId);
    }

    public Request setRunnerId(UUID runnerId){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId);
    }

    public Request setRequestId(UUID requestId){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId);
    }

    @Override
    public String toString() {
        String ret = "Request{" +
                "\n requestType=" + requestType +
                "\n runnerId=" + runnerId +
                "\n feedback='" + feedback + '\'' +
                "\n "+ command;
        if (command() != null){
            ret += "\n "+ command.isRequest();
        }
        ret += "}";
        return ret;
    }
}
