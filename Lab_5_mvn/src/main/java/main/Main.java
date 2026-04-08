package main;

import commands.*;
import exceptions.*;
import io.XmlUtil;
import organization.Organization;

import java.io.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;


public class Main {
    public static void main(String[] args) {

        // 1 как считать с консоли?

        Container<Organization> container = new Container<>();
        Invoker invoker = new Invoker(container);

        invoker.setCommand(new HelpCommand("help", invoker));
        invoker.setCommand(new UpdateCommand("update", invoker));
        invoker.setCommand(new AddCommand("add", invoker));
        invoker.setCommand(new ShowCommand("show", invoker));
        invoker.setCommand(new InfoCommand("info", invoker));
        invoker.setCommand(new RemoveByIDCommand("remove_by_id", invoker));
        invoker.setCommand(new ClearCommand("clear", invoker));
        invoker.setCommand(new SaveCommand("save", invoker));
        invoker.setCommand(new ExitCommand("exit", invoker));
        invoker.setCommand(new AddIfMinCommand("add_if_min", invoker));
        invoker.setCommand(new RemoveGreaterCommand("remove_greater", invoker));
        invoker.setCommand(new RemoveLowerCommand("remove_lower", invoker));
        invoker.setCommand(new SumOfEmployeesCountCommand("sum_of_employees_count", invoker));
        invoker.setCommand(new FilterGreaterThanPostalAddress("filter_greater_than_postal_address", invoker));
        invoker.setCommand(new PrintFieldAscendingTypeCommand("print_field_ascending_type", invoker));
        invoker.setCommand(new ExecuteScriptCommand("execute_script",invoker));

        String filePath = System.getenv("LAB5_8");
        if (filePath == null) {
            System.err.println("Ошибка, невозможно найти переменную окружения LAB5_8. Поставьте значение LAB5_8 = 'initial_collection.xml'");
        } else {
            try{
            container.addList(XmlUtil.readListFromFile(filePath));
            } catch (XmlUtilException e){
                System.err.println("!! "+e.getMessage()+" !!");
            }
        }

        programExecute(invoker);

    }
    public static void programExecute(Invoker invoker) {
        programExecute(invoker,null);
    }

    public static void programExecute(Invoker invoker, String path) {
        if (path == null) {
            run(invoker, new BufferedReader(new InputStreamReader(System.in, UTF_8)),false);
        } else {
            try (FileInputStream file = new FileInputStream(path)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(file, UTF_8));
                run(invoker, br,true);
            } catch (IOException e) {
                System.err.println("Файл не найден: "+e.getMessage());
            }
        }
    }

    public static void run(Invoker invoker, BufferedReader br, boolean isScript){
        while (true) {
            if (!isScript) {
                System.out.print("$user: ");
            }

            try {
                String input = br.readLine();

                if (isScript) {
                    if (input == null) {
                        System.out.println("~~Файл обработан полностью~~");
                        break;
                    }

                    if (input.trim().isEmpty()) {
                        continue;
                    }
                    //showing what command was
                    System.out.println(input);
                }

                invoker.defineCommand(input, isScript).execute();

            } catch (NoSuchCommandException e) {
                System.err.println("!! " + e.getMessage() + " !!");

                try {
                    invoker.allCommands().get("help").execute();
                } catch ( IOException ex) {
                    System.err.println("!! " + ex.getMessage() + " !!");
                }

            } catch (NoSuchElementException |
                     NullCommandException |
                     SameObjectExistsException |
                     RecursionLimitReached |
                     InvalidInput |
                     EmptyContainerException |
                     XmlUtilException |
                     NoFileNameException |
                     IOException e) {
                System.err.println("!! " + e.getMessage() + " !!");

            } catch (RuntimeException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
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