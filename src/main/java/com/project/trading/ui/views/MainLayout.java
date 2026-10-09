package com.project.trading.ui.views;

import com.project.trading.model.AuthUser;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("SimTrade Pro");
        logo.addClassNames("text-l", "m-m");
        logo.getStyle().set("color", "var(--lumo-primary-text-color)");
        logo.getStyle().set("font-weight", "bold");
        logo.getStyle().set("letter-spacing", "1px");

        SecurityService securityService = new SecurityService();
        AuthUser user = securityService.getAuthenticatedUser();

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        header.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        header.getStyle().set("background-color", "var(--lumo-base-color)");

        if (user != null) {
            Span usernameBadge = new Span(user.getUsername().toUpperCase());
            usernameBadge.getStyle()
                .set("background-color", "var(--lumo-primary-color-10pct)")
                .set("color", "var(--lumo-primary-color)")
                .set("padding", "4px 8px")
                .set("border-radius", "4px")
                .set("font-weight", "600");

            Button logout = new Button("Log out", e -> securityService.logout());
            logout.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
            
            HorizontalLayout userInfo = new HorizontalLayout(usernameBadge, logout);
            userInfo.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            userInfo.getStyle().set("margin-left", "auto");
            header.add(userInfo);
        }

        addToNavbar(header);
        setPrimarySection(Section.DRAWER);
    }

    private void createDrawer() {
        RouterLink marketLink = new RouterLink("Market", MarketView.class);
        RouterLink portfolioLink = new RouterLink("Portfolio", PortfolioView.class);
        RouterLink historyLink = new RouterLink("Trade History", HistoryView.class);

        VerticalLayout list = new VerticalLayout(marketLink, portfolioLink, historyLink);
        list.setPadding(true);
        addToDrawer(list);
    }
}
