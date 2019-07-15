package ru.said.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

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

    public static void sendMessge(String email, String messageTxt ) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        //Хост или IP-адрес почтового сервера
        properties.put("mail.smtp.host", "smtp.yandex.ru");
        //Требуется ли аутентификация для отправки сообщения
        properties.put("mail.smtp.auth", "true");
        //Порт для установки соединения
        properties.put("mail.smtp.socketFactory.port", "465");
        //Фабрика сокетов, так как при отправке сообщения Yandex требует SSL-соединения
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //Создаем соединение для отправки почтового сообщения
        Session session = Session.getDefaultInstance(properties,
                //Аутентификатор - объект, который передает логин и пароль
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("sm.yusupov@yandex.ru", "s07071983");
                    }
                });
        //Создаем новое почтовое сообщение
        Message message = new MimeMessage(session);
        //От кого
        message.setFrom(new InternetAddress("sm.yusupov@yandex.ru"));
        //Кому
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        //Тема письма
        message.setSubject("Очень важное письмо!!!");
        //Текст письма
        message.setText(messageTxt);
        //Поехали!!!
        Transport.send(message);

    }
}
