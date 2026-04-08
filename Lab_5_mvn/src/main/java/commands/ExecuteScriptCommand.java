package commands;

import exceptions.RecursionLimitReached;
import io.InputManager;
import main.Invoker;
import main.Main;

import java.io.IOException;

public class ExecuteScriptCommand extends Command{
    private int currentRecursion = 0;

    public ExecuteScriptCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    public void decrementCurrentRecursion() {
        if (currentRecursion > 0) {
            currentRecursion -= 1;
        }
    }

    public void incrementCurrentRecursion() {
        int recursionLimit = 3;
        if (currentRecursion <= recursionLimit) {
            currentRecursion += 1;
        } else {
            throw new RecursionLimitReached("Достигнут предел рекурсии: " + recursionLimit);
        }
    }

    @Override
    public boolean isValid(InputManager inputManager){
        if (inputManager.getMainArgument() != null){
            return true;
        } else {
            System.err.println("Нет имени файла");
            return false;
        }
    }

    @Override
    public void execute() {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        try {
            incrementCurrentRecursion();
        } catch (RecursionLimitReached r){
            System.err.println(r.getMessage());
            return;
        }

            if (isValid(inputMan)){
                Main.programExecute(invokerFather,inputMan.getMainArgument());
            }

            decrementCurrentRecursion();
    }

    @Override
    public String describe() {
        return "execute_script file_name : считать и исполнить скрипт из указанного файла. Команды которым необходимы на вход организации или адрес должны вводится в xml формате в строке с командой";
    }
}
