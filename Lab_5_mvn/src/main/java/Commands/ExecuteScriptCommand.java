package Commands;

import Exceptions.InvalidInput;
import Exceptions.RecursionLimitReached;
import IO.InputManager;
import MainProg.Invoker;
import MainProg.Main;

import java.io.IOException;

public class ExecuteScriptCommand extends Command{

    public ExecuteScriptCommand(String name, Invoker invoker) {
        this.setName(name);
        setInvokerFather(invoker);
    }

    @Override
    public boolean isValid(InputManager inputManager) throws InvalidInput {
        if (inputManager.getMainArgument() != null){
            return true;
        } else {
            throw new NullPointerException("Нет имени файла");
        }
    }

    @Override
    public void execute() throws IOException {
        Invoker invokerFather = getInvokerFather();
        InputManager inputMan = invokerFather.getInputManager();
        try {
            invokerFather.incrementCurrentRecursion();
        } catch (RecursionLimitReached r){
            System.err.println(r.getMessage());
            return;
        }

        try {

            if (isValid(inputMan)){
                Main.programExecute(invokerFather,inputMan.getMainArgument());
            }
        }catch (InvalidInput e){
            System.err.println(e.getMessage());
        } finally {
            invokerFather.decrementCurrentRecursion();
        }
    }

    @Override
    public String describe() {
        return "execute_script file_name : считать и исполнить скрипт из указанного файла. Команды которым необходимы на вход организации или адрес должны вводится в xml формате в строке с командой";
    }
}
