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

        String path = event.getLocation().getPath();
        boolean isAdminRoute = path.startsWith("admin");

        if (!LoginView.class.equals(event.getNavigationTarget()) 
            && !com.project.trading.ui.views.RegistrationView.class.equals(event.getNavigationTarget()) 
            && user == null) {
            event.forwardTo(LoginView.class);
        } else if (user != null) {
            if (LoginView.class.equals(event.getNavigationTarget())) {
                event.forwardTo(""); // redirect authenticated users away from login
            } else if (isAdminRoute && user.getRole() != com.project.trading.model.Role.ADMIN) {
                event.forwardTo(""); // redirect ordinary users away from admin routes
            }
        }
    }
}
