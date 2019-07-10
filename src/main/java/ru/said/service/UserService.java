package ru.said.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private UserService() {}

    public static boolean authentication(Connection connection, String log, String pass) throws SQLException, NoSuchAlgorithmException {
        try (PreparedStatement auth = connection.prepareStatement("SELECT * from ddt_users where login = ?")) {
            auth.setString(1, log);
            ResultSet resultSet = auth.executeQuery();
            if (resultSet.next() && hash(pass).equals(resultSet.getString("password"))) {
                return true;
            }
        }
        return false;
    }

    public static String hash(String hashpass) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] bytes = sha256.digest(hashpass.getBytes());
        StringBuilder strBuilder = new StringBuilder();
        for (byte b : bytes) {
            strBuilder.append(b);
        }
        return strBuilder.toString();
    }
}
