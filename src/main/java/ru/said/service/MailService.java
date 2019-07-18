package ru.said.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MailService {

    private MailService(){}

    private static final Logger LOGGER = Logger.getLogger(UserService.class);
    private static String myEmail;
    private static String myPassword;


    static {
        Properties properties = new Properties();
        String key = "emailSettings";
        String path = System.getProperty(key);
        File file = new File(path);
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("ОШИБКА: неверный ключ --> \"" + key + "\" ");
        }

        if (!file.exists() | !file.isFile()) {
            throw new IllegalArgumentException("ОШИБКА: файл " + path + " не существует");
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
                myEmail = properties.getProperty("yandexLog");
                myPassword = properties.getProperty("yandexPass");

            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static void sendMessage(String email, String messageTxt) throws MessagingException {
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
                        return new PasswordAuthentication(myEmail, myPassword);
                    }
                });
        //Создаем новое почтовое сообщение
        Message message = new MimeMessage(session);
        //От кого
        message.setFrom(new InternetAddress(myEmail));
        //Кому
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        //Тема письма
        message.setSubject("Очень важное письмо!!!");
        //Текст письма
        message.setText(messageTxt);
        //Поехали!!!
        Transport.send(message);

    }

    public static void ReadEmail() throws MessagingException {

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
            store.connect("imap.yandex.ru", 993, myEmail, myPassword);
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
