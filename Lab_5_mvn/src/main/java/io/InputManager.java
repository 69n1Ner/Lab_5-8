package io;

import exceptions.NullCommandException;
import main.*;
import organization.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class InputManager {
    private final Invoker invoker;
    private String command;
    private String mainArgument;
    private String xmlArgument;
    private BufferedReader br;
    private boolean isScript;
    private final List<Character> asciiChars = new ArrayList<>();

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
    public boolean isValid(String input) {
        String specialSymbols = "!@#$%^&*()+\"';:/?`~№\\=<>[]{}";
        for (int i = 0; i < input.length(); i++) {
            if (specialSymbols.indexOf(input.charAt(i)) != -1) {
                System.err.println("Строка содержит недопустимый символ: " + input.charAt(i));
                return false;
            }
        }
        if (input.length() > 255) {
            System.err.println("Слишком длинная строка! Максимальная длина 255");
            return false;
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

    public Organization inputOrganization(boolean isUpdate) throws IOException {
        if (br == null) {
            br = new BufferedReader(new InputStreamReader(System.in));
        }
        System.out.print("Введите название организации");
        String name = getValueOf(String.class, isUpdate).toString();

        System.out.print("Введите тип организации");
        OrganizationType type = (OrganizationType) getValueOf(OrganizationType.class, isUpdate);

        System.out.println("Введите координаты организации");
        //todo недоработка
        System.out.print("Координата x (максимум 623)");
        Long xC = (Long) getValueOf(Long.class, isUpdate);

        System.out.print("Координата y");
        Double yC = (Double) getValueOf(Double.class, isUpdate);
        Coordinates coordinates = new Coordinates(xC,yC);


        Address address = inputAddress(isUpdate);

        System.out.print("Введите количество сотрудников");
        Long employeesCount = (Long) getValueOf(Long.class, isUpdate,true);

        System.out.print("Введите годовую выручку");
        Integer annualTurnover = (Integer) getValueOf(Integer.class, isUpdate,true);

        return new Organization(
                name,
                annualTurnover,
                coordinates,
                employeesCount,
                address,
                type
        );
    }

    public Address inputAddress() throws IOException {
        return inputAddress(false);
    }

    public Address inputAddress(boolean isUpdate) throws IOException {
        if (br == null) {
            br = new BufferedReader(new InputStreamReader(System.in));
        }
        System.out.println("Введите адрес");
        System.out.print("Почтовый индекс (минимум 4 символа)");
        String zip = getZipCode(isUpdate);
        System.out.print("Название города");
        String city = getValueOf(String.class, isUpdate).toString();
        System.out.print("Координата x");
        Float xL = (Float) getValueOf(Float.class, isUpdate);
        System.out.print("Координата y");
        Integer yL = (Integer) getValueOf(Integer.class, isUpdate);
        System.out.print("Координата z");
        Integer zL = (Integer) getValueOf(Integer.class, isUpdate);

        return new Address( zip,new Location(city,xL,yL,zL));
    }

    private <T> Object oneMoreTime(Class<T> type, boolean positive) {
        System.out.println("Введите еще раз " + "[" + type.getSimpleName() + "]");
        if (type.isEnum()) {
            for (OrganizationType en: OrganizationType.values()) {
                System.out.println(en.name());
            }
        }

        try {
            String sa = separateAttribute(br.readLine());
            if (isValid(sa)) {
                if (type == String.class) {
                    return sa;
                }
                var method = type.getMethod("valueOf", String.class);
                if (type.isEnum()) {
                    //noinspection unchecked,rawtypes
                    return Enum.valueOf((Class<Enum>) type, sa);
                }
                Number number = (Number) type.getMethod("valueOf", String.class).invoke(null, sa);
                if (positive && (number.doubleValue() <= 0)) {
                    return null;
                }

                return method.invoke(null, sa);
            } else{
                return null;
            }
        } catch (IllegalArgumentException |
                 InvocationTargetException e){
            return null;

        } catch (IOException |
               NoSuchMethodException |
               IllegalAccessException |
               RuntimeException e) {
            System.out.println(Arrays.toString(e.getStackTrace()) + e.getMessage());

        }

        return null;
    }



    private <T> Object getValueOf(Class<T> classType, boolean isUpdate){
        return getValueOf(classType,isUpdate,false);
    }

    private <T> Object getValueOf(Class<T> classType, boolean isUpdate, boolean positive){
        System.out.println(" [" + classType.getSimpleName() + "]");
        if (classType.isEnum()) {
            for (OrganizationType en: OrganizationType.values()) {
                System.out.println(en.name());
            }
        }

        try {
            String sa = separateAttribute(br.readLine());
            if (isValid(sa)) {
                if (classType == String.class && !sa.isEmpty()) {
                    return sa;
                }
                var method = classType.getMethod("valueOf", String.class);

                if (classType.isEnum()) {
                    //noinspection unchecked,rawtypes
                    return Enum.valueOf((Class<Enum>) classType, sa);
                }
                Number number = (Number) classType.getMethod("valueOf", String.class).invoke(null, sa);
                if (positive && (number.doubleValue() <= 0)) {
                    return !isUpdate ? oneMoreTime(classType, true) : null;
                }

                return method.invoke(null, sa);
            } else {
                return !isUpdate ? oneMoreTime(classType,positive) : null;
            }

        } catch (IOException |
                 NullPointerException |
                 NoSuchMethodException |
                 IllegalAccessException |
                 InvocationTargetException |
                 IllegalArgumentException e) {

            return !isUpdate ? oneMoreTime(classType,positive) : null;
        }

    }

    private String getZipCode(boolean isUpdate) throws IOException {
        System.out.println(" [String]");
        String sa = separateAttribute(br.readLine());
        if (isValid(sa) && sa.length() >=4){
            return sa;
        }else{
            return !isUpdate? oneMoreTimeZipCode() : null;
        }
    }

    private String oneMoreTimeZipCode() throws IOException {
        System.out.println("Введите еще раз " + "[String]");
        String sa = separateAttribute(br.readLine());
        if (isValid(sa) && sa.length() >=4){
            return sa;
        }else{
            return null;
        }
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



    public boolean isScript() {
        return isScript;
    }
}
