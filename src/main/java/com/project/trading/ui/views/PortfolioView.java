package com.project.trading.ui.views;

import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Portfolio;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "portfolio", layout = MainLayout.class)
@PageTitle("Portfolio | SimTrade")
public class PortfolioView extends Div {

    private final PortfolioService portfolioService;
    private final TradingService tradingService;
    private final SecurityService securityService;
    private final Grid<PortfolioService.HoldingRow> grid;
    private final NumberFormat currencyFormat;
    private final NumberFormat percentFormat;

    private final Span cashValue;
    private final Span investedValue;
    private final Span holdingsValue;
    private final Span netWorthValue;
    private final Div emptyState;
    private final Div holdingsSection;
    private final Div tableContainer;

    private static final String SVG_WALLET = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M21 12a2.25 2.25 0 00-2.25-2.25H15a3 3 0 11-6 0H5.25A2.25 2.25 0 003 12m18 0v6a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 18v-6m18 0V9M3 12V9m18 0a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 9m18 0V6a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 6v3\" /></svg>";

    public PortfolioView() {
        this.portfolioService = new PortfolioService();
        this.tradingService = new TradingService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        this.percentFormat = NumberFormat.getPercentInstance(new Locale("en", "IN"));
        this.percentFormat.setMinimumFractionDigits(2);

        addClassName("st-page");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");
        H2 title = new H2("Portfolio");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Track your holdings, investment value, and simulated trades.");
        subtitle.addClassName("st-page-subtitle");
        titleGroup.add(title, subtitle);

        // === Stats row ===
        Div statsRow = new Div();
        statsRow.addClassName("st-stats-row");

        cashValue = new Span("-");
        cashValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Available Cash", cashValue));

        investedValue = new Span("-");
        investedValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Total Invested", investedValue));

        holdingsValue = new Span("-");
        holdingsValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Holdings Value", holdingsValue));

        netWorthValue = new Span("-");
        netWorthValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Total Net Worth", netWorthValue));

        Div headerContainer = new Div(titleGroup, statsRow);
        headerContainer.addClassName("st-header-container");

        // === Holdings section ===
        holdingsSection = new Div();

        H3 sectionTitle = new H3("Your Holdings");
        sectionTitle.addClassName("st-section-title");
        Paragraph sectionSubtitle = new Paragraph("Stocks currently held in your portfolio.");
        sectionSubtitle.addClassName("st-section-subtitle");
        holdingsSection.add(sectionTitle, sectionSubtitle);

        // === Grid ===
        grid = new Grid<>(PortfolioService.HoldingRow.class, false);
        grid.setAllRowsVisible(true);

        grid.addComponentColumn(row -> {
            Span symbol = new Span(row.symbol);
            symbol.addClassName("st-symbol");
            return symbol;
        }).setHeader("Symbol").setSortable(true).setAutoWidth(true);

        grid.addColumn(row -> row.companyName)
                .setHeader("Company").setSortable(true).setAutoWidth(true).setFlexGrow(1);

        grid.addColumn(row -> row.quantity)
                .setHeader("Shares").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addColumn(row -> currencyFormat.format(row.avgBuyPrice))
                .setHeader("Avg Cost").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addColumn(row -> currencyFormat.format(row.currentPrice))
                .setHeader("Current Price").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addColumn(row -> currencyFormat.format(row.currentValue))
                .setHeader("Market Value").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(row -> {
            String pnlText = currencyFormat.format(row.unrealizedPnl) + " (" + percentFormat.format(row.unrealizedPct / 100.0) + ")";
            Span pnlSpan = new Span(pnlText);
            if (row.unrealizedPnl > 0) {
                pnlSpan.addClassName("st-positive");
            } else if (row.unrealizedPnl < 0) {
                pnlSpan.addClassName("st-negative");
            }
            pnlSpan.getStyle().set("font-weight", "600").set("font-size", "13px");
            return pnlSpan;
        }).setHeader("Total Return").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(row -> {
            Button sellBtn = new Button("Sell");
            sellBtn.addClassNames("st-btn", "st-btn-sell");
            sellBtn.getElement().removeAttribute("theme");
            sellBtn.getStyle().set("height", "32px").set("font-size", "13px").set("padding", "0 14px");
            sellBtn.addClickListener(e -> openSellDialog(row));
            return sellBtn;
        }).setHeader("Action").setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        tableContainer = new Div(grid);
        tableContainer.addClassName("st-table-surface");

        // === Empty state ===
        emptyState = new Div();
        emptyState.addClassName("st-empty-state");
        emptyState.setVisible(false);
        emptyState.getStyle().set("background", "var(--st-bg-surface)").set("border", "1px solid var(--st-border)").set("border-radius", "var(--st-radius)");

        Component walletIcon = createSvgIcon(SVG_WALLET, "48px");
        Div emptyTitle2 = new Div();
        emptyTitle2.addClassName("st-empty-title");
        emptyTitle2.setText("Your portfolio is empty");
        Paragraph emptyText = new Paragraph("Buy a stock from the Market page to start building your portfolio.");
        emptyText.addClassName("st-empty-text");

        RouterLink marketLink = new RouterLink();
        marketLink.setRoute(MarketView.class);
        Button exploreBtn = new Button("Explore Market");
        exploreBtn.addClassNames("st-btn", "st-btn-primary");
        exploreBtn.getElement().removeAttribute("theme");
        marketLink.add(exploreBtn);

        emptyState.add(walletIcon, emptyTitle2, emptyText, marketLink);

        holdingsSection.add(tableContainer, emptyState);

        add(headerContainer, holdingsSection);
        updateData();
    }

    private Component createSvgIcon(String svgData, String size) {
        Span span = new Span();
        span.getElement().setProperty("innerHTML", svgData);
        span.getStyle().set("display", "inline-flex").set("align-items", "center").set("justify-content", "center");
        span.getStyle().set("width", size).set("height", size);
        return span;
    }

    private Div createStatCard(String label, Span value) {
        Div card = new Div();
        card.addClassName("st-stat-card");
        Div labelDiv = new Div();
        labelDiv.addClassName("st-stat-label");
        labelDiv.setText(label);
        card.add(labelDiv, value);
        return card;
    }

    private void updateData() {
        try {
            AuthUser user = securityService.getAuthenticatedUser();
            if (user == null) return;

            Portfolio portfolio = portfolioService.getPortfolioForUser(user.getId());
            PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(portfolio);

            cashValue.setText(currencyFormat.format(summary.cash));
            investedValue.setText(currencyFormat.format(summary.totalInvested));
            holdingsValue.setText(currencyFormat.format(summary.totalCurrentValue));
            netWorthValue.setText(currencyFormat.format(summary.netWorth));

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

    private void openSellDialog(PortfolioService.HoldingRow row) {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) {
            Notification.show("Please log in to trade.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Sell Stock");
        dialog.setWidth("400px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setWidthFull();

        Span symbolSpan = new Span(row.symbol);
        symbolSpan.getStyle().set("font-size", "18px").set("font-weight", "600").set("color", "var(--st-text-primary)");

        Span companySpan = new Span(row.companyName);
        companySpan.getStyle().set("font-size", "14px").set("color", "var(--st-text-secondary)").set("margin-bottom", "12px").set("display", "block");

        // Info rows
        Div sharesRow = new Div();
        sharesRow.addClassName("st-dialog-row");
        Span sharesLabel = new Span("Available Shares");
        sharesLabel.addClassName("st-dialog-row-label");
        Span sharesVal = new Span(String.valueOf(row.quantity));
        sharesVal.addClassName("st-dialog-row-value");
        sharesRow.add(sharesLabel, sharesVal);

        Div priceRow = new Div();
        priceRow.addClassName("st-dialog-row");
        Span priceLabel = new Span("Current Price");
        priceLabel.addClassName("st-dialog-row-label");
        Span priceVal = new Span(currencyFormat.format(row.currentPrice));
        priceVal.addClassName("st-dialog-row-value");
        priceRow.add(priceLabel, priceVal);

        IntegerField qtyField = new IntegerField("Quantity to Sell");
        qtyField.setMin(1);
        qtyField.setMax(row.quantity);
        qtyField.setStepButtonsVisible(true);
        qtyField.setValue(1);
        qtyField.setRequiredIndicatorVisible(true);
        qtyField.setWidthFull();
        qtyField.getStyle().set("margin-top", "12px");

        Div estDiv = new Div();
        estDiv.addClassName("st-dialog-estimate");
        Div estRow = new Div();
        estRow.addClassName("st-dialog-row");
        Span estLabel = new Span("Estimated Proceeds");
        estLabel.addClassName("st-dialog-row-label");
        Span estValue = new Span(currencyFormat.format(row.currentPrice));
        estValue.addClassName("st-dialog-row-value");
        estRow.add(estLabel, estValue);
        estDiv.add(estRow);

        qtyField.addValueChangeListener(e -> {
            if (e.getValue() != null && e.getValue() > 0) {
                estValue.setText(currencyFormat.format(row.currentPrice * e.getValue()));
            }
        });

        Span note = new Span("Final execution price is determined by the server.");
        note.addClassName("st-dialog-note");

        layout.add(symbolSpan, companySpan, sharesRow, priceRow, qtyField, estDiv, note);
        dialog.add(layout);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassNames("st-btn", "st-btn-secondary");
        cancelBtn.getElement().removeAttribute("theme");

        Button confirmBtn = new Button("Confirm Sell");
        confirmBtn.addClassNames("st-btn", "st-btn-sell");
        confirmBtn.getElement().removeAttribute("theme");

        confirmBtn.addClickListener(e -> {
            Integer qty = qtyField.getValue();
            if (qty == null || qty < 1 || qty > row.quantity) {
                Notification.show("Enter a valid quantity (1-" + row.quantity + ").", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            confirmBtn.setEnabled(false);
            confirmBtn.setText("Processing...");

            try {
                Trade trade = tradingService.sell(user.getId(), row.stockId, qty);
                Notification.show("Sold " + trade.getQuantity() + " shares of " + trade.getStockSymbol() + " for " + currencyFormat.format(trade.getTotalAmount()), 5000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                updateData();
            } catch (AppException ex) {
                Notification.show("Trade failed: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception ex) {
                Notification.show("An unexpected error occurred.", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } finally {
                confirmBtn.setEnabled(true);
                confirmBtn.setText("Confirm Sell");
            }
        });

        dialog.getFooter().add(cancelBtn, confirmBtn);
        dialog.open();
    }
}
