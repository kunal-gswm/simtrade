package com.project.trading.ui.views;

import com.project.trading.exception.AuthenticationException;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("v-login")
@PageTitle("Login | SimTrade")
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("SimTrade");

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login"); // Required to prevent default Vaadin routing loop on error
        
        loginForm.addLoginListener(e -> {
            SecurityService securityService = new SecurityService();
            try {
                securityService.authenticate(e.getUsername(), e.getPassword());
                UI.getCurrent().navigate("");
            } catch (AuthenticationException ex) {
                loginForm.setError(true);
            }
        });

        add(title, loginForm);
    }
}
