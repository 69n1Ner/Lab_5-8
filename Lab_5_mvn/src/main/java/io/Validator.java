package io;

import commands.Command;
import commands.FilterGreaterThanPostalAddress;
import exceptions.InvalidInput;
import main.Invoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

public class Validator {
    private static final Logger logger = LogManager.getLogger(Validator.class);

    //inputMan
    public static boolean isValidInput(String input) {
        String specialSymbols = "!@#$%^&*()+\"';:/?`~№\\=<>[]{}";
        for (int i = 0; i < input.length(); i++) {
            if (specialSymbols.indexOf(input.charAt(i)) != -1) {
                logger.warn("Строка содержит недопустимый символ: {}", input.charAt(i));
                System.err.println("Строка содержит недопустимый символ: " + input.charAt(i));
                return false;
            }
        }
        if (input.length() > 255) {
            logger.warn("Слишком длинная строка! Максимальная длина 255");
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

//    commands
//    public static boolean isValidArgument(Command command) throws InvalidInput {
//        String argument = command.getArgument();
//        switch (command.getArgumentType()) {
//            case ID -> {
//                try {
//                    Long.parseLong(argument);
//                    return true;
//                } catch (NumberFormatException e) {
//                    throw new InvalidInput("Неверно задан ID");
//                }
//            }
//            case ID_ONLY -> {
//                try {
//                    Long.parseLong(argument);
//                    if (command.getXmlArgument() == null){
//                        return true;
//                    }else {
//                        throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь XML строки");
//                    }
//                } catch (NumberFormatException e) {
//                    throw new InvalidInput("Неверно задан ID");
//                }
//            } case FILE -> {
//                Path path = Path.of(argument);
//                if (Files.exists(path)){
//                    if(Files.isDirectory(path)){
//                        if (Files.isReadable(path)){
//                            if (command.getXmlArgument() == null){
//                                return true;
//                            }else {
//                                throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь XML строки");
//                            }
//                        }else {
//                            throw new InvalidInput("Нет прав на чтение файла");
//                        }
//                    } else {
//                        throw new InvalidInput("Файл - директория");
//                    }
//                } else {
//                    throw new InvalidInput("Файл не найден");
//                }
//            } case NO_ARGUMENT -> {
//                if (argument == null){
//                    return true;
//                }else {
//                    throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь параметров");
//                }
//            } case NO_ARGUMENTS -> {
//                if (argument == null){
//                    if (command.getXmlArgument() == null){
//                        return true;
//                    }else {
//                        throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь XML строки");
//                    }
//                }else {
//                    throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь параметров");
//                }
//            }
//        }
//        RuntimeException re = new RuntimeException();
//        logger.fatal(re);
//        throw re;
//    }
//
//    public static boolean isValidForScript(Command command) throws InvalidInput{
//        if (isValidArgument(command)){
//            if (command.isScript()){
//                if (command.getXmlArgument() != null) {
//                    return isXmlHasIdAndDate(command);
//                }throw new InvalidInput("Команда "+ command.getCommandName() +" должна иметь XML строку при исполнении скрипта");
//            } return true;
//        }
//        return true;
//    }
//
//    public static boolean isXmlHasIdAndDate(Command command) throws InvalidInput{
//        if (command.getXmlArgument() != null) {
//            boolean ERR = command.getXmlArgument().equals("ERR");
//            boolean isId = command.getXmlArgument().matches(".*<id>[^<]+</id>.*");
//            boolean isDate = command.getXmlArgument().matches(".*<creation_date>[^<]+</creation_date>.*");
//            if (!ERR) {
//                if (isDate) {
//                    if (isId) {
//                        return true;
//                    }
//                    throw new InvalidInput("XML не имеет ID");
//                }
//                throw new InvalidInput("XML не имеет даты создания");
//            }
//            throw new InvalidInput("Неверная XML строка");
//        }
//        RuntimeException re = new RuntimeException();
//        logger.fatal(re);
//        throw re;
//    }

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
                    if (xmlArgument == null){
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
                    if(Files.isDirectory(path)){
                        if (Files.isReadable(path)){
                            if (xmlArgument == null){
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
                if (argument == null){
                    return;
                }else {
                    throw new InvalidInput("Команда "+ command.getCommandName() +" не должна иметь параметров");
                }
            } case NO_ARGUMENTS -> {
                if (argument == null){
                    if (xmlArgument == null){
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
            }
            RuntimeException re = new RuntimeException();
            logger.fatal(re);
            throw re;
    }

}