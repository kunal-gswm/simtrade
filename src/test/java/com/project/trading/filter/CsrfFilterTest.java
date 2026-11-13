package com.project.trading.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CsrfFilterTest {

    private CsrfFilter csrfFilter;

    @BeforeEach
    public void setup() {
        csrfFilter = new CsrfFilter();
    }

    @Test
    public void testGetRequest_PassesAndSetsToken() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        Map<String, Object> requestAttrs = new HashMap<>();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        HttpServletRequest req = mockRequest("GET", null, null, sessionAttrs, requestAttrs);
        HttpServletResponse resp = mockResponse(new AtomicInteger(200));
        FilterChain chain = (r, s) -> chainCalled.set(true);

        csrfFilter.doFilter(req, resp, chain);

        assertTrue(chainCalled.get(), "Chain must be called for GET request");
        assertNotNull(sessionAttrs.get(CsrfFilter.CSRF_TOKEN_ATTR), "CSRF token must be created in session");
        assertEquals(sessionAttrs.get(CsrfFilter.CSRF_TOKEN_ATTR), requestAttrs.get(CsrfFilter.CSRF_TOKEN_ATTR), "CSRF token must be exposed in request");
    }

    @Test
    public void testPostRequest_WithoutToken_Returns403() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put(CsrfFilter.CSRF_TOKEN_ATTR, "valid-token-12345");
        Map<String, Object> requestAttrs = new HashMap<>();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        AtomicInteger status = new AtomicInteger(200);

        HttpServletRequest req = mockRequest("POST", null, null, sessionAttrs, requestAttrs);
        HttpServletResponse resp = mockResponse(status);
        FilterChain chain = (r, s) -> chainCalled.set(true);

        csrfFilter.doFilter(req, resp, chain);

        assertFalse(chainCalled.get(), "Chain must not be called when token is missing");
        assertEquals(403, status.get(), "Missing token must return 403 Forbidden");
    }

    @Test
    public void testPostRequest_WithValidParam_Passes() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put(CsrfFilter.CSRF_TOKEN_ATTR, "secret-token-xyz");
        Map<String, Object> requestAttrs = new HashMap<>();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        AtomicInteger status = new AtomicInteger(200);

        HttpServletRequest req = mockRequest("POST", "secret-token-xyz", null, sessionAttrs, requestAttrs);
        HttpServletResponse resp = mockResponse(status);
        FilterChain chain = (r, s) -> chainCalled.set(true);

        csrfFilter.doFilter(req, resp, chain);

        assertTrue(chainCalled.get(), "Chain must be called when valid token is supplied");
        assertEquals(200, status.get());
    }

    @Test
    public void testPostRequest_WithValidHeader_Passes() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put(CsrfFilter.CSRF_TOKEN_ATTR, "header-token-abc");
        Map<String, Object> requestAttrs = new HashMap<>();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        AtomicInteger status = new AtomicInteger(200);

        HttpServletRequest req = mockRequest("POST", null, "header-token-abc", sessionAttrs, requestAttrs);
        HttpServletResponse resp = mockResponse(status);
        FilterChain chain = (r, s) -> chainCalled.set(true);

        csrfFilter.doFilter(req, resp, chain);

        assertTrue(chainCalled.get(), "Chain must be called when valid header token is supplied");
        assertEquals(200, status.get());
    }

    @Test
    public void testVaadinFlowRequest_BypassesFilterEvenOnPost() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        Map<String, Object> requestAttrs = new HashMap<>();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        AtomicInteger status = new AtomicInteger(200);

        HttpSession session = (HttpSession) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpSession.class},
                (proxy, m, args) -> null
        );

        HttpServletRequest req = (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, m, args) -> {
                    String name = m.getName();
                    if ("getMethod".equals(name)) return "POST";
                    if ("getSession".equals(name)) return session;
                    if ("getParameter".equals(name)) {
                        if ("v-r".equals(args[0])) return "uidl";
                        return null;
                    }
                    return null;
                }
        );

        HttpServletResponse resp = mockResponse(status);
        FilterChain chain = (r, s) -> chainCalled.set(true);

        csrfFilter.doFilter(req, resp, chain);

        assertTrue(chainCalled.get(), "Chain must be called for Vaadin Flow RPC without custom token");
        assertEquals(200, status.get());
    }

    private HttpServletRequest mockRequest(String method, String paramToken, String headerToken,
                                           Map<String, Object> sessionAttrs, Map<String, Object> requestAttrs) {
        HttpSession session = (HttpSession) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpSession.class},
                (proxy, m, args) -> {
                    String name = m.getName();
                    if ("getAttribute".equals(name)) return sessionAttrs.get(args[0]);
                    if ("setAttribute".equals(name)) {
                        sessionAttrs.put((String) args[0], args[1]);
                        return null;
                    }
                    return null;
                }
        );

        return (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, m, args) -> {
                    String name = m.getName();
                    if ("getMethod".equals(name)) return method;
                    if ("getSession".equals(name)) return session;
                    if ("getParameter".equals(name)) {
                        if (CsrfFilter.CSRF_PARAM_NAME.equals(args[0])) return paramToken;
                        return null;
                    }
                    if ("getHeader".equals(name)) {
                        if (CsrfFilter.CSRF_HEADER_NAME.equalsIgnoreCase((String) args[0])) return headerToken;
                        return null;
                    }
                    if ("setAttribute".equals(name)) {
                        requestAttrs.put((String) args[0], args[1]);
                        return null;
                    }
                    if ("getAttribute".equals(name)) return requestAttrs.get(args[0]);
                    return null;
                }
        );
    }

    private HttpServletResponse mockResponse(AtomicInteger status) {
        return (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, m, args) -> {
                    String name = m.getName();
                    if ("sendError".equals(name)) {
                        status.set((Integer) args[0]);
                        return null;
                    }
                    if ("setStatus".equals(name)) {
                        status.set((Integer) args[0]);
                        return null;
                    }
                    return null;
                }
        );
    }
}
