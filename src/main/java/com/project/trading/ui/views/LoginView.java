package com.project.trading.ui.views;

import com.project.trading.exception.AuthenticationException;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Key;

@Route("login")
@PageTitle("Login | SimTrade")
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("SimTrade Pro");

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setAlignItems(Alignment.STRETCH);
        formLayout.setMaxWidth("300px");
        formLayout.getStyle().set("padding", "2rem").set("box-shadow", "0 4px 12px rgba(0,0,0,0.15)").set("border-radius", "8px").set("background-color", "var(--lumo-base-color)");

        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Button loginButton = new Button("Log in");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Span errorMsg = new Span("Invalid username or password");
        errorMsg.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMsg.setVisible(false);

        loginButton.addClickListener(e -> {
            SecurityService securityService = new SecurityService();
            try {
                securityService.authenticate(username.getValue(), password.getValue());
                UI.getCurrent().navigate("");
            } catch (AuthenticationException ex) {
                errorMsg.setVisible(true);
            }
        });

        loginButton.addClickShortcut(Key.ENTER);

        formLayout.add(username, password, loginButton, errorMsg);

        add(title, formLayout);
    }
}
