import java.util.*;



public class Main {
    private TreeSet history = new TreeSet();

    public static void main(String[] args) {

        // 1 как считать с консоли?
        Scanner sc = new Scanner(System.in);
        Invoker invoker = new Invoker();
        InputManager inputManager = new InputManager();

        invoker.setCommand(new HelpCommand("help"));

        inputManager.separate("help help");
        String s = inputManager.getCommand();
        System.out.println(s);
        System.out.println(inputManager.getArguments());

        while (true){
            System.out.print("terminal $user:");
            String input = sc.nextLine();
            invoker.defineCommand(input).execute();



        }
    }
}

//class Invoker {
//    private Map<String, HelpCommand> commandMap = new  HashMap();
//
//    public void registerCommand(String string,HelpCommand helpCommand){
//
//    }
//}

// Main
// Invoker (вызов команд)
// Command (пусть интерфейс, Add, Help...)
// FileProducer, FileValidator(считывание можно через библу, но нельзя валидировать), FileUploader
// Validation
// Model (Cords, Organization...)
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