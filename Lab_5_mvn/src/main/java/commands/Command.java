package commands;

import io.XmlUtil;
import main.Invoker;
import net.Request;
import net.RequestType;
import net.Runner;
import organization.Address;
import organization.Organization;

import java.io.Serializable;
import java.util.UUID;

public abstract class Command implements Executable,Describable,GetLoggerable, Serializable {
    private transient Invoker invokerFather;
    private final String commandName;
    private String argument;
    private String xmlArgument;
    private final ArgumentType argumentType;
    private boolean isScript;
    private boolean isRequest = false;

    protected Command(String commandName,Invoker invoker, ArgumentType argumentType){
        this.commandName = commandName;
        this.invokerFather = invoker;
        this.argumentType = argumentType;
    }

    public String getArgument() {
        return argument;
    }

    public Command setArgument(String argument) {
        if (argument == null || argument.isEmpty()){
            this.argument = "";
            return this;
        }
        this.argument = argument;
        return this;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getXmlArgument() {
        return xmlArgument;
    }

    public Command setXmlArgument(String xmlArgument) {
        if (xmlArgument == null || xmlArgument.isEmpty()){
            this.xmlArgument = "";
            return this;
        }
        this.xmlArgument = xmlArgument;
        return this;
    }

    public boolean isScript() {
        return isScript;
    }

    public Command setIsScript(boolean isScript) {
        this.isScript = isScript;
        return this;
    }

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public Invoker getInvokerFather() {
        return invokerFather;
    }



    protected Request createRequest(String feedback) {
        return Request.build().setRequestType(RequestType.FEEDBACK).setFeedback(feedback);
    }

    protected Request createRequest(Command command) {
        return Request.build().setRequestType(RequestType.COMMAND).setCommand(command);
    }


    @Override
    public String toString() {
        return commandName+ " "+ argument + " "+ xmlArgument;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public Command setRequest(boolean request) {
        isRequest = request;
        return this;
    }

    public Command setInvokerFather(Invoker invokerFather) {
        this.invokerFather = invokerFather;
        return this;
    }
}

