package net;

import io.ContainerWrapper;
import io.XmlUtil;
import jakarta.xml.bind.annotation.XmlElement;

import java.io.Serializable;
import java.util.UUID;

public record Request (
        //todo можно добавить id request'а и время, чтобы выполнять команды последовательно, а не в хаосе + отсекать по времени повторки
        RequestType requestType,
        boolean hasXml,
        UUID clientId,
        String commandName,
        String argument,
        String xmlArgument,
        ContainerWrapper organizations,
        String errorMsg
        ) implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Request build(UUID id){
        return new Request(null,false,id,null,null,null,null,null);
    }

    public Request setRequestType(RequestType requestType){
        return new Request(requestType,hasXml,clientId,commandName,argument,xmlArgument,organizations,errorMsg);
    }
    public Request setHasXml(boolean hasXml){
        return new Request(requestType,hasXml,clientId,commandName,argument,xmlArgument,organizations,errorMsg);
    }
    public Request setCommandName(String commandName){
        return new Request(requestType,hasXml,clientId,commandName,argument,xmlArgument,organizations,errorMsg);
    }
    public Request setArgument(String argument){
        return new Request(requestType,hasXml,clientId,commandName,argument,xmlArgument,organizations,errorMsg);
    }
    public Request setXmlArgument(String xmlArgument){
        return new Request(requestType,hasXml,clientId,commandName,argument,xmlArgument,organizations,errorMsg);
    }
    public Request setOrganizations(ContainerWrapper organizations){
        return new Request(requestType,hasXml,clientId,commandName,argument,xmlArgument,organizations,errorMsg);
    }
    public Request setErrorMsg(String errorMsg){
        return new Request(requestType,hasXml,clientId,commandName,argument,xmlArgument,organizations,errorMsg);
    }

    @Override
    public String toString() {
        return "Request{" +
                "\nargument='" + argument + '\'' +
                "\n requestType=" + requestType +
                "\n hasXml=" + hasXml +
                "\n clientId=" + clientId +
                "\n commandName='" + commandName + '\'' +
                "\n xmlArgument='" + xmlArgument + '\'' +
                "\n organizations=" + organizations +
                "\n errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
