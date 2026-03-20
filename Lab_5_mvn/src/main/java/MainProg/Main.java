package MainProg;

import Commands.*;
import IO.XmlUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Main {
    public static void main(String[] args) {

        // 1 как считать с консоли?
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Container<Organization> container = new Container<>();
        Invoker invoker = new Invoker(container);

        invoker.setCommand(new HelpCommand("help",invoker));
        invoker.setCommand(new UpdateCommand("update",invoker));
        invoker.setCommand(new AddCommand("add",invoker));
        invoker.setCommand(new ShowCommand("show",invoker));
        invoker.setCommand(new InfoCommand("info",invoker));
        invoker.setCommand(new RemoveByIDCommand("remove_by_id",invoker));
        invoker.setCommand(new ClearCommand("clear",invoker));
        invoker.setCommand(new SaveCommand("save",invoker));
        invoker.setCommand(new ExitCommand("exit",invoker));
        invoker.setCommand(new AddIfMinCommand("add_if_min",invoker));
        invoker.setCommand(new RemoveGreaterCommand("remove_greater",invoker));
        invoker.setCommand(new RemoveLowerCommand("remove_lower",invoker));
        invoker.setCommand(new SumOfEmployeesCountCommand("sum_of_employees_count",invoker));
        invoker.setCommand(new FilterGreaterThanPostalAddress("filter_greater_than_postal_address",invoker));
        invoker.setCommand(new PrintFieldAscendingTypeCommand("print_field_ascending_type",invoker));

        String filePath= System.getenv("LAB5_8");
        if (filePath == null){
            System.err.println("Ошибка, невозможно найти переменную окружения LAB5_8 ");
        } else {
            container.addList(XmlUtil.readListFromFile(filePath));
        }
        //todo считывание с файла


        while (true){
            System.out.print("$user: ");

            try {
                String input = br.readLine();
                invoker.defineCommand(input).execute();
            }catch (NoSuchCommandException e){
                System.err.println(e.getMessage());

                try {
                    invoker.allCommands().get("help").execute();
                } catch (InvalidInput ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            } catch (NoSuchElementException |
                     SameObjectExistsException |
                     NullPointerException |
                     InvalidInput |
                     IOException e){
                System.err.println(e.getMessage());


            } catch (RuntimeException e){
                e.printStackTrace();

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