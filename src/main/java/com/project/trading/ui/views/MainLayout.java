package com.project.trading.ui.views;

import com.project.trading.model.AuthUser;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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

    // High quality terminal SVG icons
    private static final String SVG_DASHBOARD = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z\" /></svg>";
    private static final String SVG_BRAND = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>";
    private static final String SVG_MARKET = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M2.25 18L9 11.25l4.306 4.307a11.95 11.95 0 015.814-5.519l2.74-1.22m0 0l-5.94-2.28m5.94 2.28l-2.28 5.941\" /></svg>";
    private static final String SVG_PORTFOLIO = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M21 12a2.25 2.25 0 00-2.25-2.25H15a3 3 0 11-6 0H5.25A2.25 2.25 0 003 12m18 0v6a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 18v-6m18 0V9M3 12V9m18 0a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 9m18 0V6a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 6v3\" /></svg>";
    private static final String SVG_HISTORY = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z\" /></svg>";
    private static final String SVG_LOGOUT = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9\" /></svg>";
    private static final String SVG_PROFILE = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z\" /></svg>";

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        createNavbar();
        createDrawer();
    }

    private void createNavbar() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Toggle navigation");
        toggle.addClassName("st-drawer-toggle");
        toggle.getStyle().set("color", "var(--st-text-secondary)");

        // Mobile brand badge (visible on tablet / mobile)
        Div mobileBrand = new Div();
        mobileBrand.addClassName("st-navbar-mobile-brand");
        Component mobileBrandIcon = createSvgIcon(SVG_BRAND);
        Span mobileBrandText = new Span("SimTrade");
        mobileBrandText.addClassName("st-brand-text");
        Span mobileTag = new Span("PAPER");
        mobileTag.addClassName("st-brand-tag");
        mobileBrandText.add(mobileTag);
        mobileBrand.add(mobileBrandIcon, mobileBrandText);

        Div statusBadge = new Div();
        statusBadge.addClassName("st-navbar-status");
        Span pulse = new Span();
        pulse.addClassName("st-pulse-dot");
        Span statusText = new Span("SIMULATION LIVE");
        statusText.getStyle().set("font-size", "11px").set("font-weight", "600").set("letter-spacing", "0.06em");
        statusBadge.add(pulse, statusText);

        Div navbarLeft = new Div(toggle, mobileBrand, statusBadge);
        navbarLeft.addClassName("st-navbar-left");

        Div navbarContent = new Div(navbarLeft);
        navbarContent.addClassName("st-navbar");

        SecurityService securityService = new SecurityService();
        AuthUser user = securityService.getAuthenticatedUser();

        if (user != null) {
            Div userSection = new Div();
            userSection.addClassName("st-navbar-user");

            String rawName = user.getUsername();
            if (rawName == null || rawName.isEmpty()) rawName = "Trader";
            String formattedUsername = rawName.substring(0, 1).toUpperCase() + rawName.substring(1).toLowerCase();

            Div avatar = new Div();
            avatar.addClassName("st-avatar");
            avatar.setText(formattedUsername.substring(0, 1).toUpperCase());

            Span usernameSpan = new Span(formattedUsername);
            usernameSpan.addClassName("st-navbar-username");

            Div userProfile = new Div(avatar, usernameSpan);
            userProfile.addClassName("st-user-profile");

            Button logout = new Button("Exit", e -> securityService.logout());
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

        Span tag = new Span("PAPER");
        tag.addClassName("st-brand-tag");
        brandText.add(tag);

        brand.add(brandIcon, brandText);

        // Section title
        Div navHeader = new Div();
        navHeader.setText("Trading Terminal");
        navHeader.addClassName("st-nav-section-title");

        // Navigation links
        Nav nav = new Nav();

        RouterLink dashboardLink = createNavLink("Overview", SVG_DASHBOARD, DashboardView.class);
        RouterLink marketLink = createNavLink("Market Quotes", SVG_MARKET, MarketView.class);
        RouterLink portfolioLink = createNavLink("Holdings & P&L", SVG_PORTFOLIO, PortfolioView.class);
        RouterLink historyLink = createNavLink("Trade Log", SVG_HISTORY, HistoryView.class);
        RouterLink profileLink = createNavLink("Settings", SVG_PROFILE, ProfileView.class);

        nav.add(dashboardLink, marketLink, portfolioLink, historyLink, profileLink);

        // Drawer footer
        Div footer = new Div();
        footer.addClassName("st-drawer-footer");
        Span vSpan = new Span("SimTrade Terminal v2.0");
        vSpan.getStyle().set("font-weight", "600");
        Span subSpan = new Span("Institutional Paper Engine");
        footer.add(vSpan, subSpan);

        addToDrawer(brand, navHeader, nav, footer);
    }

    private RouterLink createNavLink(String text, String svgData, Class<? extends Component> target) {
        RouterLink link = new RouterLink();
        link.setRoute(target);
        link.addClassName("st-nav-link");

        Component svgIcon = createSvgIcon(svgData);
        Span label = new Span(text);

        link.add(svgIcon, label);
        navLinks.add(link);

        // Immediately close overlay drawer on client-side click
        link.getElement().executeJs("this.addEventListener('click', () => window.dispatchEvent(new CustomEvent('close-overlay-drawer')));");

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
        if (isOverlay()) {
            setDrawerOpened(false);
        }
        UI.getCurrent().getPage().executeJs("window.dispatchEvent(new CustomEvent('close-overlay-drawer'));");

        String currentPath = event.getLocation().getPath();
        for (RouterLink link : navLinks) {
            String href = link.getHref();
            boolean isActive = currentPath.equals(href)
                    || (href.isEmpty() && (currentPath.isEmpty() || currentPath.equals("market") || currentPath.equals("ui")))
                    || (href.equals("ui/market") && (currentPath.equals("ui/market") || currentPath.equals("ui") || currentPath.isEmpty()))
                    || (href.equals("ui/dashboard") && currentPath.equals("ui/dashboard"));

            if (isActive) {
                link.addClassName("active");
            } else {
                link.removeClassName("active");
            }
        }
    }
}
