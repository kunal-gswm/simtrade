package com.project.trading.ui.admin;

import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class AdminLayout extends AppLayout {

    private final SecurityService securityService = new SecurityService();

    // SVG Constants
    private static final String SVG_DASHBOARD = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z\" /></svg>";
    private static final String SVG_USERS = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z\" /></svg>";
    private static final String SVG_STOCKS = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M2.25 18L9 11.25l4.306 4.307a11.95 11.95 0 015.814-5.519l2.74-1.22m0 0l-5.94-2.28m5.94 2.28l-2.28 5.941\" /></svg>";
    private static final String SVG_MONITOR = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>";
    private static final String SVG_TOOLS = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M11.42 15.17L17.25 21A2.652 2.652 0 0021 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 11-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 004.486-6.336l-3.276 3.277a3.004 3.004 0 01-2.25-2.25l3.276-3.276a4.5 4.5 0 00-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437l1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008z\" /></svg>";

    public AdminLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("SimTrade Admin");
        logo.addClassNames("text-l", "m-m");
        logo.getStyle()
            .set("font-size", "1.2rem")
            .set("margin", "0")
            .set("font-weight", "600")
            .set("color", "var(--st-text-main)");

        Button logout = new Button("Log out", e -> securityService.logout());
        logout.getStyle()
              .set("margin-right", "16px")
              .set("color", "var(--st-danger-color)")
              .set("background", "transparent")
              .set("border", "1px solid var(--st-danger-color)")
              .set("border-radius", "4px")
              .set("padding", "6px 12px");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");
        header.getStyle()
              .set("background-color", "#ffffff")
              .set("border-bottom", "1px solid var(--st-border-color)")
              .set("height", "60px");

        addToNavbar(header);
    }

    private void createDrawer() {
        Nav nav = new Nav();
        nav.getStyle().set("padding", "16px");

        RouterLink dashboardLink = createNavLink("Overview", SVG_DASHBOARD, AdminDashboardView.class);
        RouterLink usersLink = createNavLink("Users", SVG_USERS, AdminUsersView.class);
        RouterLink stocksLink = createNavLink("Stocks", SVG_STOCKS, AdminStocksView.class);
        RouterLink monitorLink = createNavLink("Trade Monitor", SVG_MONITOR, AdminTradeMonitorView.class);
        RouterLink toolsLink = createNavLink("Simulator & Tools", SVG_TOOLS, AdminToolsView.class);
        
        // Add a link back to normal app
        RouterLink appLink = createNavLink("Exit Admin", SVG_TOOLS, com.project.trading.ui.views.DashboardView.class);
        appLink.getStyle().set("margin-top", "auto").set("color", "var(--st-danger-color)");

        nav.add(dashboardLink, usersLink, stocksLink, monitorLink, toolsLink, appLink);
        
        HorizontalLayout drawerHeader = new HorizontalLayout();
        drawerHeader.getStyle().set("padding", "16px").set("border-bottom", "1px solid var(--st-border-color)");
        H1 adminText = new H1("Admin Portal");
        adminText.getStyle().set("font-size", "1rem").set("margin", "0").set("color", "var(--st-text-main)");
        drawerHeader.add(adminText);

        addToDrawer(drawerHeader, nav);
    }

    private RouterLink createNavLink(String text, String svgCode, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        RouterLink link = new RouterLink(navigationTarget);
        link.addClassName("st-nav-link");
        
        com.vaadin.flow.component.html.Span icon = new com.vaadin.flow.component.html.Span();
        icon.getElement().setProperty("innerHTML", svgCode);
        icon.getStyle().set("width", "20px").set("height", "20px");
        
        com.vaadin.flow.component.html.Span label = new com.vaadin.flow.component.html.Span(text);
        
        HorizontalLayout layout = new HorizontalLayout(icon, label);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        link.add(layout);
        
        link.getStyle()
            .set("display", "block")
            .set("padding", "12px 16px")
            .set("border-radius", "8px")
            .set("color", "var(--st-text-muted)")
            .set("text-decoration", "none")
            .set("margin-bottom", "4px")
            .set("font-weight", "500")
            .set("transition", "background-color 0.2s, color 0.2s");
            
        return link;
    }
}
