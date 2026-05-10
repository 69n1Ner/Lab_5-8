package commands;

import exceptions.InvalidInput;
import io.InputManager;
import io.Validator;
import io.XmlUtil;
import main.Invoker;
import net.Request;
import net.RequestType;
import org.apache.logging.log4j.Logger;
import organization.Address;
import organization.Organization;

public abstract class Command implements Executable,Describable,GetLoggerable {
    private final Invoker invokerFather;
    private final String commandName;
    private String argument;
    private String xmlArgument;
    private final ArgumentType argumentType;
    private boolean isScript;

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

    public String basicExecuteError() {
        RuntimeException re = new RuntimeException();
        getLogger().fatal("Непредвиденная ошибка в {}", getCommandName(),re);
        return "Непредвиденная ошибка в" + getCommandName() +" | "+ re;
    }


    protected void createRequest() {
        getInvokerFather().getRunner().sendMessage(
                Request.build()
                        .setRequestType(RequestType.COMMAND)
                        .setCommand(this)
        );
    }

    protected void createRequestWith(Address address) {
        String xmlOrg = XmlUtil.adrToXml(address);
        setXmlArgument(xmlOrg);
        getInvokerFather().getRunner().sendMessage(
                Request.build()
                        .setRequestType(RequestType.COMMAND)
                        .setCommand(this)
        );
    }

    protected void createRequestWith(Organization organization) {
        String xmlOrg = XmlUtil.orgToXml(organization);
        setXmlArgument(xmlOrg);
        getInvokerFather().getRunner().sendMessage(
                Request.build()
                .setRequestType(RequestType.COMMAND)
                .setCommand(this)
        );

    }

    protected void createResponse(String response){
        getInvokerFather().getRunner().sendMessage(Request.build()
                .setFeedback(response)
                .setRequestType(RequestType.FEEDBACK)
        );
    }


//    @Override
//    public void createRequest()  {
//        Logger logger = getLogger();
//
//        Validator.isValidForScript1(this);
//
//
//        if (Validator.isValidForScript1(this)) {
//
//            Organization newOrganization = InputManager.inputOrganization();
//            String xmlOrg = XmlUtil.orgToXml(newOrganization);
//            this.setXmlArgument(xmlOrg);
//
//        } else {
//            if (!Validator.isXmlHasNotIdAndDate(this)) {
//                logger.info("Создан запрос");
//                return Request.build()
//                        .setRequestType(RequestType.COMMAND)
//                        .setCommand(this);
//            }
//        }
//
//        RuntimeException re = new RuntimeException("Ошибка в логике execute команды"+ getCommandName());
//        logger.fatal("Ошибка в логике execute команды {}", getCommandName(),re);
//        throw re;
//    }


    @Override
    public String toString() {
        return commandName+ " "+ argument + " "+ xmlArgument;
    }
}

