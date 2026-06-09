package net;

import commands.Command;
import security.User;

import java.io.Serializable;
import java.util.UUID;

public record Request (
        //todo можно добавить runnerId request'а и время, чтобы выполнять команды последовательно, а не в хаосе + отсекать по времени повторки

        /// it's used twice: to define is it script AND to define is it a registration for a new user
        boolean isScript,
        RequestType requestType,
        UUID runnerId,
        String feedback,
        Command command,
        UUID requestId,
        User user
        ) implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Request build(){
        return new Request(false,null,null,"",null,UUID.randomUUID(),null);
    }

    public Request setRequestType(RequestType requestType){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId,user);
    }

    public Request setCommand(Command command){
        command = command.setRequest(true);
        return new Request(isScript,requestType, runnerId,feedback,command,requestId,user);
    }

    public Request setFeedback(String feedback){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId,user);
    }

    public Request setRunnerId(UUID runnerId){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId,user);
    }

    public Request setRequestId(UUID requestId){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId,user);
    }

    public Request setUser(User user){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId,user);
    }

    public Request setRegistration(boolean isScript){
        return new Request(isScript,requestType, runnerId,feedback,command,requestId,user);
    }

    @Override
    public String toString() {
        String ret = "Request{" +
                "\n requestType=" + requestType +
                "\n runnerId=" + runnerId +
                "\n feedback='" + feedback + '\'' +
                "\n command="+ command;
        if (command() != null){
            ret += "\n "+ command.isRequest();
        }
        ret += "\n user="+user;
        ret += "}";
        return ret;
    }
}
