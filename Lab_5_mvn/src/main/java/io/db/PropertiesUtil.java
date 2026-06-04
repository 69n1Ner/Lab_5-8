package io.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    private PropertiesUtil() {
    }

    static {
        loadProperties();
    }

    private static void loadProperties(){
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("app.properties")){
            PROPERTIES.load(inputStream);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key){
        return PROPERTIES.getProperty(key);
    }

}
