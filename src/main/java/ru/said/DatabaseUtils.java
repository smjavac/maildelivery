package ru.said;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtils {
    private DatabaseUtils() {
    }

    private static final Logger LOGGER = Logger.getLogger(DatabaseUtils.class);
    private static String JDBC_DRIVER;
    private static String DATABASE_URL;
    private static String USER;
    private static String PASSWORD;


    static {
        Properties property = new Properties();
        String key = "config.properties";
        String path = System.getProperty(key);
        File file = new File(path);
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("ОШИБКА: неверный ключ --> \"" + key + "\" ");
        }

        if (!file.exists() | !file.isFile()) {
            throw new IllegalArgumentException("ОШИБКА: файл " + path + " не существует");
        } else {
            try (FileInputStream fis = new FileInputStream(path)
            ) {
                property.load(fis);
                JDBC_DRIVER = property.getProperty("jdbc.driver");
                DATABASE_URL = property.getProperty("db.host");
                USER = property.getProperty("db.login");
                PASSWORD = property.getProperty("db.password");
                Class.forName(JDBC_DRIVER);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return connection;
    }
}
