package IO;

import Exceptions.InvalidInput;
import Exceptions.NullCommandException;
import MainProg.*;
import OrganizationObject.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.Math.abs;

public class InputManager {
    private Invoker invoker;
    private String command;
    private String mainArgument;
    private String xmlArgument;
    private BufferedReader br;
    private boolean isScript;
    private List<Character> asciiChars = new ArrayList<>();

    public InputManager(Invoker invoker,boolean isScript) {
        this.invoker = invoker;
        this.isScript = isScript;
        for (int code = 0; code <= 31; code++) {
            asciiChars.add((char) code);
        }
    }


    public void separate(String input) {


        if (input == null || input.isEmpty()) {
            throw new NullCommandException("Пустая строка");
        }

        int charNum = 0;
        for (Character asciiChar: asciiChars){

            //Ctrl+Z
            if (input.contains("\u001A")){
                throw new NullCommandException("""
                    
                    /﹋\\
                    (҂`_´)
                    ︻╦╤─ ҉ -- - - -- - --
                    /﹋\\
                    """);

                //Ctrl+C (doesn't catch)
            } else if (input.contains(String.valueOf(asciiChar))){
                String asciiPrint =Integer.toHexString(charNum);
                if (asciiPrint.length() == 1){
                    asciiPrint = "\\u000" + asciiPrint.toUpperCase();
                } else {
                    asciiPrint ="\\u00" + asciiPrint.toUpperCase();
                }
                throw new NullCommandException("Найден спец символ: "+ asciiPrint);
            }

            ++charNum;
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
            if (xmlPart) {
                if (input.charAt(i) == '<' && input.charAt(i + 1) == '/') {
                    rt += 1;
                    continue;
                } else if (input.charAt(i) == '<') {
                    lt += 1;
                    continue;
                }

                if (input.charAt(input.length() - 1) == '>' && lt == rt) {
                    this.xmlArgument = input.substring(end);
                    isScript = true;
                } else {
                    this.xmlArgument = "ERR";
                }
                continue;

            }
            if (input.charAt(i) == ' ' && start < i) {
                start = i;
            } else if (input.charAt(i) == '<') {
                xmlPart = true;
                start = i;
                lt += 1;
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
                        start = end + 1;
                        wordFlag = false;
                        catchFlag = false;
                    }
                }
            }
        }

        this.command = wordList.get(0);

        if (wordList.size() >= 2) {
            this.mainArgument = wordList.get(1);
        }
    }
    public boolean isValid(String input) throws InvalidInput {
        String specialSymbols = "!@#$%^&*()+\"';:/?`~№\\=<>[]{}";
        for (int i = 0; i < input.length(); i++) {
            if (specialSymbols.indexOf(input.charAt(i)) != -1) {
                throw new InvalidInput("Строка содержит недопустимый символ: " + input.charAt(i));
            }
        }
        if (input.length() > 255) {
            throw new InvalidInput("Слишком длинная строка! Максимальная длина 255");
        }
        return true;
    }

    public boolean isValidCommand(String command) throws InvalidInput {
        if (!invoker.contains(command)) {
            return false;
        }
        return isValid(command);
    }

    public String separateAttribute(String input) {
        return input.trim();
    }

    public Organization inputOrganization(boolean isUpdate) throws InvalidInput, IOException {
        if (br == null) {
            br = new BufferedReader(new InputStreamReader(System.in));
        }
        System.out.print("Введите название организации");
        String name = getValueOf(String.class, isUpdate);

        System.out.print("Введите тип организации");
        OrganizationType type = getValueOf(OrganizationType.class, isUpdate);

        System.out.println("Введите координаты организации");
        //todo недоработка
        System.out.print("Координата x (максимум 623)");
        Long xC = getValueOf(Long.class, isUpdate);

        System.out.print("Координата y");
        Double yC = getValueOf(Double.class, isUpdate);
        Coordinates coordinates = new Coordinates(xC,yC);


        Address address = inputAddress();

        System.out.print("Введите количество сотрудников");
        Long employeesCount = getValueOf(Long.class, isUpdate,isUpdate);

        System.out.print("Введите годовую выручку");
        Integer annualTurnover = getValueOf(Integer.class, isUpdate,isUpdate);

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

    public Address inputAddress() throws IOException, InvalidInput {
        if (br == null) {
            br = new BufferedReader(new InputStreamReader(System.in));
        }
        System.out.println("Введите адрес");
        System.out.print("Почтовый индекс (минимум 4 символа)");
        String zip = getZipCode();
        System.out.print("Название города");
        String city = getValueOf(String.class, false);
        System.out.print("Координата x");
        Float xL = getValueOf(Float.class, false);
        System.out.print("Координата y");
        Integer yL = getValueOf(Integer.class, false);
        System.out.print("Координата z");
        Integer zL = getValueOf(Integer.class, false);

        return new Address( zip,new Location(city,xL,yL,zL));
    }

    private <T> T oneMoreTime(Class<T> type, boolean positive) throws InvalidInput {
        System.out.println("Введите еще раз " + "[" + type.getSimpleName() + "]");
        if (type.isEnum()) {
            for (OrganizationType en: OrganizationType.values()) {
            }
        }

        try {
            String sa = separateAttribute(br.readLine());
            if (isValid(sa)) {
                if (type == String.class){
                    return (T) sa;
                }
                var method = type.getMethod("valueOf", String.class);
                if (type.isEnum()) {
                    return (T) Enum.valueOf((Class<Enum>) type, sa);
                }
                Number number = (Number) type.getMethod("valueOf", String.class).invoke(null,sa);
                if (positive && (number.doubleValue() <= 0)){
                    return null;
                }

                return (T) method.invoke(null, sa);
            }

        } catch (IllegalArgumentException |
                 InvocationTargetException e){
            return null;

        }
        catch (IOException |
               NoSuchMethodException |
               IllegalAccessException  e) {
            e.printStackTrace();

        }catch (RuntimeException e){
            e.printStackTrace();
        }

        return null;
    }



    private <T> T getValueOf(Class<T> classType, boolean isUpdate) throws InvalidInput {
        return getValueOf(classType,isUpdate,false);
    }

    private <T> T getValueOf(Class<T> classType, boolean isUpdate, boolean positive) throws InvalidInput {
        System.out.println(" [" + classType.getSimpleName() + "]");
        if (classType.isEnum()) {
            for (OrganizationType en: OrganizationType.values()) {
                System.out.println(en.name());
            }
        }

        try {
            String sa = separateAttribute(br.readLine());
            if (isValid(sa)) {
                if (classType == String.class && !sa.isEmpty()){
                    return (T) sa;
                }
                var method = classType.getMethod("valueOf", String.class);

                if (classType.isEnum()) {
                    return (T) Enum.valueOf((Class<Enum>) classType, sa);
                }
                Number number = (Number) classType.getMethod("valueOf", String.class).invoke(null,sa);
                if (positive && (number.doubleValue() <= 0)){
                    return !isUpdate ? oneMoreTime(classType,true) : null;
                }

                return (T) method.invoke(null, sa);
            }

        }catch (InvalidInput e) {
                if (isUpdate) {
                    return null;
                }

        } catch (IOException |
                 NullPointerException |
                 NoSuchMethodException |
                 IllegalAccessException |
                 InvocationTargetException |
                 IllegalArgumentException e) {
            return !isUpdate ? oneMoreTime(classType,positive) : null;
        }

        return null;
    }

    private String getZipCode() throws IOException, InvalidInput {
        System.out.println(" [String]");
        String sa = separateAttribute(br.readLine());
        if (isValid(sa) && sa.length() >=4){
            return sa;
        }else{
            return oneMoreTimeZipCode();
        }
    }

    private String oneMoreTimeZipCode() throws IOException, InvalidInput {
        System.out.println("Введите еще раз " + "[String]");
        String sa = separateAttribute(br.readLine());
        if (isValid(sa) && sa.length() >=4){
            return sa;
        }else{
            return null;
        }
    }


    public void clear() {
        this.command = null;
        this.mainArgument = null;
        this.xmlArgument = null;
    }

    public String getMainArgument() {
        return mainArgument;
    }

    public String getXmlArgument() {
        return xmlArgument;
    }

    public String getCommand() {
        return command;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public boolean isScript() {
        return isScript;
    }
}
