package com.project.trading.ui.views;

import com.project.trading.model.AuthUser;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;

import java.util.ArrayList;
import java.util.List;

public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final List<RouterLink> navLinks = new ArrayList<>();

    // SVG Constants
    private static final String SVG_DASHBOARD = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z\" /></svg>";
    private static final String SVG_BRAND = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>";
    private static final String SVG_MARKET = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M2.25 18L9 11.25l4.306 4.307a11.95 11.95 0 015.814-5.519l2.74-1.22m0 0l-5.94-2.28m5.94 2.28l-2.28 5.941\" /></svg>";
    private static final String SVG_PORTFOLIO = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M21 12a2.25 2.25 0 00-2.25-2.25H15a3 3 0 11-6 0H5.25A2.25 2.25 0 003 12m18 0v6a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 18v-6m18 0V9M3 12V9m18 0a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 9m18 0V6a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 6v3\" /></svg>";
    private static final String SVG_HISTORY = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z\" /></svg>";
    private static final String SVG_LOGOUT = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9\" /></svg>";
    private static final String SVG_PROFILE = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z\" /></svg>";

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        createNavbar();
        createDrawer();
    }

    private void createNavbar() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Toggle navigation");

        Div navbarContent = new Div(toggle);
        navbarContent.addClassName("st-navbar");

        SecurityService securityService = new SecurityService();
        AuthUser user = securityService.getAuthenticatedUser();

        if (user != null) {
            Div userSection = new Div();
            userSection.addClassName("st-navbar-user");

            String rawName = user.getUsername();
            if (rawName == null || rawName.isEmpty()) rawName = "User";
            String formattedUsername = rawName.substring(0, 1).toUpperCase() + rawName.substring(1).toLowerCase();

            Div avatar = new Div();
            avatar.setText(formattedUsername.substring(0, 1));
            avatar.addClassName("st-avatar");

            Span usernameSpan = new Span(formattedUsername);
            usernameSpan.addClassName("st-navbar-username");

            Div userProfile = new Div(avatar, usernameSpan);
            userProfile.getStyle().set("display", "flex").set("align-items", "center").set("gap", "8px");

            Button logout = new Button("Log out", e -> securityService.logout());
            logout.setIcon(createSvgIcon(SVG_LOGOUT));
            logout.addClassName("st-navbar-logout");
            logout.getElement().removeAttribute("theme");

            userSection.add(userProfile, logout);
            navbarContent.add(userSection);
        }

        addToNavbar(navbarContent);
    }

    private void createDrawer() {
        // Brand section
        Div brand = new Div();
        brand.addClassName("st-drawer-brand");

        Component brandIcon = createSvgIcon(SVG_BRAND);
        Span brandText = new Span("SimTrade");
        brandText.addClassName("st-brand-text");
        brand.add(brandIcon, brandText);

        // Navigation links
        Nav nav = new Nav();

        RouterLink dashboardLink = createNavLink("Dashboard", SVG_DASHBOARD, DashboardView.class);
        RouterLink marketLink = createNavLink("Market", SVG_MARKET, MarketView.class);
        RouterLink portfolioLink = createNavLink("Portfolio", SVG_PORTFOLIO, PortfolioView.class);
        RouterLink historyLink = createNavLink("Trade History", SVG_HISTORY, HistoryView.class);
        RouterLink profileLink = createNavLink("Profile", SVG_PROFILE, ProfileView.class);

        nav.add(dashboardLink, marketLink, portfolioLink, historyLink, profileLink);

        addToDrawer(brand, nav);
    }

    private RouterLink createNavLink(String text, String svgData, Class<? extends Component> target) {
        RouterLink link = new RouterLink();
        link.setRoute(target);
        link.addClassName("st-nav-link");

        Component svgIcon = createSvgIcon(svgData);
        Span label = new Span(text);

        link.add(svgIcon, label);
        navLinks.add(link);
        return link;
    }

    private Component createSvgIcon(String svgData) {
        Span span = new Span();
        span.getElement().setProperty("innerHTML", svgData);
        span.getStyle().set("display", "inline-flex").set("align-items", "center").set("justify-content", "center");
        return span;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String currentPath = event.getLocation().getPath();
        for (RouterLink link : navLinks) {
            String href = link.getHref();
            boolean isActive = currentPath.equals(href)
                    || (href.isEmpty() && (currentPath.isEmpty() || currentPath.equals("market")))
                    || (href.equals("market") && currentPath.isEmpty());

            if (isActive) {
                link.addClassName("active");
            } else {
                link.removeClassName("active");
            }
        }
    }
}
