package commands;

import io.InputManager;
import exceptions.InvalidInput;
import main.Invoker;

import java.io.IOException;

public abstract class Command {
    private Invoker invokerFather;
    private String name; // непонятно зачем нужно

    public void setName(String name) {
        this.name = name;
    }

    public Invoker getInvokerFather() {
        return invokerFather;
    }

    public void setInvokerFather(Invoker invokerFather) {
        this.invokerFather = invokerFather;
    }

    public String getName() {
        return this.name;
    }

    public abstract void execute() throws IOException;

    public abstract String describe();

    public boolean isValid(InputManager inputManager) throws InvalidInput{
        if (inputManager.getMainArgument() != null || inputManager.getXmlArgument() != null){
            throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValidForScript(InputManager inputManager) throws InvalidInput{
        if (inputManager.getMainArgument() == null){
            if (inputManager.isScript()){
                if (inputManager.getXmlArgument() != null) {
                    return isXmlHasIdAndDate(inputManager);
                } throw new InvalidInput("Команда "+ this.getName() +" должна иметь XML строку при исполнении скрипта");
            } return false;
        } throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
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
