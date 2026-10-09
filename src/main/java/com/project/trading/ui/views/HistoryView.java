package com.project.trading.ui.views;

import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "history", layout = MainLayout.class)
@PageTitle("Trade History | SimTrade")
public class HistoryView extends VerticalLayout {

    private final TradingService tradingService;
    private final SecurityService securityService;
    private final Grid<Trade> grid;
    private final Span errorMessage;
    private final NumberFormat currencyFormat;

    public HistoryView() {
        this.tradingService = new TradingService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        setSizeFull();

        errorMessage = new Span();
        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.setVisible(false);

        grid = new Grid<>(Trade.class, false);
        grid.setSizeFull();
        
        grid.addComponentColumn(trade -> {
            Span typeSpan = new Span(trade.getType().name());
            if (trade.getType().name().equalsIgnoreCase("BUY")) {
                typeSpan.getStyle().set("color", "blue");
            } else {
                typeSpan.getStyle().set("color", "red");
            }
            return typeSpan;
        }).setHeader("Side").setSortable(true);

        grid.addColumn(Trade::getStockSymbol).setHeader("Symbol").setSortable(true);
        grid.addColumn(Trade::getQuantity).setHeader("Quantity").setSortable(true);
        grid.addColumn(trade -> currencyFormat.format(trade.getPrice())).setHeader("Execution Price").setSortable(true);
        grid.addColumn(trade -> currencyFormat.format(trade.getTotalAmount())).setHeader("Total Value").setSortable(true);
        grid.addColumn(Trade::getExecutedAt).setHeader("Timestamp").setSortable(true);

        add(errorMessage, grid);
        updateData();
    }

    private void updateData() {
        try {
            AuthUser user = securityService.getAuthenticatedUser();
            if (user == null) return; // Guarded by AuthInitListener

            List<Trade> history = tradingService.getHistory(user.getId(), 100);

            if (history.isEmpty()) {
                errorMessage.setText("No trades yet.");
                errorMessage.setVisible(true);
                grid.setItems();
            } else {
                errorMessage.setVisible(false);
                grid.setItems(history);
            }
        } catch (DataAccessException e) {
            errorMessage.setText("Error loading trade history. Please try again later.");
            errorMessage.setVisible(true);
            grid.setItems();
        }
    }
}
