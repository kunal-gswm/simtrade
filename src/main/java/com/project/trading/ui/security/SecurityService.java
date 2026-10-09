package com.project.trading.ui.security;

import com.project.trading.exception.AuthenticationException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Role;
import com.project.trading.model.User;
import com.project.trading.service.UserService;
import com.project.trading.util.SessionUtil;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;

public class SecurityService {

    private final UserService userService;

    public SecurityService() {
        this.userService = new UserService();
    }

    public AuthUser getAuthenticatedUser() {
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        if (request != null) {
            return SessionUtil.getAuthUser(request.getHttpServletRequest());
        }
        return null;
    }

    public void authenticate(String username, String password) throws AuthenticationException {
        User user = userService.login(username, password);
        AuthUser authUser = new AuthUser(user.getId(), user.getUsername(), user.getRole());
        
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        if (request != null) {
            SessionUtil.login(request.getHttpServletRequest(), authUser);
        }
    }

    public void logout() {
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        if (request != null) {
            SessionUtil.logout(request.getHttpServletRequest());
        }
        VaadinSession.getCurrent().close();
    }
}
