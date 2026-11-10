package com.project.trading.ui.admin;

import com.project.trading.model.PlatformStats;
import com.project.trading.service.AdminService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
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
@PageTitle("Platform Overview | SimTrade Admin")
public class AdminDashboardView extends VerticalLayout {

    private final AdminService adminService = new AdminService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public AdminDashboardView() {
        addClassName("st-page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Div headerContainer = new Div();
        headerContainer.addClassName("st-header-container");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");

        H1 title = new H1("Platform Operations Overview");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Real-time aggregate platform metrics, trade volumes, and active market instruments.");
        subtitle.addClassName("st-page-subtitle");

        titleGroup.add(title, subtitle);
        headerContainer.add(titleGroup);
        add(headerContainer);

        PlatformStats stats = adminService.getPlatformStats();

        // Top Stats Row
        Div statsGrid = new Div();
        statsGrid.addClassName("st-stats-row");

        statsGrid.add(
                createMetricCard("Total Users", String.valueOf(stats.getTotalUsers()), "Registered accounts"),
                createMetricCard("Active Users", String.valueOf(stats.getActiveUsers()), "Permitted traders"),
                createMetricCard("Active Stocks", String.valueOf(stats.getActiveStocks()), "Tradable symbols"),
                createMetricCard("Total Trades", String.valueOf(stats.getTotalTrades()), "Lifetime executions"),
                createMetricCard("Total Traded Value", currencyFormat.format(stats.getTotalTradedValue()), "Gross platform turnover")
        );

        add(statsGrid);

        if (stats.getTopStocksByTrades() != null && !stats.getTopStocksByTrades().isEmpty()) {
            add(createTopStocksSection(stats.getTopStocksByTrades()));
        }
    }

    private Component createMetricCard(String label, String value, String subtext) {
        Div card = new Div();
        card.addClassName("st-stat-card");

        Div header = new Div();
        header.addClassName("st-stat-header");
        Span labelSpan = new Span(label);
        labelSpan.addClassName("st-stat-label");
        header.add(labelSpan);

        Span val = new Span(value);
        val.addClassName("st-stat-value");

        Span sub = new Span(subtext);
        sub.addClassName("st-stat-subtext");

        card.add(header, val, sub);
        return card;
    }

    private Component createTopStocksSection(Map<String, Integer> topStocks) {
        Div section = new Div();
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "20px").set("margin-top", "16px").set("width", "100%").set("box-sizing", "border-box");

        H2 header = new H2("Top Symbols by Trade Frequency");
        header.addClassName("st-section-title");
        section.add(header);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.getStyle().set("margin-top", "12px").set("flex-wrap", "wrap");

        for (Map.Entry<String, Integer> entry : topStocks.entrySet()) {
            Div stockCard = new Div();
            stockCard.getStyle()
                    .set("flex", "1")
                    .set("min-width", "140px")
                    .set("padding", "14px")
                    .set("background", "var(--st-bg-surface-secondary)")
                    .set("border", "1px solid var(--st-border)")
                    .set("border-radius", "var(--st-radius)")
                    .set("text-align", "center");

            Span symbol = new Span(entry.getKey());
            symbol.addClassName("st-symbol");
            symbol.getStyle().set("display", "block").set("font-size", "15px").set("margin-bottom", "4px");

            Span trades = new Span(entry.getValue() + " executions");
            trades.getStyle().set("display", "block").set("color", "var(--st-text-muted)").set("font-size", "12px").set("font-feature-settings", "\"tnum\"");

            stockCard.add(symbol, trades);
            layout.add(stockCard);
        }

        section.add(layout);
        return section;
    }
}
