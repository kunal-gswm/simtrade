package com.project.trading.ui.views;

import com.project.trading.exception.DataAccessException;
import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.vaadin.flow.router.RouteAlias;

@Route(value = "market", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Market | SimTrade")
public class MarketView extends VerticalLayout {

    private final StockService stockService;
    private final Grid<Stock> grid;
    private final TextField searchField;
    private final Span errorMessage;

    public MarketView() {
        this.stockService = new StockService();

        setSizeFull();

        searchField = new TextField();
        searchField.setPlaceholder("Search symbol or name...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        errorMessage = new Span();
        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.setVisible(false);

        grid = new Grid<>(Stock.class, false);
        grid.setSizeFull();
        grid.addColumn(Stock::getSymbol).setHeader("Symbol").setSortable(true);
        grid.addColumn(Stock::getCompanyName).setHeader("Company").setSortable(true);
        grid.addColumn(Stock::getSector).setHeader("Sector").setSortable(true);
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        grid.addColumn(stock -> currencyFormat.format(stock.getPrice()))
            .setHeader("Price")
            .setSortable(true);

        add(searchField, errorMessage, grid);

        updateList();
    }

    private void updateList() {
        try {
            errorMessage.setVisible(false);
            List<Stock> stocks = stockService.search(searchField.getValue());
            if (stocks.isEmpty()) {
                errorMessage.setText("No stocks found matching the criteria.");
                errorMessage.setVisible(true);
                grid.setItems();
            } else {
                grid.setItems(stocks);
            }
        } catch (DataAccessException e) {
            errorMessage.setText("Error loading market data. Please try again later.");
            errorMessage.setVisible(true);
            grid.setItems();
        }
    }
}
