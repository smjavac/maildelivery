package ru.said;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import ru.said.bean.User;
import ru.said.service.UserService;

import static ru.said.DatabaseUtils.getConnection;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class MainLayout extends HorizontalLayout {
    private TextField email = new TextField("EMAIL");
    private TextField message = new TextField("text");
    private MenuBar logoutMenu = new MenuBar();
    private VerticalLayout verticalLayout = new VerticalLayout();
    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    private static final Logger LOGGER = Logger.getLogger(MainLayout.class);

    MainLayout() {
        try (Connection connection = getConnection()){

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        logoutMenu.addItem("Logout", FontAwesome.SIGN_OUT, new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                VaadinSession.getCurrent().getSession().invalidate();
                Page.getCurrent().reload();
            }
        });
        LOGGER.debug("SELECT * FROM ddt_users");
        horizontalLayout.addComponents(email, message);
        verticalLayout.addComponents(horizontalLayout, logoutMenu);
        addComponent(verticalLayout);
    }
}
