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
    private UserService() {
    }

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

    public static void sendMessage(String emailForSend, String messageTxt, String myEmail, String password) throws MessagingException {
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
                        return new PasswordAuthentication(myEmail, password);
                    }
                });
        //Создаем новое почтовое сообщение
        Message message = new MimeMessage(session);
        //От кого
        message.setFrom(new InternetAddress("sm.yusupov@yandex.ru"));
        //Кому
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailForSend));
        //Тема письма
        message.setSubject("Очень важное письмо!!!");
        //Текст письма
        message.setText(messageTxt);
        //Поехали!!!
        Transport.send(message);

    }

    public static void ReadEmail(String email, String password) throws MessagingException {

            //Объект properties содержит параметры соединения
            Properties properties = new Properties();
           // Properties properties = new Properties();
            //Так как для чтения Yandex требует SSL-соединения - нужно использовать фабрику SSL-сокетов
            properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            //Создаем соединение для чтения почтовых сообщений
            Session session = Session.getDefaultInstance(properties);
            //Это хранилище почтовых сообщений. По сути - это и есть почтовый ящик=)
            Store store = null;
            try {
                //Для чтения почтовых сообщений используем протокол IMAP.
                //Почему? Так Yandex сказал: https://yandex.ru/support/mail/mail-clients.html
                //см. раздел "Входящая почта"
                store = session.getStore("imap");
                //Подключаемся к почтовому ящику
                store.connect("imap.yandex.ru", 993, email, password);
                //Это папка, которую будем читать
                Folder inbox = null;
                try {
                    //Читаем папку "Входящие сообщения"
                    inbox = store.getFolder("INBOX");
                    //Будем только читать сообщение, не меняя их
                    inbox.open(Folder.READ_ONLY);

                    //Получаем количество сообщения в папке
                    int count = inbox.getMessageCount();
                    //Вытаскиваем все сообщения с первого по последний
                    Message[] messages = inbox.getMessages(1, count);
                    //Циклом пробегаемся по всем сообщениям
                    for (Message message : messages) {
                        //От кого
                        String from = ((InternetAddress) message.getFrom()[0]).getAddress();
                        System.out.println("FROM: " + from);
                        //Тема письма
                        System.out.println("SUBJECT: " + message.getSubject());
                    }
                } finally {
                    if (inbox != null) {
                        //Не забываем закрыть собой папку сообщений.
                        inbox.close(false);
                    }
                }

            } finally {
                if (store != null) {
                    //И сам почтовый ящик тоже закрываем
                    store.close();
                }
            }

    }
}
