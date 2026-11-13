package com.project.trading.ui.views;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "ui/dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Terminal Overview | SimTrade")
public class DashboardView extends VerticalLayout {

    private final SecurityService securityService = new SecurityService();
    private final PortfolioService portfolioService = new PortfolioService();
    private final TradingService tradingService = new TradingService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final NumberFormat percentFormat = NumberFormat.getPercentInstance(new Locale("en", "IN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    private AuthUser currentUser;
    private MarketDataBroadcaster.Registration broadcastRegistration;

    // Live KPI spans
    private final Span netWorthValue = new Span("-");
    private final Span netWorthSubtext = new Span();
    private final Span cashValue = new Span("-");
    private final Span holdingsValue = new Span("-");
    private final Span holdingsSubtext = new Span();
    private final Span overallPnlValue = new Span("-");
    private final Span realizedPnlValue = new Span("-");
    private final Span unrealizedPnlValue = new Span("-");

    // Dynamic containers
    private final Div allocationContainer = new Div();
    private final Div recentTradesContainer = new Div();
    private final Div activePositionsContainer = new Div();

    public DashboardView() {
        this.percentFormat.setMinimumFractionDigits(2);
        this.percentFormat.setMaximumFractionDigits(2);

        addClassName("st-page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        currentUser = securityService.getAuthenticatedUser();
        if (currentUser == null) {
            return;
        }

        add(createHeader(currentUser));
        add(createMetricsGrid());
        add(createAllocationSection());

        HorizontalLayout splitSection = new HorizontalLayout();
        splitSection.setWidthFull();
        splitSection.setSpacing(true);
        splitSection.addClassName("st-split-section");
        splitSection.getStyle().set("margin-top", "12px");

        Component recentTradesSection = createRecentTradesSection();
        Component portfolioSnapshot = createPortfolioSnapshot();
        recentTradesSection.getStyle().set("min-width", "0");
        portfolioSnapshot.getStyle().set("min-width", "0");

        splitSection.add(recentTradesSection, portfolioSnapshot);
        splitSection.setFlexGrow(3, recentTradesSection);
        splitSection.setFlexGrow(2, portfolioSnapshot);

        add(splitSection);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();

        // Refresh immediately on attach
        refreshDashboardData();

        // Register to centralized market broadcaster for real-time push events
        broadcastRegistration = MarketDataBroadcaster.register(ui, new MarketDataBroadcaster.BroadcastListener() {
            @Override
            public void onPriceUpdate(MarketDataBroadcaster.PriceUpdate event) {
                refreshDashboardData();
            }

            @Override
            public void onTradeUpdate(MarketDataBroadcaster.TradeUpdate event) {
                if (event.trade() != null && currentUser != null && event.trade().getUserId() == currentUser.getId()) {
                    refreshDashboardData();
                }
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (broadcastRegistration != null) {
            broadcastRegistration.remove();
            broadcastRegistration = null;
        }
        super.onDetach(detachEvent);
    }

    private synchronized void refreshDashboardData() {
        if (currentUser == null) return;
        try {
            PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(currentUser.getId());
            List<Trade> recentTrades = tradingService.getHistory(currentUser.getId(), 5);

            // 1. Total Net Worth
            netWorthValue.setText(currencyFormat.format(summary.netWorth));
            String pnlSign = summary.isOverallPositive() ? "+" : "";
            netWorthSubtext.setText(pnlSign + percentFormat.format(summary.totalReturnPct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)) + " return");

            // 2. Available Cash
            cashValue.setText(currencyFormat.format(summary.cash));

            // 3. Current Holdings Value
            holdingsValue.setText(currencyFormat.format(summary.totalCurrentValue));
            holdingsSubtext.setText("Cost: " + currencyFormat.format(summary.totalInvested));

            // 4. Overall P&L
            String overallSign = summary.overallPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            overallPnlValue.setText(overallSign + currencyFormat.format(summary.overallPnl));
            setPnlColor(overallPnlValue, summary.overallPnl);

            // 5. Realized P&L
            String realizedSign = summary.realizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            realizedPnlValue.setText(realizedSign + currencyFormat.format(summary.realizedPnl));
            setPnlColor(realizedPnlValue, summary.realizedPnl);

            // 6. Unrealized P&L
            String unrealizedSign = summary.totalUnrealizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            unrealizedPnlValue.setText(unrealizedSign + currencyFormat.format(summary.totalUnrealizedPnl));
            setPnlColor(unrealizedPnlValue, summary.totalUnrealizedPnl);

            // Update Allocation Bar
            updateAllocationBar(summary);

            // Update Recent Trades
            updateRecentTrades(recentTrades);

            // Update Active Positions
            updateActivePositions(summary);

        } catch (Exception e) {
            // Keep existing displayed state if a transient query error occurs
        }
    }

    private void setPnlColor(Span span, BigDecimal value) {
        span.removeClassName("st-positive");
        span.removeClassName("st-negative");
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            span.addClassName("st-positive");
        } else if (value.compareTo(BigDecimal.ZERO) < 0) {
            span.addClassName("st-negative");
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

        Paragraph subtitle = new Paragraph("Live paper trading capital, position valuations and order telemetry.");
        subtitle.addClassName("st-page-subtitle");

        titleGroup.add(title, subtitle);

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("st-header-actions");
        actions.setSpacing(true);

        RouterLink marketLink = new RouterLink();
        marketLink.setRoute(MarketView.class);
        Button marketBtn = new Button("Market Quotes");
        marketBtn.addClassNames("st-btn", "st-btn-primary");
        marketBtn.getElement().removeAttribute("theme");
        marketLink.add(marketBtn);

        RouterLink portLink = new RouterLink();
        portLink.setRoute(PortfolioView.class);
        Button portBtn = new Button("Holdings & P&L");
        portBtn.addClassNames("st-btn", "st-btn-secondary");
        portBtn.getElement().removeAttribute("theme");
        portLink.add(portBtn);

        actions.add(marketLink, portLink);
        headerContainer.add(titleGroup, actions);

        return headerContainer;
    }

    private Component createMetricsGrid() {
        Div grid = new Div();
        grid.addClassName("st-stats-row");

        // 1. Featured Total Net Worth Card
        Div netWorthCard = createMetricCardElement("Total Net Worth", netWorthValue, netWorthSubtext);
        netWorthCard.addClassName("st-stat-card-featured");
        grid.add(netWorthCard);

        // 2. Available Cash
        grid.add(createMetricCardElement("Available Cash", cashValue, new Span("Settled buying power")));

        // 3. Current Holdings Value
        grid.add(createMetricCardElement("Holdings Value", holdingsValue, holdingsSubtext));

        // 4. Overall P&L
        grid.add(createMetricCardElement("Overall P&L", overallPnlValue, new Span("Realized + Unrealized")));

        // 5. Realized P&L
        grid.add(createMetricCardElement("Realized P&L", realizedPnlValue, new Span("Closed trade gains/losses")));

        // 6. Unrealized P&L
        grid.add(createMetricCardElement("Unrealized P&L", unrealizedPnlValue, new Span("Open position floating P&L")));

        return grid;
    }

    private Div createMetricCardElement(String label, Span valueSpan, Span subtextSpan) {
        Div card = new Div();
        card.addClassName("st-stat-card");

        Div header = new Div();
        header.addClassName("st-stat-header");
        Span title = new Span(label);
        title.addClassName("st-stat-label");
        header.add(title);

        valueSpan.addClassName("st-stat-value");
        valueSpan.addClassName("tabular-num");

        subtextSpan.addClassName("st-stat-subtext");

        card.add(header, valueSpan, subtextSpan);
        return card;
    }

    private Component createAllocationSection() {
        allocationContainer.addClassName("st-allocation-container");
        return allocationContainer;
    }

    private void updateAllocationBar(PortfolioService.PortfolioSummary summary) {
        allocationContainer.removeAll();

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span title = new Span("Capital Allocation");
        title.getStyle().set("font-size", "11px").set("font-weight", "600").set("text-transform", "uppercase").set("letter-spacing", "0.06em").set("color", "var(--st-text-secondary)");

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
        ratioText.getStyle().set("font-size", "11px").set("color", "var(--st-text-muted)").set("font-feature-settings", "\"tnum\"");

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
        cashDot.getStyle().set("background-color", "var(--st-copper)");
        Span cashLabel = new Span(cashDot, new Span("Cash Reserve (" + currencyFormat.format(summary.cash) + ")"));

        Span eqDot = new Span();
        eqDot.addClassName("st-legend-dot");
        eqDot.getStyle().set("background-color", "var(--st-jade)");
        Span eqLabel = new Span(eqDot, new Span("Holdings Equity (" + currencyFormat.format(summary.totalCurrentValue) + ")"));

        legend.add(cashLabel, eqLabel);
        allocationContainer.add(top, bar, legend);
    }

    private Component createRecentTradesSection() {
        Div section = new Div();
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "14px 16px");

        HorizontalLayout secHeader = new HorizontalLayout();
        secHeader.setWidthFull();
        secHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        secHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 header = new H2("Recent Executions");
        header.addClassName("st-section-title");

        RouterLink historyLink = new RouterLink("Audit Log →", HistoryView.class);
        historyLink.getStyle().set("font-size", "11px").set("color", "var(--st-copper)").set("text-decoration", "none").set("font-weight", "500");

        secHeader.add(header, historyLink);
        section.add(secHeader, recentTradesContainer);
        return section;
    }

    private void updateRecentTrades(List<Trade> recentTrades) {
        recentTradesContainer.removeAll();

        if (recentTrades == null || recentTrades.isEmpty()) {
            Div emptyState = new Div();
            emptyState.addClassName("st-empty-state");

            Span icon = new Span();
            icon.getElement().setProperty("innerHTML", "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z\" /></svg>");

            H2 emptyTitle = new H2("No executed trades yet");
            emptyTitle.addClassName("st-empty-title");

            Paragraph emptyMsg = new Paragraph("Select an equity instrument to place your first simulated market order.");
            emptyMsg.addClassName("st-empty-text");

            RouterLink marketLink = new RouterLink("Browse Market Quotes", MarketView.class);
            marketLink.addClassNames("st-btn", "st-btn-primary");
            marketLink.getElement().removeAttribute("theme");

            emptyState.add(icon, emptyTitle, emptyMsg, marketLink);
            recentTradesContainer.add(emptyState);
        } else {
            Div tableContainer = new Div();
            tableContainer.setWidthFull();
            tableContainer.getStyle()
                    .set("margin-top", "8px")
                    .set("overflow-x", "auto")
                    .set("-webkit-overflow-scrolling", "touch")
                    .set("max-width", "100%");

            HorizontalLayout tableHeader = new HorizontalLayout();
            tableHeader.setWidthFull();
            tableHeader.getStyle().set("border-bottom", "1px solid var(--st-border)")
                    .set("padding", "6px 0")
                    .set("font-weight", "600")
                    .set("font-size", "10px")
                    .set("text-transform", "uppercase")
                    .set("letter-spacing", "0.06em")
                    .set("min-width", "460px")
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
                row.getStyle().set("padding", "8px 0")
                        .set("border-bottom", "1px solid var(--st-border-subtle)")
                        .set("min-width", "460px")
                        .set("font-size", "12px");
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
            recentTradesContainer.add(tableContainer);
        }
    }

    private Component createPortfolioSnapshot() {
        Div section = new Div();
        section.addClassName("st-table-surface");
        section.getStyle().set("padding", "14px 16px");

        HorizontalLayout secHeader = new HorizontalLayout();
        secHeader.setWidthFull();
        secHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        secHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 header = new H2("Active Positions");
        header.addClassName("st-section-title");

        RouterLink portLink = new RouterLink("Manage Holdings →", PortfolioView.class);
        portLink.getStyle().set("font-size", "11px").set("color", "var(--st-copper)").set("text-decoration", "none").set("font-weight", "500");

        secHeader.add(header, portLink);
        section.add(secHeader, activePositionsContainer);
        return section;
    }

    private void updateActivePositions(PortfolioService.PortfolioSummary summary) {
        activePositionsContainer.removeAll();

        if (summary.rows.isEmpty()) {
            Div empty = new Div();
            empty.addClassName("st-empty-state");
            empty.getStyle().set("padding", "20px 12px");
            Span msg = new Span("No open equity positions held.");
            msg.getStyle().set("color", "var(--st-text-muted)").set("font-size", "12px");
            empty.add(msg);
            activePositionsContainer.add(empty);
        } else {
            Div tableContainer = new Div();
            tableContainer.setWidthFull();
            tableContainer.getStyle()
                    .set("margin-top", "8px")
                    .set("overflow-x", "auto")
                    .set("-webkit-overflow-scrolling", "touch")
                    .set("max-width", "100%");

            HorizontalLayout tableHeader = new HorizontalLayout();
            tableHeader.setWidthFull();
            tableHeader.getStyle().set("border-bottom", "1px solid var(--st-border)")
                    .set("padding", "6px 0")
                    .set("font-weight", "600")
                    .set("font-size", "10px")
                    .set("text-transform", "uppercase")
                    .set("letter-spacing", "0.06em")
                    .set("min-width", "460px")
                    .set("color", "var(--st-text-secondary)");

            tableHeader.add(
                    createCell("Symbol", 2),
                    createCell("Shares", 1),
                    createCell("Value", 2),
                    createCell("Unrealized Return", 2)
            );
            tableContainer.add(tableHeader);

            int limit = Math.min(summary.rows.size(), 4);
            for (int i = 0; i < limit; i++) {
                PortfolioService.HoldingRow row = summary.rows.get(i);
                HorizontalLayout posRow = new HorizontalLayout();
                posRow.setWidthFull();
                posRow.getStyle().set("padding", "8px 0")
                        .set("border-bottom", "1px solid var(--st-border-subtle)")
                        .set("min-width", "460px")
                        .set("font-size", "12px");
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
                setPnlColor(pnl, row.unrealizedPnl);

                posRow.add(
                        createCell(sym, 2),
                        createCell(qty, 1),
                        createCell(val, 2),
                        createCell(pnl, 2)
                );
                tableContainer.add(posRow);
            }
            activePositionsContainer.add(tableContainer);
        }
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
