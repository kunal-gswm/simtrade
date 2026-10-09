package com.project.trading.ui.admin;

import com.project.trading.model.PlatformStats;
import com.project.trading.service.AdminService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

@Route(value = "admin/dashboard", layout = AdminLayout.class)
@RouteAlias(value = "admin", layout = AdminLayout.class)
@PageTitle("Admin Overview | SimTrade")
public class AdminDashboardView extends VerticalLayout {

    private final AdminService adminService = new AdminService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public AdminDashboardView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "var(--st-bg-color)");

        H1 title = new H1("Platform Overview");
        title.getStyle().set("margin-top", "0").set("color", "var(--st-text-main)");
        add(title);

        PlatformStats stats = adminService.getPlatformStats();
        
        HorizontalLayout topRow = new HorizontalLayout();
        topRow.setWidthFull();
        topRow.setSpacing(true);
        topRow.add(
            createMetricCard("Total Users", String.valueOf(stats.getTotalUsers())),
            createMetricCard("Active Users", String.valueOf(stats.getActiveUsers())),
            createMetricCard("Active Stocks", String.valueOf(stats.getActiveStocks()))
        );

        HorizontalLayout bottomRow = new HorizontalLayout();
        bottomRow.setWidthFull();
        bottomRow.setSpacing(true);
        bottomRow.add(
            createMetricCard("Total Trades", String.valueOf(stats.getTotalTrades())),
            createMetricCard("Total Traded Value", currencyFormat.format(stats.getTotalTradedValue()))
        );

        add(topRow, bottomRow);

        if (stats.getTopStocksByTrades() != null && !stats.getTopStocksByTrades().isEmpty()) {
            add(createTopStocksSection(stats.getTopStocksByTrades()));
        }
    }

    private Component createMetricCard(String label, String value) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid var(--st-border-color)")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("flex", "1")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "8px");

        Span title = new Span(label);
        title.getStyle().set("font-size", "0.9rem").set("color", "var(--st-text-muted)");

        Span val = new Span(value);
        val.getStyle().set("font-size", "1.8rem").set("font-weight", "600").set("color", "var(--st-primary-color)");
        
        card.add(title, val);
        return card;
    }

    private Component createTopStocksSection(Map<String, Integer> topStocks) {
        Div section = new Div();
        section.setWidth("100%");
        section.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid var(--st-border-color)")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("margin-top", "24px");

        H2 header = new H2("Top Stocks by Trade Volume");
        header.getStyle().set("font-size", "1.2rem").set("margin-top", "0");
        section.add(header);
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        
        for (Map.Entry<String, Integer> entry : topStocks.entrySet()) {
            Div stockCard = new Div();
            stockCard.getStyle()
                     .set("flex", "1")
                     .set("padding", "12px")
                     .set("background", "var(--st-bg-hover)")
                     .set("border-radius", "6px")
                     .set("text-align", "center");
            
            Span symbol = new Span(entry.getKey());
            symbol.getStyle().set("display", "block").set("font-weight", "bold").set("font-size", "1.1rem");
            
            Span trades = new Span(entry.getValue() + " trades");
            trades.getStyle().set("display", "block").set("color", "var(--st-text-muted)").set("font-size", "0.9rem");
            
            stockCard.add(symbol, trades);
            layout.add(stockCard);
        }
        
        section.add(layout);
        return section;
    }
}
