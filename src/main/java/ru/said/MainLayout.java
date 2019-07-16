package ru.said;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import ru.said.service.UserService;


import javax.mail.*;


public class MainLayout extends HorizontalLayout {
    private Button send = new Button("Отправить");
    private Button inbox = new Button("Входящие");
    private TextField email = new TextField("EMAIL");
    private TextField messageTxT = new TextField("text");
    private MenuBar logoutMenu = new MenuBar();
    private VerticalLayout verticalLayout = new VerticalLayout();
    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    private static final Logger LOGGER = Logger.getLogger(MainLayout.class);

    MainLayout() {

        send.addClickListener(clickEvent -> {
            try {
                UserService.sendMessage(email.getValue(), messageTxT.getValue());
                LOGGER.debug("Письмо отправлено");
            } catch (MessagingException e) {
                LOGGER.debug("Письмо не отправлено(((");
                LOGGER.debug(e.getMessage());
            }
        });

        inbox.addClickListener(clickEvent1 -> {
            try {
                UserService.ReadEmail(email.getValue(), messageTxT.getValue());
                LOGGER.debug("Письма считаны");
            } catch (MessagingException e) {
                LOGGER.error("Письма не считаны(((");
                LOGGER.error(e.getMessage());
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
        verticalLayout.addComponents(horizontalLayout, send, inbox, logoutMenu);
        addComponent(verticalLayout);
    }

}

