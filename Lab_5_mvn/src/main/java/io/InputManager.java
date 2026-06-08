package io;

import db.UserDao;
import exceptions.NoSuchCommandException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.*;
import security.MD2Hash;
import security.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;


import static java.lang.Math.abs;


public class InputManager {
    private static final Logger log = LogManager.getLogger(InputManager.class);
    private String commandName;
    private String mainArgument;
    private String xmlArgument;


    //todo ВАЖНО сделать связь с проверкой на клиента через запросы к серверу, а не просто к бд


    public static User inputUser(BufferedReader br, boolean isRegistration){
        String name = getUserString(br,false,isRegistration);
        String password = getUserString(br,true,isRegistration);
        return new User().setUserName(name).setPassword(password);
    }

    /* isOMT отвечает за повторный ввод 1 раз после неудачной попытки
        isAgain отвечает за полный ввод заново и пароля и имени
     */
    private static String getUserString(BufferedReader br,boolean isPassword, boolean isRegistration){
        return getUserString(br,isPassword,false,isRegistration);
    }

    private static String  getUserString(BufferedReader br,boolean isPassword,boolean isOMT, boolean isRegistration){

        try {
            String input;
            if (isOMT){
                if (isPassword){
                    log.info("Введите пароль еще раз");

                } else {
                    log.info("Введите имя еще раз");

                }
            } else {
                if (isPassword) {
                    log.info("Введите пароль");
                } else {
                    log.info("Введите имя");
                }
            }

            input = br.readLine();
            input = separateSecurity(input);


            if (input != null && !input.isEmpty() && Validator.isUserInfoValid(input,false,isPassword)){
                if (isPassword){
                    return MD2Hash.hashWithMD2(input);
                }else return input;

            }else {
                return isOMT
                        ? isRegistration
                                ? getUserString(br,isPassword, true,true)
                                : null
                        : getUserString(br,isPassword,true,isRegistration);
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            log.warn(e);
        }
        return null;
    }


    public static String separateSecurity(String input){
        String text = Validator.hasSpecialSymbol(input);
        if (text !=null){
            log.warn(text);
            return null;
        }
        input = input.trim().strip();
        if (input.contains(" ")){
            log.warn("В строке не должно быть пробелов");
            return null;
        }

        return input;
    }

    public void separateCommand(String input) throws NoSuchCommandException {
        String text = Validator.hasSpecialSymbol(input);
        if (text !=null){
            throw new NoSuchCommandException(text);
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
//                    log.debug("xmlArgument={}",xmlArgument);
                    this.xmlArgument = input.substring(end);
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

        this.commandName = wordList.get(0);

        if (wordList.size() >= 2) {
            this.mainArgument = wordList.get(1);
        }
    }

    private static String separateAttribute(String input) {
        return input.trim();
    }

    public static Organization inputOrganization() {
        return inputOrganization(false);
    }

    public static Organization inputOrganization(boolean isUpdate) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Введите название организации");
        String name = (String) getValueOf(String.class, isUpdate,br);

        System.out.print("Введите тип организации");
        OrganizationType type = (OrganizationType) getOrganizationType(isUpdate, false,br);

        System.out.println("Введите координаты организации");
        //todo недоработка
        System.out.print("Координата x (максимум 623)");
        Long xC = (Long) getValueOf(Long.class, isUpdate,br);

        System.out.print("Координата y");
        Double yC = (Double) getValueOf(Double.class, isUpdate,br);
        Coordinates coordinates = new Coordinates(xC, yC);


        Address address = inputAddress(isUpdate);

        System.out.print("Введите количество сотрудников");
        Long employeesCount = (Long) getValueOf(Long.class, isUpdate, true,br);

        System.out.print("Введите годовую выручку");
        Integer annualTurnover = (Integer) getValueOf(Integer.class, isUpdate, true,br);


        return new Organization(
                name,
                annualTurnover,
                coordinates,
                employeesCount,
                address,
                type
        );
    }

    public static Address inputAddress() {
        return inputAddress(false);
    }

    public static Address inputAddress(boolean isUpdate) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Введите адрес");
        System.out.print("Почтовый индекс (минимум 4 символа)");
        String zip = getZipCode(isUpdate,false,br);
        System.out.print("Название города");
        String city = (String) getValueOf(String.class, isUpdate,br);
        System.out.print("Координата x");
        Float xL = (Float) getValueOf(Float.class, isUpdate,br);
        System.out.print("Координата y");
        Integer yL = (Integer) getValueOf(Integer.class, isUpdate,br);
        System.out.print("Координата z");
        Integer zL = (Integer) getValueOf(Integer.class, isUpdate,br);


        return new Address(zip, new Location(city, xL, yL, zL));
    }

    private static  <T> Object oneMoreTime(Class<T> type, boolean positive, BufferedReader br) {
        System.out.println("Введите еще раз " + "[" + type.getSimpleName() + "]");

        try {
            String sa = separateAttribute(br.readLine());
            if (Validator.isValidInput(sa)) {
                if (type == String.class) {
                    return sa;
                }
                Method method = type.getMethod("valueOf", String.class);

                Number number = (Number) type.getMethod("valueOf", String.class).invoke(null, sa);
                if (positive && (number.doubleValue() <= 0)) {
                    return null;
                }

                return method.invoke(null, sa);
            } else {
                return null;
            }
        } catch (IllegalArgumentException |
                 InvocationTargetException e) {
            return null;

        } catch (IOException |
                 NoSuchMethodException |
                 IllegalAccessException |
                 RuntimeException e) {
            System.out.println(Arrays.toString(e.getStackTrace()) + e.getMessage());

        }

        return null;
    }


    private static  <T> Object getValueOf(Class<T> classType, boolean isUpdate, BufferedReader br) {
        return getValueOf(classType, isUpdate, false, br);
    }

    private static  <T> Object getValueOf(Class<T> classType, boolean isUpdate, boolean positive, BufferedReader br) {
        System.out.println(" [" + classType.getSimpleName() + "]");


        try {
            String sa = separateAttribute(br.readLine());
            if (Validator.isValidInput(sa)) {
                if (classType == String.class && !sa.isEmpty()) {
                    return sa;
                }
                Method method = classType.getMethod("valueOf", String.class);

                Number number = (Number) classType.getMethod("valueOf", String.class).invoke(null, sa);
                if (positive && (number.doubleValue() <= 0)) {
                    return !isUpdate ? oneMoreTime(classType, true, br) : null;
                }

                return method.invoke(null, sa);
            } else {
                return !isUpdate ? oneMoreTime(classType, positive,br) : null;
            }

        } catch (IOException |
                 NullPointerException |
                 NoSuchMethodException |
                 IllegalAccessException |
                 InvocationTargetException |
                 IllegalArgumentException e) {

            return !isUpdate ? oneMoreTime(classType, positive,br) : null;
        }

    }

    private static Object getOrganizationType(boolean isUpdate, boolean isOMT, BufferedReader br) {
        if (isOMT) {
            System.out.println("Введите еще раз");
        } else {
            System.out.println();
        }
        Arrays.stream(OrganizationType.values()).forEach(e -> System.out.println(e.name()));


        try {
            String sa = separateAttribute(br.readLine());
            if (Validator.isValidInput(sa)) {
                return OrganizationType.valueOf(sa);
            } else if (isOMT) {
                return null;
            } else {
                return !isUpdate ? getOrganizationType(false, true,br) : null;
            }

        } catch (IllegalArgumentException | IOException e) {
            if (isOMT) {
                return null;
            } else {
                return !isUpdate ? getOrganizationType(false, true, br) : null;
            }
        }
    }

    private static String getZipCode(boolean isUpdate, boolean isOMT, BufferedReader br) {
        if (isOMT) {
            System.out.println("Введите еще раз");
        } else {
            System.out.println(" [String]");
        }
        try {

            String sa = separateAttribute(br.readLine());
            if (Validator.isValidInput(sa) && sa.length() >= 4) {
                return sa;
            } else if (isOMT) {
                return null;
            } else {
                return !isUpdate ? getZipCode(false,true,br) : null;
            }
        } catch (IllegalArgumentException | IOException e) {
            if (isOMT) {
                return null;
            } else {
                return !isUpdate ? getZipCode(false, true, br) : null;
            }
        }
    }


    public static ObjWithFeedback<Organization> generateOrganizationFields(Organization organization, boolean isReadFile) {
        ObjWithFeedback<Organization> organizationWithFeedback = new ObjWithFeedback<>(organization,new  ArrayList<>());

        if (!isReadFile) {
            organization.setCreationDate(organization.getCreationDate() == null ? LocalDate.now() : organization.getCreationDate());
        }

        if (organization.getAnnualTurnover() == 0) {
            organizationWithFeedback.feedback().add("Значение годовой выручки было установлено на: 0");
        }

        long xC = organization.getCoordinates().getX();
        Double yC = organization.getCoordinates().getY();
        if (xC == 0) {
            organizationWithFeedback.feedback().add("Значение координаты X организации было установлено на: 0");
        } else if (xC > 623) {
            organization.getCoordinates().setX(623);
            organizationWithFeedback.feedback().add("Значение координаты X организации получило максимальное значение (623)");
        }
        if (yC == null) {
            organization.getCoordinates().setY(0D);
            organizationWithFeedback.feedback().add("Значение координаты Y организации было установлено на: 0");
        }

        if (organization.getEmployeesCount() == 0) {
            organizationWithFeedback.feedback().add("Значение количества сотрудников было установлено на: 0");
        }

        if (organization.getName().isEmpty()) {
            String name = "Organization#"+String.valueOf(abs(organization.hashCode())).substring(6);
            organization.setName(name);
            organizationWithFeedback.feedback().add("Значение названия организации было установлено на: "+ name);
        }

        if (organization.getType() == null){
            organization.setType(OrganizationType.PUBLIC);
            organizationWithFeedback.feedback().add("Значение типа организации было установлено на: "+OrganizationType.PUBLIC.getName());
        }
        ObjWithFeedback<Address> objWithFeedback = generateAddressFields(organization.getPostalAddress());
        Address address = objWithFeedback.object();
        List<String> feedback = objWithFeedback.feedback();

        organizationWithFeedback.feedback().addAll(feedback);
        organizationWithFeedback.object().setPostalAddress(address);

        return organizationWithFeedback;
    }

    public static ObjWithFeedback<Address> generateAddressFields(Address address){
        ObjWithFeedback<Address> addressWithFeedback = new ObjWithFeedback<>(address,new ArrayList<>());
        String zip = address.getZipCode();
        Float xL = address.getTown().getX();
        Integer yL = address.getTown().getY();
        Integer zL = address.getTown().getZ();
        String name = address.getTown().getName();
        Location town = address.getTown();

        if (zip == null || zip.length() < 4) {
            String zipCode = "0000";
            address.setZipCode(zipCode);
            addressWithFeedback.feedback().add("Значение почтового индекса было установлено: "+zipCode);
        }
        if (xL == null) {
            town.setX(0F);
            addressWithFeedback.feedback().add("Значение координаты X города было установлено на: 0");
        }
        if (yL == null) {
            town.setY(0);
            addressWithFeedback.feedback().add("Значение координаты Y города было установлено на: 0");
        }
        if (zL == null) {
            town.setZ(0);
            addressWithFeedback.feedback().add("Значение координаты Z города было установлено на: 0");
        }
        if (name == null || name.isEmpty()) {
            String addressName = "City#"+String.valueOf(abs(address.hashCode())).substring(6);
            town.setName(addressName);
            addressWithFeedback.feedback().add("Значение названия города было установлено на: "+addressName);
        }
        return addressWithFeedback;
    }

    public static Level parseLevel(String string) {
        if (string == null || string.isEmpty()){
            System.out.println("Уровень логирования установлен по умолчанию: info");
            return Level.INFO;
        }

        string = string.toLowerCase();
        switch (string){
            case "info" -> {
                System.out.println("Уровень логирования установлен: "+ string);
                return Level.INFO;
            }
            case "warn" -> {
                System.out.println("Уровень логирования установлен: "+ string);
                return Level.WARN;
            }
            case "error" ->{
                System.out.println("Уровень логирования установлен: "+ string);
                return Level.ERROR;
            }
            case "debug" ->{
                System.out.println("Уровень логирования установлен: "+ string);
                return Level.DEBUG;
            }
            default -> {
                System.err.println("Неопознанное значения уровня логирования: "+string);
                System.out.println("Уровень логирования установлен по умолчанию: info");
                return Level.INFO;
            }
        }
    }

    public static boolean parseConsoleLogger(String s) {
        if (s == null || s.isEmpty()) {
            System.out.println("Логирование будет выведено на консоль");
            return true;
        }
        s = s.toLowerCase();

        switch (s) {
            case "true" -> {
                System.out.println("Логирование будет выведено на консоль");
                return true;
            }
            case "false" -> {
                System.out.println("Логирование не будет выведено на консоль");
                return false;
            }
            default -> {
                System.err.println("Неопознанное значения флага вывода на консоль: "+s);
                System.out.println("Логирование будет выведено на консоль");
                return true;
            }
        }
    }

    public static boolean parseFile(String s){
        if (s == null || s.isEmpty()) {
            System.out.println("Логирование будет выведено в файл");
            return true;
        }
        s = s.toLowerCase();

        switch (s) {
            case "true" -> {
                System.out.println("Логирование будет выведено в файл");
                return true;
            }
            case "false" -> {
                System.out.println("Логирование не будет выведено в файл");
                return false;
            }
            default -> {
                System.err.println("Неопознанное значения флага вывода в файл: "+s);
                System.out.println("Логирование будет выведено в файл");
                return true;
            }
        }
    }
    public static Path parseInitCollection(String string){
        if (string == null || string.isEmpty()){
            System.out.println("Не указана начальная коллекция или переменная LAB5_8 не задана");
            return null;
        }
        if (string.contains("\"")){
            string = string.replace("\"","");
        }
        return Path.of(string);
    }

    public String getMainArgument() {
        return mainArgument;
    }

    public String getXmlArgument() {
        return xmlArgument;
    }

    public String getCommandName() {
        return commandName;
    }

}