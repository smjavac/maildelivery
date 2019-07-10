package ru.said;

import com.vaadin.ui.*;
//import com.vaadin.ui.PasswordField;
//import com.vaadin.ui.TextField;
//import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;
import ru.said.service.UserService;
import static ru.said.DatabaseUtils.getConnection;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class LoginLayout extends VerticalLayout {
    private Button inter = new Button("войти");
    private TextField userLogin = new TextField("Логин");
    private PasswordField userPassword = new PasswordField("Пароль");
    private static final Logger LOGGER = Logger.getLogger(LoginLayout.class);

    LoginLayout(LoginListener loginListener) {
        inter.addClickListener(clickEvent -> {
            String login = userLogin.getValue();
            String password = userPassword.getValue();
            try {
                if (UserService.authentication(getConnection(), login, password)) {
                    Notification.show("",
                            "Вход выполнен",
                            Notification.Type.HUMANIZED_MESSAGE);
                    CurrentUser.set(login);
                    //   new UserView();

                    //  addComponent(new MainLayout());  ;
                    loginListener.loginSuccessful();
                } else {
                    Notification.show("Ошибка",
                            "Вход не выполнен",
                            Notification.Type.HUMANIZED_MESSAGE);
                }
            }   catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
        userLogin.clear();
        userPassword.clear();
        });


    }


    public interface LoginListener extends Serializable {
        void loginSuccessful();
    }
}
