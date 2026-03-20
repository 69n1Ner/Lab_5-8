package Commands;

import IO.InputManager;
import MainProg.InvalidInput;
import MainProg.Invoker;

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

    public abstract void execute() throws InvalidInput, IOException;

    public abstract String describe();

    public boolean isValid(InputManager inputManager) throws InvalidInput{
        if (inputManager.getMainArgument() != null || inputManager.getXmlArgument() != null){
            throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
        }
        return true;
    }

    public static boolean isXmlNotValid(InputManager inputManager) throws InvalidInput {
        if (inputManager.getXmlArgument() != null) {
            boolean ERR = inputManager.getXmlArgument().equals("ERR");
            boolean isName = inputManager.getXmlArgument().matches(".*<name>[^<]+</name>.*");
            boolean isDate = inputManager.getXmlArgument().matches(".*<creation_date>[^<]+</creation_date>.*");
            if (isDate && isName && !ERR){
                return false;
            } else {
                throw new InvalidInput("command Неверный XML");
            }
        }

        return true;

    }


//    void unexecute();
}
