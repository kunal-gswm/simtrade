package com.project.trading.ui.views;

import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "ui/dashboard", layout = MainLayout.class)
@PageTitle("Terminal Overview | SimTrade")
public class DashboardView extends VerticalLayout {

    private final SecurityService securityService = new SecurityService();
    private final PortfolioService portfolioService = new PortfolioService();
    private final TradingService tradingService = new TradingService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final NumberFormat percentFormat = NumberFormat.getPercentInstance(new Locale("en", "IN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    public DashboardView() {
        this.percentFormat.setMinimumFractionDigits(2);
        this.percentFormat.setMaximumFractionDigits(2);

        addClassName("st-page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) {
            return; // AuthInitListener handles redirect
        }

        try {
            PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(
                    portfolioService.getPortfolioForUser(user.getId()));
            List<Trade> recentTrades = tradingService.getHistory(user.getId(), 5);

            // Header Group
            add(createHeader(user));

            // 6-Card Institutional Stat Grid
            add(createMetricsGrid(summary));

            // Capital Allocation Bar
            add(createAllocationBar(summary));

            // Split Layout: Recent Trades & Active Positions
            HorizontalLayout splitSection = new HorizontalLayout();
            splitSection.setWidthFull();
            splitSection.setSpacing(true);
            splitSection.getStyle().set("margin-top", "16px").set("flex-wrap", "wrap");

            Component recentTradesSection = createRecentTradesSection(recentTrades);
            Component portfolioSnapshot = createPortfolioSnapshot(summary);

            splitSection.add(recentTradesSection, portfolioSnapshot);
            splitSection.setFlexGrow(3, recentTradesSection);
            splitSection.setFlexGrow(2, portfolioSnapshot);

            add(splitSection);
        } catch (Exception e) {
            Div errContainer = new Div();
            errContainer.addClassName("st-empty-state");
            Paragraph error = new Paragraph("Unable to load terminal data: " + e.getMessage());
            error.addClassName("st-negative");
            errContainer.add(error);
            add(errContainer);
        }
    }

    private Component createHeader(AuthUser user) {
        Div headerContainer = new Div();
        headerContainer.addClassName("st-header-container");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");

        String rawName = user.getUsername();
        if (rawName == null || rawName.isEmpty()) rawName = "Trader";
        String formattedUsername = rawName.substring(0, 1).toUpperCase() + rawName.substring(1).toLowerCase();

        H1 title = new H1("Overview · " + formattedUsername);
        title.addClassName("st-page-title");

        Paragraph subtitle = new Paragraph("Institutional paper-trading account overview and positions summary.");
        subtitle.addClassName("st-page-subtitle");

        titleGroup.add(title, subtitle);

        // Header Quick Actions
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        RouterLink marketLink = new RouterLink();
        marketLink.setRoute(MarketView.class);
        Button marketBtn = new Button("Market Quotes");
        marketBtn.addClassNames("st-btn", "st-btn-primary");
        marketBtn.getElement().removeAttribute("theme");
        marketLink.add(marketBtn);

        RouterLink portLink = new RouterLink();
        portLink.setRoute(PortfolioView.class);
        Button portBtn = new Button("Holdings");
        portBtn.addClassNames("st-btn", "st-btn-secondary");
        portBtn.getElement().removeAttribute("theme");
        portLink.add(portBtn);

        actions.add(marketLink, portLink);
        headerContainer.add(titleGroup, actions);

        return headerContainer;
    }

    private Component createMetricsGrid(PortfolioService.PortfolioSummary summary) {
        Div grid = new Div();
        grid.addClassName("st-stats-row");

        // 1. Total Net Worth
        String pnlSubtext = (summary.isOverallPositive() ? "+" : "") +
                percentFormat.format(summary.totalReturnPct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        grid.add(createMetricCard("Total Net Worth", currencyFormat.format(summary.netWorth),
                summary.isOverallPositive() ? "positive" : "negative", pnlSubtext + " overall return"));

        // 2. Available Cash
        grid.add(createMetricCard("Available Cash", currencyFormat.format(summary.cash),
                "neutral", "Liquid purchasing power"));

        // 3. Current Holdings Value
        grid.add(createMetricCard("Holdings Value", currencyFormat.format(summary.totalCurrentValue),
                "neutral", "Cost: " + currencyFormat.format(summary.totalInvested)));

        // 4. Overall P&L
        String overallSign = summary.overallPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        grid.add(createMetricCard("Overall P&L", overallSign + currencyFormat.format(summary.overallPnl),
                summary.isOverallPositive() ? "positive" : "negative", "Realized + Unrealized"));

        // 5. Realized P&L
        String realizedSign = summary.realizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        grid.add(createMetricCard("Realized P&L", realizedSign + currencyFormat.format(summary.realizedPnl),
                summary.realizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "positive" : "negative", "Closed positions"));

        // 6. Unrealized P&L
        String unrealizedSign = summary.totalUnrealizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        grid.add(createMetricCard("Unrealized P&L", unrealizedSign + currencyFormat.format(summary.totalUnrealizedPnl),
                summary.totalUnrealizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "positive" : "negative", "Floating positions"));

        return grid;
    }

    private Component createAllocationBar(PortfolioService.PortfolioSummary summary) {
        Div container = new Div();
        container.addClassName("st-allocation-container");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span title = new Span("Capital Allocation");
        title.getStyle().set("font-size", "12px").set("font-weight", "600").set("text-transform", "uppercase").set("letter-spacing", "0.05em").set("color", "var(--st-text-secondary)");

        BigDecimal total = summary.netWorth;
        double cashPct = 100.0;
        double equityPct = 0.0;

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            cashPct = summary.cash.divide(total, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
            equityPct = summary.totalCurrentValue.divide(total, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
            if (cashPct > 100.0) cashPct = 100.0;
            if (equityPct > 100.0) equityPct = 100.0;
        }

        Span ratioText = new Span(String.format(Locale.US, "Cash: %.1f%%  ·  Equity: %.1f%%", cashPct, equityPct));
        ratioText.getStyle().set("font-size", "12px").set("color", "var(--st-text-muted)").set("font-feature-settings", "\"tnum\"");

        top.add(title, ratioText);

        Div bar = new Div();
        bar.addClassName("st-allocation-bar");

        Div cashSeg = new Div();
        cashSeg.addClassName("st-allocation-cash");
        cashSeg.getStyle().set("width", String.format(Locale.US, "%.1f%%", cashPct));

        Div eqSeg = new Div();
        eqSeg.addClassName("st-allocation-equity");
        eqSeg.getStyle().set("width", String.format(Locale.US, "%.1f%%", equityPct));

        bar.add(cashSeg, eqSeg);

        Div legend = new Div();
        legend.addClassName("st-allocation-legend");

        Span cashDot = new Span();
        cashDot.addClassName("st-legend-dot");
        cashDot.getStyle().set("background-color", "var(--st-blue)");
        Span cashLabel = new Span(cashDot, new Span("Available Cash (" + currencyFormat.format(summary.cash) + ")"));

        Span eqDot = new Span();
        eqDot.addClassName("st-legend-dot");
        eqDot.getStyle().set("background-color", "var(--st-green)");
        Span eqLabel = new Span(eqDot, new Span("Holdings Equity (" + currencyFormat.format(summary.totalCurrentValue) + ")"));

        legend.add(cashLabel, eqLabel);
        container.add(top, bar, legend);

        return container;
    }

    private Component createMetricCard(String label, String value, String type, String subtext) {
        Div card = new Div();
        card.addClassName("st-stat-card");

        Div header = new Div();
        header.addClassName("st-stat-header");

        Span title = new Span(label);
        title.addClassName("st-stat-label");
        header.add(title);

        Span val = new Span(value);
        val.addClassName("st-stat-value");

        if ("positive".equals(type)) {
            val.addClassName("st-positive");
        } else if ("negative".equals(type)) {
            val.addClassName("st-negative");
        }

        Span sub = new Span(subtext);
        sub.addClassName("st-stat-subtext");

        card.add(header, val, sub);
        return card;
    }

    private Component createRecentTradesSection(List<Trade> recentTrades) {
        Div section = new Div();
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "16px 20px");

        HorizontalLayout secHeader = new HorizontalLayout();
        secHeader.setWidthFull();
        secHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        secHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 header = new H2("Recent Executions");
        header.addClassName("st-section-title");

        RouterLink historyLink = new RouterLink("Full Audit Log →", HistoryView.class);
        historyLink.getStyle().set("font-size", "12px").set("color", "var(--st-blue)").set("text-decoration", "none").set("font-weight", "500");

        secHeader.add(header, historyLink);
        section.add(secHeader);

        if (recentTrades == null || recentTrades.isEmpty()) {
            Div emptyState = new Div();
            emptyState.addClassName("st-empty-state");

            Span icon = new Span();
            icon.getElement().setProperty("innerHTML", "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z\" /></svg>");

            H2 emptyTitle = new H2("No executed trades");
            emptyTitle.addClassName("st-empty-title");

            Paragraph emptyMsg = new Paragraph("No simulated trades have been placed yet. Explore listed stocks to execute an order.");
            emptyMsg.addClassName("st-empty-text");

            RouterLink marketLink = new RouterLink("Explore Market Quotes", MarketView.class);
            marketLink.addClassNames("st-btn", "st-btn-primary");

            emptyState.add(icon, emptyTitle, emptyMsg, marketLink);
            section.add(emptyState);
        } else {
            Div tableContainer = new Div();
            tableContainer.setWidthFull();
            tableContainer.getStyle().set("margin-top", "12px");

            HorizontalLayout tableHeader = new HorizontalLayout();
            tableHeader.setWidthFull();
            tableHeader.getStyle().set("border-bottom", "1px solid var(--st-border)")
                    .set("padding", "8px 0")
                    .set("font-weight", "600")
                    .set("font-size", "11px")
                    .set("text-transform", "uppercase")
                    .set("letter-spacing", "0.05em")
                    .set("color", "var(--st-text-secondary)");

            tableHeader.add(
                    createCell("Time", 2),
                    createCell("Symbol", 1),
                    createCell("Side", 1),
                    createCell("Qty", 1),
                    createCell("Price", 1),
                    createCell("Total", 1)
            );
            tableContainer.add(tableHeader);

            for (Trade trade : recentTrades) {
                HorizontalLayout row = new HorizontalLayout();
                row.setWidthFull();
                row.getStyle().set("padding", "10px 0")
                        .set("border-bottom", "1px solid var(--st-border-subtle)")
                        .set("font-size", "13px");
                row.setAlignItems(FlexComponent.Alignment.CENTER);

                String dateStr = dateFormat.format(trade.getExecutedAt());
                Span typeSpan = new Span(trade.getType().name());
                typeSpan.addClassName("st-badge");
                if ("BUY".equalsIgnoreCase(trade.getType().name())) {
                    typeSpan.addClassName("st-badge-buy");
                } else {
                    typeSpan.addClassName("st-badge-sell");
                }

                Span symbolSpan = new Span(trade.getStockSymbol());
                symbolSpan.addClassName("st-symbol");

                Span qtySpan = new Span(String.valueOf(trade.getQuantity()));
                qtySpan.addClassName("tabular-num");

                Span priceSpan = new Span(currencyFormat.format(trade.getPrice()));
                priceSpan.addClassName("tabular-num");

                Span totalSpan = new Span(currencyFormat.format(trade.getTotalAmount()));
                totalSpan.addClassName("tabular-num");

                row.add(
                        createCell(new Span(dateStr), 2),
                        createCell(symbolSpan, 1),
                        createCell(typeSpan, 1),
                        createCell(qtySpan, 1),
                        createCell(priceSpan, 1),
                        createCell(totalSpan, 1)
                );
                tableContainer.add(row);
            }
            section.add(tableContainer);
        }
        return section;
    }

    private Component createPortfolioSnapshot(PortfolioService.PortfolioSummary summary) {
        Div section = new Div();
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "16px 20px");

        HorizontalLayout secHeader = new HorizontalLayout();
        secHeader.setWidthFull();
        secHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        secHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 header = new H2("Active Positions");
        header.addClassName("st-section-title");

        RouterLink portLink = new RouterLink("Manage →", PortfolioView.class);
        portLink.getStyle().set("font-size", "12px").set("color", "var(--st-blue)").set("text-decoration", "none").set("font-weight", "500");

        secHeader.add(header, portLink);
        section.add(secHeader);

        if (summary.rows.isEmpty()) {
            Div empty = new Div();
            empty.addClassName("st-empty-state");
            empty.getStyle().set("padding", "28px 12px");
            Span msg = new Span("No open equity positions held.");
            msg.getStyle().set("color", "var(--st-text-muted)").set("font-size", "13px");
            empty.add(msg);
            section.add(empty);
        } else {
            Div tableContainer = new Div();
            tableContainer.setWidthFull();
            tableContainer.getStyle().set("margin-top", "12px");

            HorizontalLayout tableHeader = new HorizontalLayout();
            tableHeader.setWidthFull();
            tableHeader.getStyle().set("border-bottom", "1px solid var(--st-border)")
                    .set("padding", "8px 0")
                    .set("font-weight", "600")
                    .set("font-size", "11px")
                    .set("text-transform", "uppercase")
                    .set("letter-spacing", "0.05em")
                    .set("color", "var(--st-text-secondary)");

            tableHeader.add(
                    createCell("Symbol", 2),
                    createCell("Shares", 1),
                    createCell("Value", 2),
                    createCell("Unrealized P&L", 2)
            );
            tableContainer.add(tableHeader);

            int limit = Math.min(summary.rows.size(), 4);
            for (int i = 0; i < limit; i++) {
                PortfolioService.HoldingRow row = summary.rows.get(i);
                HorizontalLayout posRow = new HorizontalLayout();
                posRow.setWidthFull();
                posRow.getStyle().set("padding", "10px 0")
                        .set("border-bottom", "1px solid var(--st-border-subtle)")
                        .set("font-size", "13px");
                posRow.setAlignItems(FlexComponent.Alignment.CENTER);

                Span sym = new Span(row.symbol);
                sym.addClassName("st-symbol");

                Span qty = new Span(String.valueOf(row.quantity));
                qty.addClassName("tabular-num");

                Span val = new Span(currencyFormat.format(row.currentValue));
                val.addClassName("tabular-num");

                String sign = row.unrealizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                Span pnl = new Span(sign + currencyFormat.format(row.unrealizedPnl));
                pnl.addClassName("tabular-num");
                if (row.unrealizedPnl.compareTo(BigDecimal.ZERO) > 0) {
                    pnl.addClassName("st-positive");
                } else if (row.unrealizedPnl.compareTo(BigDecimal.ZERO) < 0) {
                    pnl.addClassName("st-negative");
                }

                posRow.add(
                        createCell(sym, 2),
                        createCell(qty, 1),
                        createCell(val, 2),
                        createCell(pnl, 2)
                );
                tableContainer.add(posRow);
            }
            section.add(tableContainer);
        }

        return section;
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
