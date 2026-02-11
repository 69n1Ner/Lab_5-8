import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // 1 как считать с консоли?
        Scanner sc = new Scanner(System.in);
        while (true){
            String input = sc.nextLine();

            if (input.equals("help")){
                System.out.println("helping...");
                break;
            }
            else {
                System.out.println("wrong line");
            }


        }
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