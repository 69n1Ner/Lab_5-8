package Main;

import Commands.AddCommand;
import Commands.HelpCommand;
import Commands.ShowCommand;
import Commands.UpdateCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Main {
    private TreeSet history = new TreeSet();

    public static void main(String[] args) {

        // 1 как считать с консоли?
        BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        Container<Organization> container = new Container<Organization>();
        Invoker invoker = new Invoker(container);
        invoker.setCommand(new HelpCommand("help",invoker));
        invoker.setCommand(new UpdateCommand("update",invoker));
        invoker.setCommand(new AddCommand("add",invoker));
        invoker.setCommand(new ShowCommand("show",invoker));
        //        lab_5to8.InputManager inputManager = new lab_5to8.InputManager();

//        inputManager.separate("help    help2   ");
//        String s = inputManager.getCommand();
//        System.out.println(s);
//        System.out.println(inputManager.getArguments());

        while (true){
            System.out.print("$user: ");
            try {
                String input = rd.readLine();
                invoker.defineCommand(input).execute();
            }catch (NoSuchCommandException e){
                System.out.println(e.getMessage());
                invoker.allCommands().get("help").execute();
            } catch (RuntimeException|  IOException e){
                System.out.println(e.getMessage());
            }



        }
    }
}

//class lab_5to8.Invoker {
//    private Map<String, lab_5to8.HelpCommand> commandMap = new  HashMap();
//
//    public void registerCommand(String string,lab_5to8.HelpCommand helpCommand){
//
//    }
//}

// lab_5to8.Main
// lab_5to8.Invoker (вызов команд)
// Command (пусть интерфейс, Add, Help...)
// FileProducer, FileValidator(считывание можно через библу, но нельзя валидировать), FileUploader
// Validation
// Model (Cords, lab_5to8.Organization...)
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