package com.project.trading.ui.views;

import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.service.StockService;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "market", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Market | SimTrade")
public class MarketView extends VerticalLayout {

    private final StockService stockService;
    private final TradingService tradingService;
    private final SecurityService securityService;
    private final Grid<Stock> grid;
    private final TextField searchField;
    private final Span errorMessage;
    private final NumberFormat currencyFormat;

    public MarketView() {
        this.stockService = new StockService();
        this.tradingService = new TradingService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

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
        
        grid.addColumn(stock -> currencyFormat.format(stock.getPrice()))
            .setHeader("Price")
            .setSortable(true);

        grid.addComponentColumn(stock -> {
            Button buyBtn = new Button("Buy");
            buyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            buyBtn.addClickListener(e -> openBuyDialog(stock));
            return buyBtn;
        }).setHeader("Actions");

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

    private void openBuyDialog(Stock stock) {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) {
            Notification.show("Please log in to trade.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Buy " + stock.getSymbol());

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        Span priceInfo = new Span("Current server price: " + currencyFormat.format(stock.getPrice()));
        priceInfo.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-secondary-text-color)");

        Span disclaimer = new Span("Note: Final execution price is determined by the server.");
        disclaimer.getStyle().set("font-size", "var(--lumo-font-size-xs)").set("color", "var(--lumo-secondary-text-color)");

        IntegerField qtyField = new IntegerField("Quantity");
        qtyField.setMin(1);
        qtyField.setMax(10000);
        qtyField.setStepButtonsVisible(true);
        qtyField.setValue(1);
        qtyField.setRequiredIndicatorVisible(true);

        dialogLayout.add(priceInfo, qtyField, disclaimer);
        dialog.add(dialogLayout);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        Button confirmBtn = new Button("Confirm Buy");
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        confirmBtn.addClickListener(e -> {
            Integer qty = qtyField.getValue();
            if (qty == null || qty < 1) {
                Notification.show("Please enter a valid quantity.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            confirmBtn.setEnabled(false); // Prevent double submission

            try {
                Trade trade = tradingService.buy(user.getId(), stock.getId(), qty);
                Notification.show("Successfully bought " + trade.getQuantity() + " shares of " + trade.getStockSymbol() + " for " + currencyFormat.format(trade.getTotalAmount()), 5000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                updateList(); // Refresh prices if needed
            } catch (AppException ex) {
                Notification.show("Trade failed: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception ex) {
                Notification.show("An unexpected error occurred during execution.", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } finally {
                confirmBtn.setEnabled(true);
            }
        });

        dialog.getFooter().add(cancelBtn, confirmBtn);
        dialog.open();
    }
}
