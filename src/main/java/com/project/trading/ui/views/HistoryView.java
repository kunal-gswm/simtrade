package com.project.trading.ui.views;

import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Route(value = "history", layout = MainLayout.class)
@PageTitle("Trade History | SimTrade")
public class HistoryView extends Div {

    private final TradingService tradingService;
    private final SecurityService securityService;
    private final Grid<Trade> grid;
    private final NumberFormat currencyFormat;
    private final DateTimeFormatter dateFormatter;

    private final Span totalTradesValue;
    private final Span buyOrdersValue;
    private final Span sellOrdersValue;
    private final Div emptyState;
    private final Div tableContainer;

    private static final String SVG_HISTORY = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z\" /></svg>";

    public HistoryView() {
        this.tradingService = new TradingService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        this.dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        addClassName("st-page");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");
        H2 title = new H2("Trade History");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Review your completed buy and sell orders.");
        subtitle.addClassName("st-page-subtitle");
        titleGroup.add(title, subtitle);

        // === Stats row ===
        Div statsRow = new Div();
        statsRow.addClassName("st-stats-row");

        totalTradesValue = new Span("-");
        totalTradesValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Total Trades", totalTradesValue));

        buyOrdersValue = new Span("-");
        buyOrdersValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Buy Orders", buyOrdersValue));

        sellOrdersValue = new Span("-");
        sellOrdersValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Sell Orders", sellOrdersValue));

        Div headerContainer = new Div(titleGroup, statsRow);
        headerContainer.addClassName("st-header-container");

        // === Grid ===
        grid = new Grid<>(Trade.class, false);
        grid.setAllRowsVisible(true);

        grid.addColumn(trade -> trade.getExecutedAt().toLocalDateTime().format(dateFormatter))
                .setHeader("Date and Time").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(trade -> {
            Span symbol = new Span(trade.getStockSymbol());
            symbol.addClassName("st-symbol");
            return symbol;
        }).setHeader("Symbol").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(trade -> {
            Span typeSpan = new Span(trade.getType().name());
            typeSpan.addClassName("st-badge");
            if (trade.getType().name().equalsIgnoreCase("BUY")) {
                typeSpan.addClassName("st-badge-buy");
            } else {
                typeSpan.addClassName("st-badge-sell");
            }
            return typeSpan;
        }).setHeader("Trade Type").setSortable(true).setAutoWidth(true);

        grid.addColumn(Trade::getQuantity)
                .setHeader("Quantity").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true).setFlexGrow(1);

        grid.addColumn(trade -> currencyFormat.format(trade.getPrice()))
                .setHeader("Execution Price").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addColumn(trade -> currencyFormat.format(trade.getTotalAmount()))
                .setHeader("Total Value").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        tableContainer = new Div(grid);
        tableContainer.addClassName("st-table-surface");

        // === Empty state ===
        emptyState = new Div();
        emptyState.addClassName("st-empty-state");
        emptyState.setVisible(false);
        emptyState.getStyle().set("background", "var(--st-bg-surface)").set("border", "1px solid var(--st-border)").set("border-radius", "var(--st-radius)");

        Component recordsIcon = createSvgIcon(SVG_HISTORY, "48px");
        Div emptyTitle = new Div();
        emptyTitle.addClassName("st-empty-title");
        emptyTitle.setText("No trades yet");
        Paragraph emptyText = new Paragraph("Your completed trades will appear here after you place an order.");
        emptyText.addClassName("st-empty-text");

        RouterLink marketLink = new RouterLink();
        marketLink.setRoute(MarketView.class);
        Button exploreBtn = new Button("Explore Market");
        exploreBtn.addClassNames("st-btn", "st-btn-primary");
        exploreBtn.getElement().removeAttribute("theme");
        marketLink.add(exploreBtn);

        emptyState.add(recordsIcon, emptyTitle, emptyText, marketLink);

        add(headerContainer, tableContainer, emptyState);
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

            List<Trade> history = tradingService.getHistory(user.getId(), 100);

            if (history.isEmpty()) {
                totalTradesValue.setText("0");
                buyOrdersValue.setText("0");
                sellOrdersValue.setText("0");

                tableContainer.setVisible(false);
                emptyState.setVisible(true);
                grid.setItems();
            } else {
                tableContainer.setVisible(true);
                emptyState.setVisible(false);
                grid.setItems(history);

                totalTradesValue.setText(String.valueOf(history.size()));
                long buys = history.stream().filter(t -> t.getType().name().equalsIgnoreCase("BUY")).count();
                long sells = history.size() - buys;
                buyOrdersValue.setText(String.valueOf(buys));
                sellOrdersValue.setText(String.valueOf(sells));
            }
        } catch (DataAccessException e) {
            tableContainer.setVisible(false);
            emptyState.setVisible(true);
        }
    }
}
