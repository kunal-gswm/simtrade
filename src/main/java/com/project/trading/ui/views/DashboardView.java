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

@Route(value = "ui/dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | SimTrade")
public class DashboardView extends VerticalLayout {

    private final SecurityService securityService = new SecurityService();
    private final PortfolioService portfolioService = new PortfolioService();
    private final TradingService tradingService = new TradingService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    public DashboardView() {
        addClassName("st-page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) {
            return; // AuthInitListener will redirect
        }

        Div headerContainer = new Div();
        headerContainer.addClassName("st-page-header");
        
        String rawName = user.getUsername();
        if (rawName == null || rawName.isEmpty()) rawName = "User";
        String formattedUsername = rawName.substring(0, 1).toUpperCase() + rawName.substring(1).toLowerCase();

        H1 title = new H1("Welcome back, " + formattedUsername);
        title.addClassName("st-page-title");
        
        Paragraph subtitle = new Paragraph("Here's your portfolio overview.");
        subtitle.addClassName("st-page-subtitle");
        
        headerContainer.add(title, subtitle);
        add(headerContainer);

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
        cards.addClassName("st-stats-row");

        cards.add(
                createMetricCard("Available Cash", currencyFormat.format(summary.cash), "neutral", 
                        "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M21 12a2.25 2.25 0 00-2.25-2.25H15a3 3 0 11-6 0H5.25A2.25 2.25 0 003 12m18 0v6a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 18v-6m18 0V9M3 12V9m18 0a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 9m18 0V6a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 6v3\" /></svg>"),
                createMetricCard("Holdings Value", currencyFormat.format(summary.totalCurrentValue), "neutral",
                        "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M10.5 6a7.5 7.5 0 107.5 7.5h-7.5V6z\" /><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M13.5 10.5H21A7.5 7.5 0 0013.5 3v7.5z\" /></svg>"),
                createMetricCard("Total Net Worth", currencyFormat.format(summary.netWorth), "neutral",
                        "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z\" /></svg>"),
                createMetricCard("Total Profit/Loss", currencyFormat.format(summary.overallPnl), summary.overallPnl >= 0 ? "positive" : "negative",
                        "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M2.25 18L9 11.25l4.306 4.307a11.95 11.95 0 015.814-5.519l2.74-1.22m0 0l-5.94-2.28m5.94 2.28l-2.28 5.941\" /></svg>")
        );
        return cards;
    }

    private Component createMetricCard(String label, String value, String type, String svgData) {
        Div card = new Div();
        card.addClassName("st-stat-card");

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(Alignment.CENTER);
        header.setSpacing(true);
        
        Span iconSpan = new Span();
        iconSpan.getElement().setProperty("innerHTML", svgData);
        iconSpan.getStyle()
                .set("width", "18px")
                .set("height", "18px")
                .set("color", "var(--st-text-muted)")
                .set("display", "inline-flex");

        Span title = new Span(label);
        title.addClassName("st-stat-label");
        title.getStyle().set("margin-bottom", "0");

        header.add(iconSpan, title);
        header.getStyle().set("margin-bottom", "8px");

        Span val = new Span(value);
        val.addClassName("st-stat-value");
        val.getStyle().set("font-feature-settings", "\"tnum\"");
        
        if ("positive".equals(type)) {
            val.addClassName("st-positive");
        } else if ("negative".equals(type)) {
            val.addClassName("st-negative");
        }

        card.add(header, val);
        return card;
    }

    private Component createRecentTradesSection(List<Trade> recentTrades) {
        Div section = new Div();
        section.setWidth("100%");
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "20px");

        H2 header = new H2("Recent Trades");
        header.addClassName("st-section-title");
        section.add(header);

        if (recentTrades == null || recentTrades.isEmpty()) {
            Div emptyState = new Div();
            emptyState.addClassName("st-empty-state");
            
            Span icon = new Span();
            icon.getElement().setProperty("innerHTML", "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z\" /></svg>");
            
            H2 emptyTitle = new H2("No recent trades");
            emptyTitle.addClassName("st-empty-title");
            
            Paragraph emptyMsg = new Paragraph("You haven't made any trades yet. Head over to the market to get started.");
            emptyMsg.addClassName("st-empty-text");
            
            RouterLink marketLink = new RouterLink("Browse Market", MarketView.class);
            marketLink.addClassName("st-btn");
            marketLink.addClassName("st-btn-primary");
            
            emptyState.add(icon, emptyTitle, emptyMsg, marketLink);
            section.add(emptyState);
        } else {
            Div tableContainer = new Div();
            tableContainer.setWidthFull();
            
            HorizontalLayout tableHeader = new HorizontalLayout();
            tableHeader.setWidthFull();
            tableHeader.getStyle().set("border-bottom", "1px solid var(--st-border)")
                                  .set("padding-bottom", "8px")
                                  .set("font-weight", "500")
                                  .set("font-size", "13px")
                                  .set("color", "var(--st-text-secondary)");
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
                row.getStyle().set("padding", "12px 0")
                            .set("border-bottom", "1px solid var(--st-border)")
                            .set("font-size", "14px");
                row.setAlignItems(Alignment.CENTER);
                
                String dateStr = dateFormat.format(trade.getExecutedAt());
                Span typeSpan = new Span(trade.getType().name());
                typeSpan.addClassName("st-badge");
                if (trade.getType().name().equals("BUY")) {
                    typeSpan.addClassName("st-badge-buy");
                } else {
                    typeSpan.addClassName("st-badge-sell");
                }
                
                Span symbolSpan = new Span(trade.getStockSymbol());
                symbolSpan.addClassName("st-symbol");
                
                Span qtySpan = new Span(String.valueOf(trade.getQuantity()));
                qtySpan.getStyle().set("font-feature-settings", "\"tnum\"");
                
                Span priceSpan = new Span(currencyFormat.format(trade.getPrice()));
                priceSpan.getStyle().set("font-feature-settings", "\"tnum\"");
                
                Span totalSpan = new Span(currencyFormat.format(trade.getTotalAmount()));
                totalSpan.getStyle().set("font-feature-settings", "\"tnum\"");
                
                row.add(
                        createCell(dateStr, 2),
                        createCell(symbolSpan, 1),
                        createCell(typeSpan, 1),
                        createCell(qtySpan, 1),
                        createCell(priceSpan, 1),
                        createCell(totalSpan, 1)
                );
                tableContainer.add(row);
            }
            section.add(tableContainer);
            
            RouterLink historyLink = new RouterLink("View all trades →", HistoryView.class);
            historyLink.getStyle().set("display", "inline-block")
                                  .set("margin-top", "16px")
                                  .set("font-size", "14px")
                                  .set("font-weight", "500")
                                  .set("color", "var(--st-blue)")
                                  .set("text-decoration", "none");
            section.add(historyLink);
        }
        return section;
    }

    private Component createQuickActions() {
        Div section = new Div();
        section.setWidth("100%");
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "20px");

        H2 header = new H2("Quick Links");
        header.addClassName("st-section-title");
        
        VerticalLayout actions = new VerticalLayout();
        actions.setPadding(false);
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "16px");
        
        actions.add(createActionLink("Explore Market", MarketView.class, "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M2.25 18L9 11.25l4.306 4.307a11.95 11.95 0 015.814-5.519l2.74-1.22m0 0l-5.94-2.28m5.94 2.28l-2.28 5.941\" /></svg>"));
        actions.add(createActionLink("View Portfolio", PortfolioView.class, "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M21 12a2.25 2.25 0 00-2.25-2.25H15a3 3 0 11-6 0H5.25A2.25 2.25 0 003 12m18 0v6a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 18v-6m18 0V9M3 12V9m18 0a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 9m18 0V6a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 6v3\" /></svg>"));
        actions.add(createActionLink("Trade History", HistoryView.class, "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z\" /></svg>"));
        actions.add(createActionLink("Account Settings", ProfileView.class, "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z\" /></svg>"));
        
        section.add(header, actions);
        return section;
    }

    private Component createActionLink(String text, Class<? extends Component> viewClass, String svgData) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);
        link.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("width", "100%")
                .set("padding", "12px 16px")
                .set("background", "var(--st-bg-main)")
                .set("border", "1px solid var(--st-border)")
                .set("border-radius", "var(--st-radius)")
                .set("color", "var(--st-text-primary)")
                .set("text-decoration", "none")
                .set("font-weight", "500")
                .set("box-sizing", "border-box")
                .set("transition", "all var(--st-transition)");
        
        link.getElement().addEventListener("mouseover", e -> link.getStyle().set("background", "var(--st-hover-bg)").set("border-color", "var(--st-blue)"));
        link.getElement().addEventListener("mouseout", e -> link.getStyle().set("background", "var(--st-bg-main)").set("border-color", "var(--st-border)"));

        Span icon = new Span();
        icon.getElement().setProperty("innerHTML", svgData);
        icon.getStyle().set("width", "20px").set("height", "20px").set("color", "var(--st-blue)");
        
        Span label = new Span(text);
        
        link.add(icon, label);
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
