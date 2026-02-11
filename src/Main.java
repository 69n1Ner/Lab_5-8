import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

class InputManager{

}

public class Main {
    private Map history = new HashMap();

    public static void main(String[] args) {

        // 1 как считать с консоли?
        Scanner sc = new Scanner(System.in);
        Invoker invoker = new Invoker();

        invoker.registerCommand("help", new HelpCommand("help"));


        Main main  = new Main();
        while (true){
            String commandName




        }
    }
}

class Invoker {
    private Map<String, HelpCommand> commandMap = new  HashMap();

    public void registerCommand(String string,HelpCommand helpCommand){

    }
}

// Main
// Invoker (вызов команд)
// Command (пусть интерфейс, Add, Help...)
// FileProducer, FileValidator(считывание можно через библу, но нельзя валидировать), FileUploader
// Validation
// Model (Cords, StudyGroup...)
//

/// ///HELP
// 1 Вывести меню действий
// 2 Ввод пользователя
// 3 Обработка (валидация) ввода пользователя
// -- Создать объект команды (?)
// -- вероятно (!) поместить в список истории команд

// 4 (команда верная) -->
//
//                      -- Ввод name-a
//                                          --> корректно
//                      -- Исполнить результат
//                                          --> некорректно
//                      -- Выводим ошибку (ошибку лучше хранить в файле)

/// ///