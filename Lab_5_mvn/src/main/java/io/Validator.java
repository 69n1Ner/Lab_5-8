package io;

import commands.Command;
import main.Invoker;

import java.nio.file.Files;
import java.nio.file.Path;

public class Validator {

    //inputMan
    public static boolean isValidInput(String input) {
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
    //invoker
    public static boolean isCommandExists(String command, Invoker invoker) {
        if (!invoker.contains(command)) {
            return false;
        }
        return Validator.isValidInput(command);
    }

    //commands
    public static boolean isValidArgument(Command command) {
        String argument = command.getArgument();
        switch (command.getArgumentType()){
            case ID -> {
                try {
                    Long.parseLong(argument);
                } catch (NumberFormatException e) {
                    System.err.println("Неверно задан ID toLogger");
                    return false;
                }
            } case FILE -> {
                Path path = Path.of(argument);
                if (Files.exists(path)){
                    if(Files.isDirectory(path)){
                        if (Files.isReadable(path)){
                            return true;
                        }else {
                            System.err.println("Нет прав на чтение файла toLogger");
                            return false;
                        }
                    } else {
                        System.err.println("Файл - директория toLogger");
                        return false;
                    }
                } else {
                    System.err.println("Файл не найден toLogger");
                    return false;
                }
            } case NO_ARGUMENT -> {
                if (argument == null){
                    return true;
                }else {
                    System.err.println("Команда "+ command.getCommandName() +" не должна иметь параметров toLogger");
                    return false;
                }
            } case NO_ARGUMENTS -> {
                if (argument == null){
                    if (command.getXmlArgument() == null){
                        return true;
                    }else {
                        System.err.println("Команда "+ command.getCommandName() +" не должна иметь XML строки toLogger");
                        return false;
                    }
                }else {
                    System.err.println("Команда "+ command.getCommandName() +" не должна иметь параметров toLogger");
                    return false;
                }
            }
        }
        System.err.println("непредвиденная toLogger");
        return false;
    }

    public static boolean isNotValidForScript(Command command) {
        if (command.getArgument() == null){
            if (command.isScript()){
                if (command.getXmlArgument() != null) {
                    return !isXmlHasNotIdAndDate(command);
                }
                System.err.println("Команда "+ command.getCommandName() +" должна иметь XML строку при исполнении скрипта toLogger");
            } return true;
        }
        System.err.println("Команда "+ command.getCommandName() +" не должна иметь параметров toLogger");
        return false;
    }

    public static boolean isXmlHasNotIdAndDate(Command command) {
        if (command.getXmlArgument() != null) {
            boolean ERR = command.getXmlArgument().equals("ERR");
            boolean isId = command.getXmlArgument().matches(".*<id>[^<]+</id>.*");
            boolean isDate = command.getXmlArgument().matches(".*<creation_date>[^<]+</creation_date>.*");
            if (!ERR) {
                if (isDate) {
                    if (isId) {
                    }//logger.warn("XML не имеет ID");
                    System.err.println("XML не имеет ID");
                }//logger.warn("XML не имеет даты создания");
                System.err.println("XML не имеет даты создания");
            }//logger.warn("Неверная XML строка");
            System.err.println("Неверная XML строка");
        }
        return false;
    }
}

