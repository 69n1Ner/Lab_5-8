package db;

import org.postgresql.util.PSQLException;

import java.sql.SQLException;

public class DbErrorTranslator {
    private DbErrorTranslator() {
    }

    public static String translateSqlException(PSQLException e) {
        String sqlState = e.getSQLState();

        if (sqlState == null) {
            return ("Потеряно соединение с БД или неизвестная ошибка");
        }

        return switch (sqlState) {
            case "23505" -> "Это имя занято";
            case "23503" -> "Нарушена целостность внешних ключей";
            case "23502" -> "Обязательное поле не было заполнено";
            case "23514" -> "Значение не проходит проверку";
            default -> "Ошибка базы данных (код: " + sqlState + ")";
        };
    }
}
