package com.project.trading.ui.views;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Portfolio;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "ui/portfolio", layout = MainLayout.class)
@PageTitle("Portfolio & Holdings | SimTrade")
public class PortfolioView extends Div {

    private final PortfolioService portfolioService;
    private final TradingService tradingService;
    private final SecurityService securityService;
    private final Grid<PortfolioService.HoldingRow> grid;
    private final NumberFormat currencyFormat;
    private final NumberFormat percentFormat;

    private AuthUser currentUser;
    private MarketDataBroadcaster.Registration broadcastRegistration;

    private final Span netWorthValue = new Span("-");
    private final Span cashValue = new Span("-");
    private final Span investedValue = new Span("-");
    private final Span holdingsValue = new Span("-");
    private final Span unrealizedPnlValue = new Span("-");
    private final Span realizedPnlValue = new Span("-");

    private final Div emptyState;
    private final Div holdingsSection;
    private final Div tableContainer;
    private final Div allocationContainer;

    private static final String SVG_WALLET = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M21 12a2.25 2.25 0 00-2.25-2.25H15a3 3 0 11-6 0H5.25A2.25 2.25 0 003 12m18 0v6a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 18v-6m18 0V9M3 12V9m18 0a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 9m18 0V6a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 6v3\" /></svg>";

    public PortfolioView() {
        this.portfolioService = new PortfolioService();
        this.tradingService = new TradingService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        this.percentFormat = NumberFormat.getPercentInstance(new Locale("en", "IN"));
        this.percentFormat.setMinimumFractionDigits(2);
        this.percentFormat.setMaximumFractionDigits(2);

        addClassName("st-page");

        currentUser = securityService.getAuthenticatedUser();

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");
        H2 title = new H2("Portfolio & Holdings");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Live position mark-to-market valuations, cost basis, unrealized returns and portfolio distribution.");
        subtitle.addClassName("st-page-subtitle");
        titleGroup.add(title, subtitle);

        // Stats row (6 metrics)
        Div statsRow = new Div();
        statsRow.addClassName("st-stats-row");

        Div netWorthCard = createStatCard("Total Net Worth", netWorthValue, "Cash + Equity holdings");
        netWorthCard.addClassName("st-stat-card-featured");
        statsRow.add(netWorthCard);

        statsRow.add(createStatCard("Available Cash", cashValue, "Settled purchasing power"));
        statsRow.add(createStatCard("Total Invested", investedValue, "Aggregate cost basis"));
        statsRow.add(createStatCard("Holdings Value", holdingsValue, "Current mark-to-market"));
        statsRow.add(createStatCard("Unrealized P&L", unrealizedPnlValue, "Floating open returns"));
        statsRow.add(createStatCard("Realized P&L", realizedPnlValue, "Settled closed trades"));

        Div headerContainer = new Div(titleGroup, statsRow);
        headerContainer.addClassName("st-header-container");

        // Allocation Bar
        allocationContainer = new Div();
        allocationContainer.addClassName("st-allocation-container");

        // Holdings section
        holdingsSection = new Div();
        H3 sectionTitle = new H3("Open Equity Positions");
        sectionTitle.addClassName("st-section-title");
        Paragraph sectionSubtitle = new Paragraph("Live position mark-to-market tracking updating with real-time simulated price ticks.");
        sectionSubtitle.addClassName("st-section-subtitle");
        holdingsSection.add(sectionTitle, sectionSubtitle);

        // Responsive Grid with Column Prioritization
        grid = new Grid<>(PortfolioService.HoldingRow.class, false);
        grid.setAllRowsVisible(true);

        grid.addComponentColumn(row -> {
            Span symbol = new Span(row.symbol);
            symbol.addClassName("st-symbol");
            return symbol;
        }).setHeader("Symbol").setSortable(true).setAutoWidth(true);

        grid.addColumn(row -> row.companyName)
                .setHeader("Company Name").setSortable(true).setAutoWidth(true).setFlexGrow(1);

        grid.addComponentColumn(row -> {
            Span shares = new Span(String.valueOf(row.quantity));
            shares.addClassName("tabular-num");
            return shares;
        }).setHeader("Shares").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(row -> {
            Span avg = new Span(currencyFormat.format(row.avgBuyPrice));
            avg.addClassName("tabular-num");
            return avg;
        }).setHeader("Avg Cost").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(row -> {
            Span cur = new Span(currencyFormat.format(row.currentPrice));
            cur.addClassName("tabular-num");
            return cur;
        }).setHeader("Current Price").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(row -> {
            Span val = new Span(currencyFormat.format(row.currentValue));
            val.addClassName("tabular-num");
            val.getStyle().set("font-weight", "600");
            return val;
        }).setHeader("Market Value").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(row -> {
            String pnlText = (row.unrealizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") +
                    currencyFormat.format(row.unrealizedPnl) + " (" +
                    (row.unrealizedPct.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") +
                    percentFormat.format(row.unrealizedPct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)) + ")";
            Span pnlSpan = new Span(pnlText);
            pnlSpan.addClassName("tabular-num");
            if (row.unrealizedPnl.compareTo(BigDecimal.ZERO) > 0) {
                pnlSpan.addClassName("st-positive");
            } else if (row.unrealizedPnl.compareTo(BigDecimal.ZERO) < 0) {
                pnlSpan.addClassName("st-negative");
            } else {
                pnlSpan.getStyle().set("color", "var(--st-text-muted)");
            }
            pnlSpan.getStyle().set("font-weight", "600").set("font-size", "12px");
            return pnlSpan;
        }).setHeader("Unrealized Return").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(row -> {
            Button sellBtn = new Button("Sell");
            sellBtn.addClassNames("st-btn", "st-btn-sell");
            sellBtn.getElement().removeAttribute("theme");
            sellBtn.getStyle().set("height", "26px").set("font-size", "11px").set("padding", "0 10px");
            sellBtn.addClickListener(e -> openSellDialog(row));
            return sellBtn;
        }).setHeader("Action").setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        tableContainer = new Div(grid);
        tableContainer.addClassName("st-table-surface");

        // Empty state
        emptyState = new Div();
        emptyState.addClassName("st-empty-state");
        emptyState.setVisible(false);

        Component walletIcon = createSvgIcon(SVG_WALLET, "36px");
        Div emptyTitle2 = new Div();
        emptyTitle2.addClassName("st-empty-title");
        emptyTitle2.setText("Your portfolio has no open positions");
        Paragraph emptyText = new Paragraph("Select an equity instrument from the Market Quotes page to place your first simulated trade.");
        emptyText.addClassName("st-empty-text");

        RouterLink marketLink = new RouterLink();
        marketLink.setRoute(MarketView.class);
        Button exploreBtn = new Button("Browse Market Quotes");
        exploreBtn.addClassNames("st-btn", "st-btn-primary");
        exploreBtn.getElement().removeAttribute("theme");
        marketLink.add(exploreBtn);

        emptyState.add(walletIcon, emptyTitle2, emptyText, marketLink);

        holdingsSection.add(tableContainer, emptyState);

        add(headerContainer, allocationContainer, holdingsSection);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();

        // Immediate load
        updateData();

        // Broadcast subscription for live real-time push price changes & trades with UI lifecycle binding
        broadcastRegistration = MarketDataBroadcaster.register(ui, new MarketDataBroadcaster.BroadcastListener() {
            @Override
            public void onPriceUpdate(MarketDataBroadcaster.PriceUpdate event) {
                updateData();
            }

            @Override
            public void onTradeUpdate(MarketDataBroadcaster.TradeUpdate event) {
                if (event.trade() != null && currentUser != null && event.trade().getUserId() == currentUser.getId()) {
                    updateData();
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

    private Component createSvgIcon(String svgData, String size) {
        Span span = new Span();
        span.getElement().setProperty("innerHTML", svgData);
        span.getStyle().set("display", "inline-flex").set("align-items", "center").set("justify-content", "center");
        span.getStyle().set("width", size).set("height", size);
        return span;
    }

    private Div createStatCard(String label, Span value, String subtext) {
        Div card = new Div();
        card.addClassName("st-stat-card");

        Div header = new Div();
        header.addClassName("st-stat-header");
        Span labelSpan = new Span(label);
        labelSpan.addClassName("st-stat-label");
        header.add(labelSpan);

        value.addClassName("st-stat-value");
        value.addClassName("tabular-num");

        Span sub = new Span(subtext);
        sub.addClassName("st-stat-subtext");

        card.add(header, value, sub);
        return card;
    }

    private synchronized void updateData() {
        try {
            if (currentUser == null) return;

            PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(currentUser.getId());

            netWorthValue.setText(currencyFormat.format(summary.netWorth));
            cashValue.setText(currencyFormat.format(summary.cash));
            investedValue.setText(currencyFormat.format(summary.totalInvested));
            holdingsValue.setText(currencyFormat.format(summary.totalCurrentValue));

            String unSign = summary.totalUnrealizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            unrealizedPnlValue.setText(unSign + currencyFormat.format(summary.totalUnrealizedPnl));
            setPnlColor(unrealizedPnlValue, summary.totalUnrealizedPnl);

            String reSign = summary.realizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            realizedPnlValue.setText(reSign + currencyFormat.format(summary.realizedPnl));
            setPnlColor(realizedPnlValue, summary.realizedPnl);

            updateAllocationBar(summary);

            if (summary.rows.isEmpty()) {
                tableContainer.setVisible(false);
                emptyState.setVisible(true);
                grid.setItems();
            } else {
                emptyState.setVisible(false);
                tableContainer.setVisible(true);
                grid.setItems(summary.rows);
            }
        } catch (DataAccessException e) {
            tableContainer.setVisible(false);
            emptyState.setVisible(true);
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
        Span eqLabel = new Span(eqDot, new Span("Equity Allocation (" + currencyFormat.format(summary.totalCurrentValue) + ")"));

        legend.add(cashLabel, eqLabel);
        allocationContainer.add(top, bar, legend);
    }

    private void openSellDialog(PortfolioService.HoldingRow row) {
        if (currentUser == null) {
            Notification.show("Please log in to trade.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Sell Order · " + row.symbol);
        dialog.setWidth("min(400px, 94vw)");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setWidthFull();

        Span companySpan = new Span(row.companyName);
        companySpan.getStyle().set("font-size", "12px").set("color", "var(--st-text-secondary)").set("margin-bottom", "12px").set("display", "block");

        // Available Shares
        Div sharesRow = new Div();
        sharesRow.addClassName("st-dialog-row");
        Span sharesLabel = new Span("Held Shares");
        sharesLabel.addClassName("st-dialog-row-label");
        Span sharesVal = new Span(String.valueOf(row.quantity));
        sharesVal.addClassName("st-dialog-row-value");
        sharesRow.add(sharesLabel, sharesVal);

        // Price Row
        Div priceRow = new Div();
        priceRow.addClassName("st-dialog-row");
        Span priceLabel = new Span("Current Execution Price");
        priceLabel.addClassName("st-dialog-row-label");
        Span priceVal = new Span(currencyFormat.format(row.currentPrice));
        priceVal.addClassName("st-dialog-row-value");
        priceRow.add(priceLabel, priceVal);

        // Quantity Input
        IntegerField qtyField = new IntegerField("Quantity to Sell (Shares)");
        qtyField.setMin(1);
        qtyField.setMax(row.quantity);
        qtyField.setStepButtonsVisible(true);
        qtyField.setValue(1);
        qtyField.setRequiredIndicatorVisible(true);
        qtyField.setWidthFull();
        qtyField.getStyle().set("margin-top", "10px");

        // Estimated proceeds
        Div estDiv = new Div();
        estDiv.addClassName("st-dialog-estimate");
        Div estRow = new Div();
        estRow.addClassName("st-dialog-row");
        Span estLabel = new Span("Estimated Gross Proceeds");
        estLabel.addClassName("st-dialog-row-label");
        Span estValue = new Span(currencyFormat.format(row.currentPrice));
        estValue.addClassName("st-dialog-row-value");
        estRow.add(estLabel, estValue);
        estDiv.add(estRow);

        qtyField.addValueChangeListener(e -> {
            if (e.getValue() != null && e.getValue() > 0) {
                estValue.setText(currencyFormat.format(row.currentPrice.multiply(BigDecimal.valueOf(e.getValue()))));
            }
        });

        Span note = new Span("Proceeds will be credited to settled available cash upon execution.");
        note.addClassName("st-dialog-note");

        layout.add(companySpan, sharesRow, priceRow, qtyField, estDiv, note);
        dialog.add(layout);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassNames("st-btn", "st-btn-secondary");
        cancelBtn.getElement().removeAttribute("theme");

        Button confirmBtn = new Button("Execute Sell");
        confirmBtn.addClassNames("st-btn", "st-btn-sell");
        confirmBtn.getElement().removeAttribute("theme");

        confirmBtn.addClickListener(e -> {
            Integer qty = qtyField.getValue();
            if (qty == null || qty < 1 || qty > row.quantity) {
                Notification.show("Please enter a valid share quantity (1-" + row.quantity + ").", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            confirmBtn.setEnabled(false);
            confirmBtn.setText("Executing...");

            try {
                Trade trade = tradingService.sell(currentUser.getId(), row.stockId, qty);
                Notification.show("Executed SELL: " + trade.getQuantity() + " shares of " + trade.getStockSymbol() +
                                " at " + currencyFormat.format(trade.getPrice()) + " (Total: " + currencyFormat.format(trade.getTotalAmount()) + ")",
                        5000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                updateData();
            } catch (AppException ex) {
                Notification.show("Execution rejected: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception ex) {
                Notification.show("Order processing error.", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } finally {
                confirmBtn.setEnabled(true);
                confirmBtn.setText("Execute Sell");
            }
        });

        dialog.getFooter().add(cancelBtn, confirmBtn);
        dialog.open();
    }
}
