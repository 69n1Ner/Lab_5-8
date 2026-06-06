package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL_KEY = PropertiesUtil.getProperty("db.url");
    private static final String USER_KEY = PropertiesUtil.getProperty("db.user");
    private static final String PASSWORD_KEY = PropertiesUtil.getProperty("db.password");

    public static Connection open(){
        try {
            return DriverManager.getConnection(URL_KEY,USER_KEY,PASSWORD_KEY);
        } catch (SQLException e) {
            //todo дописать свой exception
            throw new RuntimeException(e);
        }
    }

    private ConnectionManager() {
    }
}
