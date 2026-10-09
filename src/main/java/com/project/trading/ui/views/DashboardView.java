package com.project.trading.ui.views;

import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
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
import com.vaadin.flow.router.RouterLink;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | SimTrade")
public class DashboardView extends VerticalLayout {

    private final SecurityService securityService = new SecurityService();
    private final PortfolioService portfolioService = new PortfolioService();
    private final TradingService tradingService = new TradingService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    public DashboardView() {
        addClassName("dashboard-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) {
            return; // AuthInitListener will redirect
        }

        H1 title = new H1("Welcome, " + user.getUsername());
        title.getStyle().set("margin-top", "0").set("color", "var(--st-text-main)");
        add(title);

        try {
            PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(
                    portfolioService.getPortfolioForUser(user.getId()));
            List<Trade> recentTrades = tradingService.getHistory(user.getId(), 5);

            add(createMetricsCards(summary));
            
            HorizontalLayout bottomLayout = new HorizontalLayout();
            bottomLayout.setWidthFull();
            bottomLayout.setSpacing(true);
            bottomLayout.getStyle().set("margin-top", "24px");
            
            bottomLayout.add(createRecentTradesSection(recentTrades), createQuickActions());
            bottomLayout.setFlexGrow(2, bottomLayout.getComponentAt(0));
            bottomLayout.setFlexGrow(1, bottomLayout.getComponentAt(1));
            
            add(bottomLayout);
        } catch (Exception e) {
            Paragraph error = new Paragraph("Unable to load dashboard data. Please try again later.");
            error.getStyle().set("color", "var(--st-danger-color)");
            add(error);
        }
    }

    private Component createMetricsCards(PortfolioService.PortfolioSummary summary) {
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.setSpacing(true);

        cards.add(
                createMetricCard("Available Cash", currencyFormat.format(summary.cash), "neutral"),
                createMetricCard("Holdings Value", currencyFormat.format(summary.totalCurrentValue), "neutral"),
                createMetricCard("Total Net Worth", currencyFormat.format(summary.netWorth), "neutral"),
                createMetricCard("Total Profit/Loss", currencyFormat.format(summary.overallPnl), summary.overallPnl >= 0 ? "positive" : "negative")
        );
        return cards;
    }

    private Component createMetricCard(String label, String value, String type) {
        Div card = new Div();
        card.addClassName("metric-card");
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
        val.getStyle().set("font-size", "1.5rem").set("font-weight", "600");
        
        if ("positive".equals(type)) {
            val.getStyle().set("color", "var(--st-success-color)");
        } else if ("negative".equals(type)) {
            val.getStyle().set("color", "var(--st-danger-color)");
        } else {
            val.getStyle().set("color", "var(--st-text-main)");
        }

        card.add(title, val);
        return card;
    }

    private Component createRecentTradesSection(List<Trade> recentTrades) {
        Div section = new Div();
        section.setWidth("100%");
        section.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid var(--st-border-color)")
                .set("border-radius", "8px")
                .set("padding", "20px");

        H2 header = new H2("Recent Trades");
        header.getStyle().set("font-size", "1.2rem").set("margin-top", "0");
        section.add(header);

        if (recentTrades == null || recentTrades.isEmpty()) {
            Paragraph emptyMsg = new Paragraph("You have no recent trades.");
            emptyMsg.getStyle().set("color", "var(--st-text-muted)");
            RouterLink marketLink = new RouterLink("Browse Market to get started", MarketView.class);
            marketLink.getStyle().set("color", "var(--st-primary-color)");
            section.add(emptyMsg, marketLink);
        } else {
            Div tableContainer = new Div();
            tableContainer.setWidthFull();
            
            HorizontalLayout tableHeader = new HorizontalLayout();
            tableHeader.setWidthFull();
            tableHeader.getStyle().set("border-bottom", "1px solid var(--st-border-color)")
                                  .set("padding-bottom", "8px")
                                  .set("font-weight", "bold")
                                  .set("color", "var(--st-text-muted)");
            tableHeader.add(
                createCell("Date", 2),
                createCell("Symbol", 1),
                createCell("Type", 1),
                createCell("Qty", 1),
                createCell("Price", 1),
                createCell("Total Value", 1)
            );
            tableContainer.add(tableHeader);
            
            for (Trade trade : recentTrades) {
                HorizontalLayout row = new HorizontalLayout();
                row.setWidthFull();
                row.getStyle().set("padding", "12px 0").set("border-bottom", "1px solid var(--st-bg-hover)");
                row.setAlignItems(Alignment.CENTER);
                
                String dateStr = dateFormat.format(trade.getExecutedAt());
                Span typeSpan = new Span(trade.getType().name());
                typeSpan.getStyle()
                        .set("padding", "4px 8px")
                        .set("border-radius", "4px")
                        .set("font-size", "0.85rem")
                        .set("font-weight", "500");
                if (trade.getType().name().equals("BUY")) {
                    typeSpan.getStyle().set("background", "rgba(16, 185, 129, 0.1)").set("color", "var(--st-success-color)");
                } else {
                    typeSpan.getStyle().set("background", "rgba(239, 68, 68, 0.1)").set("color", "var(--st-danger-color)");
                }
                
                row.add(
                        createCell(dateStr, 2),
                        createCell(trade.getStockSymbol(), 1),
                        createCell(typeSpan, 1),
                        createCell(String.valueOf(trade.getQuantity()), 1),
                        createCell(currencyFormat.format(trade.getPrice()), 1),
                        createCell(currencyFormat.format(trade.getTotalAmount()), 1)
                );
                tableContainer.add(row);
            }
            section.add(tableContainer);
        }
        return section;
    }

    private Component createQuickActions() {
        Div section = new Div();
        section.setWidth("100%");
        section.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid var(--st-border-color)")
                .set("border-radius", "8px")
                .set("padding", "20px");

        H2 header = new H2("Quick Actions");
        header.getStyle().set("font-size", "1.2rem").set("margin-top", "0");
        
        VerticalLayout actions = new VerticalLayout();
        actions.setPadding(false);
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "16px");
        
        actions.add(createActionLink("Browse Market", MarketView.class));
        actions.add(createActionLink("View Portfolio", PortfolioView.class));
        actions.add(createActionLink("Trade History", HistoryView.class));
        actions.add(createActionLink("My Profile", ProfileView.class));
        
        section.add(header, actions);
        return section;
    }

    private Component createActionLink(String text, Class<? extends Component> viewClass) {
        RouterLink link = new RouterLink(text, viewClass);
        link.getStyle()
                .set("display", "block")
                .set("width", "100%")
                .set("padding", "12px")
                .set("background", "var(--st-bg-hover)")
                .set("border-radius", "6px")
                .set("color", "var(--st-primary-color)")
                .set("text-decoration", "none")
                .set("font-weight", "500");
        return link;
    }

    private Component createCell(String text, int flexGrow) {
        Span span = new Span(text);
        span.getStyle().set("flex", String.valueOf(flexGrow));
        return span;
    }
    
    private Component createCell(Component component, int flexGrow) {
        Div div = new Div(component);
        div.getStyle().set("flex", String.valueOf(flexGrow));
        return div;
    }
}
