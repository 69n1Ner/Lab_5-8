package commands;

import io.InputManager;
import exceptions.InvalidInput;
import main.Invoker;

import java.io.IOException;

public abstract class Command {
    private Invoker invokerFather;
    private String commandName;
    private String argument;
    private String xmlArgument;

    public String getArgument() {
        return argument;
    }

    public Command setArgument(String argument) {
        this.argument = argument;
        return this;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getXmlArgument() {
        return xmlArgument;
    }

    public Command setXmlArgument(String xmlArgument) {
        this.xmlArgument = xmlArgument;
        return this;
    }

    public Invoker getInvokerFather() {
        return invokerFather;
    }

    public void setInvokerFather(Invoker invokerFather) {
        this.invokerFather = invokerFather;
    }

    public abstract void execute();

    public abstract String describe();

//    public boolean isValid(InputManager inputManager){
//        if (inputManager.getMainArgument() != null || inputManager.getXmlArgument() != null){
//            System.err.println("Команда "+ this.getCommandName() +" не должна иметь параметров");
//            return false;
//        }
//        return true;
//    }

    public boolean isNotValidForScript(InputManager inputManager) throws InvalidInput{
        if (inputManager.getMainArgument() == null){
            if (inputManager.isScript()){
                if (inputManager.getXmlArgument() != null) {
                    return !isXmlHasIdAndDate(inputManager);
                } throw new InvalidInput("Команда "+ this.getCommandName() +" должна иметь XML строку при исполнении скрипта");
            } return true;
        } throw new InvalidInput("Команда "+ this.getCommandName() +" не должна иметь параметров");
    }

    public static boolean isXmlHasIdAndDate(InputManager inputManager) throws InvalidInput {
        if (inputManager.getXmlArgument() != null) {
            boolean ERR = inputManager.getXmlArgument().equals("ERR");
            boolean isId = inputManager.getXmlArgument().matches(".*<id>[^<]+</id>.*");
            boolean isDate = inputManager.getXmlArgument().matches(".*<creation_date>[^<]+</creation_date>.*");
            if (!ERR) {
                if (isDate) {
                    if (isId) {
                        return true;
                    }throw new InvalidInput("XML не имеет ID");
                }throw new InvalidInput("XML не имеет даты создания");
            }throw new InvalidInput("Неверная XML строка");


        }
        return false;
    }

//    void unexecute();
}
