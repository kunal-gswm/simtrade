package com.project.trading.util;

import com.project.trading.model.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    private static final String AUTH_USER_KEY = "AuthUser";
    private static final String FLASH_SUCCESS = "flashSuccess";
    private static final String FLASH_ERROR = "flashError";

    public static void login(HttpServletRequest req, AuthUser user) {
        req.changeSessionId();
        req.getSession().setAttribute(AUTH_USER_KEY, user);
    }

    public static void logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static AuthUser getAuthUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (AuthUser) session.getAttribute(AUTH_USER_KEY);
        }
        return null;
    }

    public static void flashSuccess(HttpServletRequest req, String message) {
        req.getSession().setAttribute(FLASH_SUCCESS, message);
    }

    public static void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute(FLASH_ERROR, message);
    }

    public static String getAndRemoveFlashSuccess(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String msg = (String) session.getAttribute(FLASH_SUCCESS);
            session.removeAttribute(FLASH_SUCCESS);
            return msg;
        }
        return null;
    }

    public static String getAndRemoveFlashError(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String msg = (String) session.getAttribute(FLASH_ERROR);
            session.removeAttribute(FLASH_ERROR);
            return msg;
        }
        return null;
    }
}
