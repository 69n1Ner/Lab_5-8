package io;

import main.Invoker;

public class Validator {
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


    public static boolean isValidCommand(String command, Invoker invoker) {
        if (!invoker.contains(command)) {
            return false;
        }
        return Validator.isValidInput(command);
    }

    public static boolean isValid(Invoker invoker) {
        try {
            invoker.getContainer().getById(Long.parseLong(invoker.getInputManager().getMainArgument()));
            return true;
        } catch (NumberFormatException e) {
            System.err.println("Неверно задан ID");
            return false;
        }
    }
}