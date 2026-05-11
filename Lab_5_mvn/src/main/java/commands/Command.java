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
    private UUID id;
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

    public void setInvokerFather(Invoker invoker){
        this.invokerFather = invoker;
    }

    public String basicExecuteError() {
        RuntimeException re = new RuntimeException();
        getLogger().fatal("Непредвиденная ошибка в {}", getCommandName(),re);
        return "Непредвиденная ошибка в" + getCommandName() +" | "+ re;
    }


    protected void createRequest() {
        Runner runner = getInvokerFather().getRunner();
        runner.sendAndWait(
                Request.build(runner.getUuid())
                        .setRequestType(RequestType.COMMAND)
                        .setCommand(this)
        ,);
    }
    protected void createRequestWith(Address address) {
        String xmlOrg = XmlUtil.adrToXml(address);
        Runner runner = getInvokerFather().getRunner();

        setXmlArgument(xmlOrg);
            runner.sendMessage(
                    Request.build(runner.getUuid())
                            .setRequestType(RequestType.COMMAND)
                            .setCommand(this)
            );
    }

    protected void createRequestWith(Organization organization) {
        String xmlOrg = XmlUtil.orgToXml(organization);
        getLogger().debug("{} --commandIN",xmlOrg);
        Runner runner = getInvokerFather().getRunner();

        setXmlArgument(xmlOrg);
            runner.sendMessage(
                    Request.build(runner.getUuid())
                    .setRequestType(RequestType.COMMAND)
                    .setCommand(this)
            );
    }

    protected void createResponse(String response) {
        getLogger().debug("before");
        Runner runner = getInvokerFather().getRunner();
        getLogger().debug("{} response", id);
        runner.sendMessage(
                Request.build(id)
                .setFeedback(response)
                .setRequestType(RequestType.FEEDBACK)
        );
    }

    @Override
    public String toString() {
        return commandName+ " "+ argument + " "+ xmlArgument;
    }

    public UUID getId() {
        return id;
    }

    public Command setId(UUID id) {
        this.id = id;
        return this;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public Command setRequest(boolean request) {
        isRequest = request;
        return this;
    }

}

