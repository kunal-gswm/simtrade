package com.project.trading.ui.admin;

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

public class AdminLayout extends AppLayout implements AfterNavigationObserver {

    private final SecurityService securityService = new SecurityService();
    private final List<RouterLink> navLinks = new ArrayList<>();

    // SVG Constants
    private static final String SVG_BRAND = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>";
    private static final String SVG_DASHBOARD = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z\" /></svg>";
    private static final String SVG_USERS = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z\" /></svg>";
    private static final String SVG_STOCKS = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M2.25 18L9 11.25l4.306 4.307a11.95 11.95 0 015.814-5.519l2.74-1.22m0 0l-5.94-2.28m5.94 2.28l-2.28 5.941\" /></svg>";
    private static final String SVG_MONITOR = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M9 17.25v1.007a3 3 0 01-.879 2.122L7.5 21h9l-.621-.621A3 3 0 0115 18.257V17.25m6-12V15a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 15V5.25m18 0A2.25 2.25 0 0018.75 3H5.25A2.25 2.25 0 003 5.25m18 0V12a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 12V5.25\" /></svg>";
    private static final String SVG_TOOLS = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M11.42 15.17L17.25 21A2.652 2.652 0 0021 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 11-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 004.486-6.336l-3.276 3.277a3.004 3.004 0 01-2.25-2.25l3.276-3.276a4.5 4.5 0 00-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437l1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008z\" /></svg>";
    private static final String SVG_EXIT = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M9 15L3 9m0 0l6-6M3 9h12a6 6 0 010 12h-3\" /></svg>";
    private static final String SVG_LOGOUT = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9\" /></svg>";
    private static final String SVG_CLOSE = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M6 18L18 6M6 6l12 12\" /></svg>";

    public AdminLayout() {
        setPrimarySection(Section.NAVBAR);
        setDrawerOpened(false);
        createNavbar();
        createDrawer();
        setupResponsiveEngine();
    }

    private void setupResponsiveEngine() {
        getElement().executeJs(
            "const al = this;" +
            "const syncMode = () => {" +
            "  const isMobile = window.innerWidth <= 1024;" +
            "  if (isMobile) {" +
            "    al.style.setProperty('--vaadin-app-layout-drawer-overlay', 'true');" +
            "    al.style.setProperty('--vaadin-app-layout-drawer-offset-size', '0px');" +
            "    if (typeof al._setOverlay === 'function') al._setOverlay(true);" +
            "    else al.setAttribute('overlay', '');" +
            "    if (al.__wasDesktop || al.__wasDesktop === undefined) {" +
            "      al.drawerOpened = false;" +
            "    }" +
            "    al.__wasDesktop = false;" +
            "    if (typeof al._updateOffsetSize === 'function') al._updateOffsetSize();" +
            "  } else {" +
            "    const wasMobile = (al.__wasDesktop === false);" +
            "    const isInitial = (al.__wasDesktop === undefined);" +
            "    al.__wasDesktop = true;" +
            "    al.style.setProperty('--vaadin-app-layout-drawer-overlay', 'false');" +
            "    if (typeof al._setOverlay === 'function') al._setOverlay(false);" +
            "    else al.removeAttribute('overlay');" +
            "    if (isInitial || wasMobile) {" +
            "      al.drawerOpened = true;" +
            "    }" +
            "    if (typeof al._updateOffsetSize === 'function') al._updateOffsetSize();" +
            "  }" +
            "};" +
            "window.addEventListener('resize', syncMode);" +
            "window.addEventListener('close-overlay-drawer', () => { al.drawerOpened = false; });" +
            "requestAnimationFrame(syncMode);" +
            "setTimeout(syncMode, 50);" +
            "setTimeout(syncMode, 200);"
        );
    }

    private void createNavbar() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Toggle navigation");
        toggle.getElement().setAttribute("title", "Toggle navigation");
        toggle.addClassName("st-drawer-toggle");
        toggle.getStyle().set("color", "var(--st-text-secondary)");

        // Mobile brand badge
        Div mobileBrand = new Div();
        mobileBrand.addClassName("st-navbar-mobile-brand");
        Component mobileBrandIcon = createSvgIcon(SVG_BRAND);
        Span mobileBrandText = new Span("SimTrade");
        mobileBrandText.addClassName("st-brand-text");
        Span mobileTag = new Span("ADMIN");
        mobileTag.getStyle()
                .set("font-size", "9px")
                .set("font-weight", "700")
                .set("letter-spacing", "0.08em")
                .set("text-transform", "uppercase")
                .set("color", "var(--st-amber)")
                .set("background", "var(--st-amber-bg)")
                .set("border", "1px solid rgba(217, 119, 6, 0.3)")
                .set("padding", "1px 5px")
                .set("border-radius", "2px");
        mobileBrandText.add(mobileTag);
        mobileBrand.add(mobileBrandIcon, mobileBrandText);

        Span titleBadge = new Span("ADMIN CONSOLE");
        titleBadge.addClassName("st-navbar-status");
        titleBadge.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "700")
                .set("letter-spacing", "0.08em")
                .set("color", "var(--st-amber)")
                .set("background", "var(--st-amber-bg)")
                .set("border", "1px solid rgba(217, 119, 6, 0.3)")
                .set("padding", "3px 8px")
                .set("border-radius", "3px");

        Div navbarLeft = new Div(toggle, mobileBrand, titleBadge);
        navbarLeft.addClassName("st-navbar-left");

        Div navbarContent = new Div(navbarLeft);
        navbarContent.addClassName("st-navbar");

        Div userSection = new Div();
        userSection.addClassName("st-navbar-user");

        Button logout = new Button("Exit Admin", e -> securityService.logout());
        logout.setIcon(createSvgIcon(SVG_LOGOUT));
        logout.addClassName("st-navbar-logout");
        logout.getElement().removeAttribute("theme");

        userSection.add(logout);
        navbarContent.add(userSection);

        addToNavbar(navbarContent);
    }

    private void createDrawer() {
        Div drawerWrapper = new Div();
        drawerWrapper.addClassName("st-drawer-wrapper");

        // Brand section (with mobile close button)
        Div brand = new Div();
        brand.addClassName("st-drawer-brand");

        Div brandLeft = new Div();
        brandLeft.addClassName("st-drawer-brand-left");

        Component brandIcon = createSvgIcon(SVG_BRAND);
        Span brandText = new Span("SimTrade");
        brandText.addClassName("st-brand-text");

        Span tag = new Span("ADMIN");
        tag.getStyle()
                .set("font-size", "9px")
                .set("font-weight", "700")
                .set("letter-spacing", "0.08em")
                .set("text-transform", "uppercase")
                .set("color", "var(--st-amber)")
                .set("background", "var(--st-amber-bg)")
                .set("border", "1px solid rgba(217, 119, 6, 0.3)")
                .set("padding", "1px 5px")
                .set("border-radius", "2px");
        brandText.add(tag);

        brandLeft.add(brandIcon, brandText);

        Button closeBtn = new Button();
        closeBtn.addClassName("st-drawer-close-btn");
        closeBtn.setIcon(createSvgIcon(SVG_CLOSE));
        closeBtn.setAriaLabel("Collapse sidebar");
        closeBtn.getElement().setAttribute("title", "Collapse sidebar");
        closeBtn.getElement().removeAttribute("theme");
        closeBtn.addClickListener(e -> setDrawerOpened(false));
        closeBtn.getElement().executeJs("this.addEventListener('click', () => { const al = this.closest('vaadin-app-layout'); if (al) al.drawerOpened = false; });");

        brand.add(brandLeft, closeBtn);

        // Nav Section
        Div navSection = new Div();
        navSection.addClassName("st-drawer-nav-section");

        Div navHeader = new Div();
        navHeader.setText("Administration");
        navHeader.addClassName("st-nav-section-title");

        Nav nav = new Nav();
        nav.addClassName("st-nav-menu");

        RouterLink dashboardLink = createNavLink("Platform Overview", SVG_DASHBOARD, AdminDashboardView.class);
        RouterLink usersLink = createNavLink("User Management", SVG_USERS, AdminUsersView.class);
        RouterLink stocksLink = createNavLink("Stock Catalog", SVG_STOCKS, AdminStocksView.class);
        RouterLink monitorLink = createNavLink("Trade Monitor", SVG_MONITOR, AdminTradeMonitorView.class);
        RouterLink toolsLink = createNavLink("Simulator & Tools", SVG_TOOLS, AdminToolsView.class);

        // Switch back to normal trader app
        RouterLink appLink = createNavLink("Trader Terminal", SVG_EXIT, com.project.trading.ui.views.DashboardView.class);
        appLink.getStyle().set("margin-top", "16px").set("border-top", "1px solid var(--st-border-subtle)");

        nav.add(dashboardLink, usersLink, stocksLink, monitorLink, toolsLink, appLink);
        navSection.add(navHeader, nav);

        // Drawer footer
        Div footer = new Div();
        footer.addClassName("st-drawer-footer");
        Span vSpan = new Span("Admin Controller v2.0");
        vSpan.getStyle().set("font-weight", "600");
        Span subSpan = new Span("Restricted Superuser Access");
        footer.add(vSpan, subSpan);

        drawerWrapper.add(brand, navSection, footer);
        addToDrawer(drawerWrapper);
    }

    private RouterLink createNavLink(String text, String svgData, Class<? extends Component> target) {
        RouterLink link = new RouterLink();
        link.setRoute(target);
        link.addClassName("st-nav-link");

        Component svgIcon = createSvgIcon(svgData);
        Span label = new Span(text);

        link.add(svgIcon, label);
        navLinks.add(link);

        // Close overlay drawer immediately upon click
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
        UI.getCurrent().getPage().executeJs(
            "if (window.innerWidth <= 1024) {" +
            "  const al = document.querySelector('vaadin-app-layout');" +
            "  if (al) al.drawerOpened = false;" +
            "  window.dispatchEvent(new CustomEvent('close-overlay-drawer'));" +
            "}"
        );

        String currentPath = event.getLocation().getPath();
        for (RouterLink link : navLinks) {
            String href = link.getHref();
            boolean isActive = currentPath.equals(href)
                    || (href.equals("admin/dashboard") && (currentPath.equals("admin") || currentPath.equals("admin/dashboard")));

            if (isActive) {
                link.addClassName("active");
            } else {
                link.removeClassName("active");
            }
        }
    }
}
