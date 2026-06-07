package io;

import commands.Command;
import commands.FilterGreaterThanPostalAddress;
import exceptions.InvalidInput;
import exceptions.NoSuchCommandException;
import main.Invoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Validator {
    private static final Logger logger = LogManager.getLogger(Validator.class);
    private static final List<Character> asciiChars = new ArrayList<>();

    static {
        for (int code = 0; code <= 31; code++) {
            asciiChars.add((char) code);
        }
    }

    //inputMan
    public static boolean isValidInput(String input) {
        String specialSymbols = "!@#$%^&*()+\"';:/?`~№\\=<>[]{}";
        for (int i = 0; i < input.length(); i++) {
            if (specialSymbols.indexOf(input.charAt(i)) != -1) {
                logger.warn("Строка содержит недопустимый символ: {}", input.charAt(i));
                return false;
            }
        }
        if (input.length() > 255) {
            logger.warn("Слишком длинная строка! Максимальная длина 255");
            return false;
        }

        String text = hasSpecialSymbol(input);
        if (text != null){
            logger.warn(text);
            return false;
        }
        return true;
    }

    public static String hasSpecialSymbol(String input){
        if (input == null || input.isEmpty()) {
            return ("Введена пустая строка");
        }
        int charNum = 0;
        for (Character asciiChar : asciiChars) {

            //Ctrl+Z
            if (input.contains("\u001A")) {
                return ("""
                        
                        /﹋\\
                        (҂`_´)
                        ︻╦╤─ ҉ -- - - -- - --
                        /﹋\\
                        """);

                //Ctrl+C (doesn't catch)
            } else if (input.contains(String.valueOf(asciiChar))) {
                String asciiPrint = Integer.toHexString(charNum);
                if (asciiPrint.length() == 1) {
                    asciiPrint = "\\u000" + asciiPrint.toUpperCase();
                } else {
                    asciiPrint = "\\u00" + asciiPrint.toUpperCase();
                }
                return ("Найден спец символ: "+ asciiPrint);
            }

            ++charNum;
        }
        return null;
    }

    //invoker
    public static boolean isCommandExists(String command, Invoker invoker) {
        if (!invoker.contains(command)) {
            return false;
        }
        return Validator.isValidInput(command);
    }


    // ---
    public static void isValidArgument(Command command) throws InvalidInput {
        String argument = command.getArgument();
        String xmlArgument = command.getXmlArgument();
        switch (command.getArgumentType()) {
            case ID -> {
                try {
                    Long.parseLong(argument);

                    return;
                } catch (NumberFormatException e) {
                    throw new InvalidInput("Неверно задан ID");
                }
            }
            case ID_ONLY -> {
                try {
                    Long.parseLong(argument);
                    if (xmlArgument == null || xmlArgument.isEmpty()){
                        return;
                    }else {
                        throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь XML строки");
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidInput("Неверно задан ID");
                }
            } case FILE -> {
                Path path = Path.of(argument);
                if (Files.exists(path)){
                    if(!Files.isDirectory(path)){
                        if (Files.isReadable(path)){
                            if (xmlArgument == null || xmlArgument.isEmpty()){
                                return;
                            }else {
                                throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь XML строки");
                            }
                        }else {
                            throw new InvalidInput("Нет прав на чтение файла");
                        }
                    } else {
                        throw new InvalidInput("Файл - директория");
                    }
                } else {
                    throw new InvalidInput("Файл не найден");
                }
            } case NO_ARGUMENT -> {
                if (argument == null || argument.isEmpty()){
                    return;
                }else {
                    throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь параметров");
                }
            } case NO_ARGUMENTS -> {
                if (argument == null || argument.isEmpty()){
                    if (xmlArgument == null || xmlArgument.isEmpty()){
                        return;
                    }else {
                        throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь XML строки");
                    }
                }else {
                    throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь параметров");
                }
            }
        }
        RuntimeException re = new RuntimeException();
        logger.fatal(re);
        throw re;
    }

    public static void isValidForScript(Command command) throws InvalidInput{
        if (command instanceof FilterGreaterThanPostalAddress) {
            isXmlAddressValid(command);
        } else {
            isXmlOrgValid(command);
        }
    }

    public static void isXmlAddressValid(Command command) throws InvalidInput{
        //todo мб добавить нужную проверку на что либо
//        String xmlArg = command.getXmlArgument();
    }

    public static void isXmlOrgValid(Command command) throws InvalidInput{
            String xmlArgument = command.getXmlArgument();

            if (command.isScript()) {
                if (xmlArgument != null) {
                    boolean ERR = !xmlArgument.equals("ERR");
                    boolean isId = !xmlArgument.matches(".*<id>[^<]+</id>.*");
                    boolean isDate = !xmlArgument.matches(".*<creation_date>[^<]+</creation_date>.*");
                    if (!ERR) {
                        if (isDate) {
                            if (isId) {
                                return;
                            }
                            throw new InvalidInput("XML не имеет ID");
                        }
                        throw new InvalidInput("XML не имеет даты создания");
                    }
                    throw new InvalidInput("Неверная XML строка");
                }else {
                    throw new InvalidInput("Команда должна иметь XML строку");
                }
            }
    }

}