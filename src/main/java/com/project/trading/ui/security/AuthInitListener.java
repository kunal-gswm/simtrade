package com.project.trading.ui.security;

import com.project.trading.model.AuthUser;
import com.project.trading.ui.views.LoginView;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.router.BeforeEnterEvent;

public class AuthInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            uiEvent.getUI().addBeforeEnterListener(this::authenticateNavigation);
        });
    }

    private void authenticateNavigation(BeforeEnterEvent event) {
        SecurityService securityService = new SecurityService();
        AuthUser user = securityService.getAuthenticatedUser();

        if (!LoginView.class.equals(event.getNavigationTarget()) && user == null) {
            event.forwardTo(LoginView.class);
        } else if (LoginView.class.equals(event.getNavigationTarget()) && user != null) {
            event.forwardTo(""); // redirect authenticated users away from login
        }
    }
}
