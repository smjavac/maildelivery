package ru.said;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
//import ru.said.bean.User;
//import ru.said.service.UserService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
//import static ru.said.DatabaseUtils.getConnection;
//import java.security.NoSuchAlgorithmException;
//import java.sql.Connection;
//import java.sql.SQLException;


public class MainLayout extends HorizontalLayout {
    private Button send = new Button("Отправить");
    private TextField email = new TextField("EMAIL");
    private TextField messageTxT = new TextField("text");
    private MenuBar logoutMenu = new MenuBar();
    private VerticalLayout verticalLayout = new VerticalLayout();
    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    private static final Logger LOGGER = Logger.getLogger(MainLayout.class);

    MainLayout() {
//        try (Connection connection = getConnection()){
//
//        } catch (SQLException e) {
//            LOGGER.error(e.getMessage(), e);
//        }
        send.addClickListener(clickEvent -> {
            try {
                sendMessge();
            } catch (MessagingException e) {
                LOGGER.debug("сообщение не отправлено");
                LOGGER.debug(e.getMessage());
            }
        });
        logoutMenu.addItem("Logout", FontAwesome.SIGN_OUT, new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                VaadinSession.getCurrent().getSession().invalidate();
                Page.getCurrent().reload();
            }
        });
        LOGGER.debug("SELECT * FROM ddt_users");
        horizontalLayout.addComponents(email, messageTxT);
        verticalLayout.addComponents(horizontalLayout, send, logoutMenu);
        addComponent(verticalLayout);
    }

    private void sendMessge() throws MessagingException {
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
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getValue()));
        //Тема письма
        message.setSubject("Очень важное письмо!!!");
        //Текст письма
        message.setText(messageTxT.getValue());
        //Поехали!!!
        Transport.send(message);
    }
}
