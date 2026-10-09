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
        H1 logo = new H1("SimTrade");
        logo.addClassNames("text-l", "m-m");

        SecurityService securityService = new SecurityService();
        AuthUser user = securityService.getAuthenticatedUser();

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        if (user != null) {
            Button logout = new Button("Log out", e -> securityService.logout());
            HorizontalLayout userInfo = new HorizontalLayout(new Span(user.getUsername()), logout);
            userInfo.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            userInfo.getStyle().set("margin-left", "auto");
            header.add(userInfo);
        }

        addToNavbar(header);
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
