package Commands;

import Main.InputManager;
import Main.InvalidInput;
import Main.Invoker;

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

    public abstract void execute();

    public abstract String describe();

    public boolean isValid(InputManager inputManager){
        if (inputManager.getMainArgument() != null || inputManager.getXmlArgument() != null){
            throw new InvalidInput("Команда "+ this.getName() +" не должна иметь параметров");
        }
        return true;
    }

    public boolean isXmlValid(InputManager inputManager){
        if ((inputManager.getXmlArgument() == null || !inputManager.getXmlArgument().equals("ERR"))) {
            return true;
        } else {
            throw new InvalidInput("Часть xml задана неверно");
        }
    }


//    void unexecute();
}
