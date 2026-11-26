package com.project.trading.ui.views;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.shared.Registration;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Route(value = "ui/history", layout = MainLayout.class)
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
    private final Span totalVolumeValue;
    private final Div emptyState;
    private final Div tableContainer;

    private Registration tradeListenerRegistration;

    private static final String SVG_HISTORY = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z\" /></svg>";

    public HistoryView() {
        this.tradingService = new TradingService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        this.dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");

        addClassName("st-page");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");
        H2 title = new H2("Trade Log & Audit Trail");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Immutable historical audit trail of all executed market simulation orders.");
        subtitle.addClassName("st-page-subtitle");
        titleGroup.add(title, subtitle);

        // Stats row
        Div statsRow = new Div();
        statsRow.addClassName("st-stats-row");

        totalTradesValue = new Span("-");
        totalTradesValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Total Executions", totalTradesValue, "Lifetime transactions"));

        buyOrdersValue = new Span("-");
        buyOrdersValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Buy Orders", buyOrdersValue, "Long positions acquired"));

        sellOrdersValue = new Span("-");
        sellOrdersValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Sell Orders", sellOrdersValue, "Liquidated positions"));

        totalVolumeValue = new Span("-");
        totalVolumeValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Total Traded Value", totalVolumeValue, "Aggregate gross turnover"));

        Div headerContainer = new Div(titleGroup, statsRow);
        headerContainer.addClassName("st-header-container");

        // Grid
        grid = new Grid<>(Trade.class, false);
        grid.setAllRowsVisible(true);

        grid.addComponentColumn(trade -> {
            Span timeSpan = new Span(trade.getExecutedAt().toLocalDateTime().format(dateFormatter));
            timeSpan.addClassName("tabular-num");
            return timeSpan;
        }).setHeader("Execution Timestamp").setSortable(true).setAutoWidth(true);

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
        }).setHeader("Order Side").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(trade -> {
            Span qty = new Span(String.valueOf(trade.getQuantity()));
            qty.addClassName("tabular-num");
            return qty;
        }).setHeader("Quantity").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true).setFlexGrow(1);

        grid.addComponentColumn(trade -> {
            Span price = new Span(currencyFormat.format(trade.getPrice()));
            price.addClassName("tabular-num");
            return price;
        }).setHeader("Execution Price").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(trade -> {
            Span total = new Span(currencyFormat.format(trade.getTotalAmount()));
            total.addClassName("tabular-num");
            total.getStyle().set("font-weight", "600");
            return total;
        }).setHeader("Total Settled").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        tableContainer = new Div(grid);
        tableContainer.addClassName("st-table-surface");

        // Empty state
        emptyState = new Div();
        emptyState.addClassName("st-empty-state");
        emptyState.setVisible(false);

        Component historyIcon = createSvgIcon(SVG_HISTORY, "40px");
        Div emptyTitle = new Div();
        emptyTitle.addClassName("st-empty-title");
        emptyTitle.setText("No trade history found");
        Paragraph emptyText = new Paragraph("Your completed orders will appear here chronologically once executed.");
        emptyText.addClassName("st-empty-text");

        RouterLink marketLink = new RouterLink();
        marketLink.setRoute(MarketView.class);
        Button exploreBtn = new Button("Open Market Quotes");
        exploreBtn.addClassNames("st-btn", "st-btn-primary");
        exploreBtn.getElement().removeAttribute("theme");
        marketLink.add(exploreBtn);

        emptyState.add(historyIcon, emptyTitle, emptyText, marketLink);

        add(headerContainer, tableContainer, emptyState);

        loadData();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        loadData();

        tradeListenerRegistration = MarketDataBroadcaster.registerTradeListener(ui, trade -> {
            loadData();
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (tradeListenerRegistration != null) {
            tradeListenerRegistration.remove();
            tradeListenerRegistration = null;
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

        Span sub = new Span(subtext);
        sub.addClassName("st-stat-subtext");

        card.add(header, value, sub);
        return card;
    }

    private void loadData() {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) return;

        try {
            List<Trade> trades = tradingService.getHistory(user.getId(), 500);

            if (trades == null || trades.isEmpty()) {
                tableContainer.setVisible(false);
                emptyState.setVisible(true);
                totalTradesValue.setText("0");
                buyOrdersValue.setText("0");
                sellOrdersValue.setText("0");
                totalVolumeValue.setText(currencyFormat.format(BigDecimal.ZERO));
            } else {
                emptyState.setVisible(false);
                tableContainer.setVisible(true);
                grid.setItems(trades);

                totalTradesValue.setText(String.valueOf(trades.size()));
                long buys = trades.stream().filter(t -> "BUY".equalsIgnoreCase(t.getType().name())).count();
                long sells = trades.stream().filter(t -> "SELL".equalsIgnoreCase(t.getType().name())).count();
                buyOrdersValue.setText(String.valueOf(buys));
                sellOrdersValue.setText(String.valueOf(sells));

                BigDecimal totalVolume = trades.stream()
                        .map(Trade::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalVolumeValue.setText(currencyFormat.format(totalVolume));
            }
        } catch (DataAccessException e) {
            tableContainer.setVisible(false);
            emptyState.setVisible(true);
        }
    }
}
