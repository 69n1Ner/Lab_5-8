package lab_5to8;

import java.util.ArrayList;
import java.util.List;

public class InputManager {
    Invoker invoker;
    private String command;
    private String mainArgument;
    private String xmlArgument;

    //TODO как то обработать ошибки сканера по типу Ctrl+D
    public InputManager(Invoker invoker) {
        this.invoker = invoker;
    }



    public void separate(String input) {
        if (input == null || input.isEmpty()){
            throw new InvalidInput("Пустая команда");
        }
        /* todo <ОТВЕРГНУТО> сделать обработку строки с выбором:
            1. если введена строка только с нужным количеством параметров),
             то пропускать на дальнейшее считывание параметров (интерактивный режим)
            2. если введено больше параметров, то считать этот больший параметр как
             xml текст и обрабатывать по другому
         */

        /* todo переделать так, чтобы ф-я считывала только те слова, которые идут до символа "<",
            далее просто посчитать количество открывающих и закрывающих тегов:
            1. если все ок, то после последнего закрывающего тега смотреть, остались ли элементы
            2. в других случаях ловить ошибки
        */
        int start = 0;
        int end = 0;
        int lt = 0;
        int rt = 0;
        boolean wordFlag = false;
        boolean catchFlag = false;
        boolean xmlPart = false;
        List<String> wordList = new ArrayList<>();
        String word;
        for (int i = 0; i < input.length(); i++) {
            input = input.trim().toLowerCase();
            if (xmlPart){
                if (input.charAt(i) == '<' && input.charAt(i+1) == '/'){
                    rt+=1;
                    continue;
                } else if (input.charAt(i) == '<'){
                    lt+=1;
                    continue;
                }

                if (input.charAt(input.length() - 1) == '>' && lt == rt){
                    this.xmlArgument = input.substring(end);
                } else {
                    this.xmlArgument = "ERR";
                }
                continue;

            }


            if (input.charAt(i) == ' ' && start<i) {
                start = i;
            } else if (input.charAt(i) == '<') {
                xmlPart = true;
                start = i;
                lt+=1;
            } else {
                end = i + 1;
                try {
                    if (input.charAt(end) == ' ') {
                        wordFlag = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    catchFlag = true;
                } finally {
                    if (wordFlag || catchFlag) {
                        word = input.substring(start, end);
                        if (!word.isEmpty())
                        wordList.add(word);
                        start = end+1;
                        wordFlag = false;
                        catchFlag = false;
                    }
                }
            }
        }

        this.command = wordList.get(0);

        if (wordList.size() == 2){
            this.mainArgument = wordList.get(1);
        }
    }

    public boolean isValid(String input){
        String specialSymbols = "!@#$%^&*()+\"';:./?,`~№\\=<>[]{}";
        for (int i =0; i<input.length();i++){
            if (specialSymbols.indexOf(input.charAt(i)) != -1){
                throw new InvalidInput("Строка содержит недопустимый символ: "+ input.charAt(i));
            }
        }

        if (!invoker.contains(input)){
            String commandList = "";        //идея заменить на исполнение HelpCommand не получается
            for (String command: invoker.allCommands()){
                commandList += command + "\n";
            }
           throw new InvalidInput("Неверная команда: "+ input + "\n"+ "Доступные команды:" + "\n" + commandList);
        }

        if (input.length()>255){
            throw new InvalidInput("Слишком длинная строка! Максимальная длина 255");
        }


        return true;
    }

    public boolean isValidCommand(String command) {
        if (!invoker.contains(command)) {
            return false;
        }
        return isValid(command);
    }

    public String separateAttribute(String input) {
        return input.trim();
    }

    public Organization inputOrganization(boolean inputType) {
        if (br == null) {
            br = new BufferedReader(new InputStreamReader(System.in));
        }
        System.out.print("Введите название организации");
        String name = getValueOf(String.class, inputType);
        OrganizationType type = getValueOf(OrganizationType.class);
        Coordinates coordinates = new Coordinates(
                (long) getValueOf(Long.class),
                getValueOf(Double.class)
        );
        Address address = new Address(
                getValueOf(String.class),
                new Location(
                        getValueOf(String.class),
                        getValueOf(Float.class),
                        getValueOf(Integer.class),
                        getValueOf(Integer.class))
        );
        long employeesCount = getValueOf(Long.class);
        int annualTurnover = getValueOf(Integer.class);
        Organization organization = new Organization(
                name,
                annualTurnover,
                coordinates,
                employeesCount,
                address,
                type
        );

        return organization;
    }

    private Object oneMoreTime(Class<?> type) {
        System.out.println("Введите еще раз " + "[" + type.getSimpleName() + "]");
        try {
            String sa = separateAttribute(br.readLine());
            if (isValid(sa)) {
                var method = type.getMethod("valueOf", String.class);
                return method.invoke(null, sa);
            }
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Ошибка " + e.getMessage());
        } catch (InvalidInput e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    private <T> T getValueOf(Class<T> classType){
        return getValueOf(classType,false);
    }

    private <T> T getValueOf(Class<T> classType, boolean inputType) {
        System.out.println(" [" + classType.getSimpleName() + "]");
        try {
            String sa = separateAttribute(br.readLine());
            if (isValid(sa)) {
                var method = classType.getMethod("valueOf", String.class);
                if (classType.isEnum()) {
                    return (T) Enum.valueOf((Class<Enum>) classType, sa);
                }
                return (T) method.invoke(null, sa);
            }
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Ошибка " + e.getMessage());
        } catch (InvalidInput e) {
            if (inputType) {
                return null;
            }
        }
        return null;
    }

//    private String getString(boolean inputType) {
//        System.out.println(" [String]");
//        try {
//            String sa = separateAttribute(br.readLine());
//            if (isValid(sa)) {
//                return sa;
//            }
//        } catch (InvalidInput e) {
//            if (inputType) {
//                return null;
//            }
//            return (String) oneMoreTime(String.class);
//        } catch (IOException e) {
//            System.err.println("Ошибка " + e.getMessage());
//        }
//        return "";
//    }


    public void clear() {
        this.command = null;
        this.mainArgument = null;
        this.xmlArgument = null;
    }

    public String  getMainArgument() {
        return mainArgument;
    }

    public String getXmlArgument() {
        return xmlArgument;
    }

    public String getCommand() {
        return command;
    }
}
