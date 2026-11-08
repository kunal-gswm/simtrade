package com.project.trading.ui.views;

import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Portfolio;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.StockService;
import com.project.trading.service.TradingService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Route(value = "ui/market", layout = MainLayout.class)
@RouteAlias(value = "ui", layout = MainLayout.class)
@PageTitle("Market Quotes | SimTrade")
public class MarketView extends Div {

    private final StockService stockService;
    private final TradingService tradingService;
    private final SecurityService securityService;
    private final PortfolioService portfolioService;
    private final Grid<Stock> grid;
    private final TextField searchField;
    private final ComboBox<String> sectorFilter;
    private final NumberFormat currencyFormat;
    private final NumberFormat percentFormat;

    private final Span cashValue;
    private final Span stockCountValue;
    private final Div emptyState;
    private final Div tableContainer;

    private static final String SVG_SEARCH = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z\" /></svg>";

    public MarketView() {
        this.stockService = new StockService();
        this.tradingService = new TradingService();
        this.securityService = new SecurityService();
        this.portfolioService = new PortfolioService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        this.percentFormat = NumberFormat.getPercentInstance(new Locale("en", "IN"));
        this.percentFormat.setMinimumFractionDigits(2);
        this.percentFormat.setMaximumFractionDigits(2);

        addClassName("st-page");

        // Header Section
        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");

        H2 title = new H2("Market Quotes");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Live simulated equity instruments, real-time spread data and order placement.");
        subtitle.addClassName("st-page-subtitle");
        titleGroup.add(title, subtitle);

        // Stats row
        Div statsRow = new Div();
        statsRow.addClassName("st-stats-row");

        cashValue = new Span("-");
        cashValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Available Cash", cashValue, "Liquid trading balance"));

        stockCountValue = new Span("-");
        stockCountValue.addClassName("st-stat-value");
        statsRow.add(createStatCard("Listed Symbols", stockCountValue, "Active tradable equities"));

        Div headerContainer = new Div(titleGroup, statsRow);
        headerContainer.addClassName("st-header-container");

        // Search & Filter Toolbar
        searchField = new TextField();
        searchField.setPlaceholder("Search symbol or company name...");
        searchField.setPrefixComponent(createSvgIcon(SVG_SEARCH, "16px"));
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
        searchField.addClassName("st-search-field");

        sectorFilter = new ComboBox<>();
        sectorFilter.setPlaceholder("All Sectors");
        sectorFilter.setClearButtonVisible(true);
        sectorFilter.addClassName("st-search-field");

        List<String> sectors = stockService.search("").stream()
                .map(Stock::getSector)
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        sectorFilter.setItems(sectors);
        sectorFilter.addValueChangeListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, sectorFilter);
        toolbar.getStyle().set("margin-bottom", "16px").set("flex-wrap", "wrap");

        // Empty state
        emptyState = new Div();
        emptyState.addClassName("st-empty-state");
        emptyState.setVisible(false);

        Component emptyIcon = createSvgIcon(SVG_SEARCH, "40px");
        Div emptyTitle = new Div();
        emptyTitle.addClassName("st-empty-title");
        emptyTitle.setText("No equities match your criteria");
        Paragraph emptyText = new Paragraph("Try clearing the search query or resetting the sector filter.");
        emptyText.addClassName("st-empty-text");
        Button clearSearch = new Button("Reset filters", e -> {
            searchField.clear();
            sectorFilter.clear();
            updateList();
        });
        clearSearch.addClassNames("st-btn", "st-btn-secondary");
        emptyState.add(emptyIcon, emptyTitle, emptyText, clearSearch);

        // High-density Grid
        grid = new Grid<>(Stock.class, false);
        grid.setAllRowsVisible(true);

        grid.addComponentColumn(stock -> {
            Span symbol = new Span(stock.getSymbol());
            symbol.addClassName("st-symbol");
            return symbol;
        }).setHeader("Symbol").setSortable(true).setAutoWidth(true);

        grid.addColumn(Stock::getCompanyName)
                .setHeader("Company Name").setSortable(true).setAutoWidth(true).setFlexGrow(1);

        grid.addComponentColumn(stock -> {
            Span badge = new Span(stock.getSector() != null ? stock.getSector() : "General");
            badge.addClassName("st-badge-sector");
            return badge;
        }).setHeader("Sector").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(stock -> {
            Span price = new Span(currencyFormat.format(stock.getPrice()));
            price.addClassName("tabular-num");
            price.getStyle().set("font-weight", "600");
            return price;
        }).setHeader("Last Price").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(stock -> {
            double changePct = stock.getChangePercentAsDouble();
            String prefix = changePct > 0 ? "+" : "";
            Span changeSpan = new Span(prefix + percentFormat.format(changePct / 100.0));
            changeSpan.addClassName("tabular-num");
            if (changePct > 0) {
                changeSpan.addClassName("st-positive");
            } else if (changePct < 0) {
                changeSpan.addClassName("st-negative");
            } else {
                changeSpan.getStyle().set("color", "var(--st-text-muted)");
            }
            changeSpan.getStyle().set("font-weight", "600").set("font-size", "12px");
            return changeSpan;
        }).setHeader("Change").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(stock -> {
            Button buyBtn = new Button("Buy");
            buyBtn.addClassNames("st-btn", "st-btn-buy");
            buyBtn.getElement().removeAttribute("theme");
            buyBtn.getStyle().set("height", "28px").set("font-size", "12px").set("padding", "0 12px");
            buyBtn.addClickListener(e -> openBuyDialog(stock));
            return buyBtn;
        }).setHeader("Action").setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        tableContainer = new Div(grid);
        tableContainer.addClassName("st-table-surface");

        add(headerContainer, toolbar, emptyState, tableContainer);

        updateList();
        updateSummary();

        // Real-time polling listener
        UI.getCurrent().setPollInterval(1000);
        UI.getCurrent().addPollListener(e -> {
            updateList();
            updateSummary();
        });
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

    private void updateSummary() {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user != null) {
            try {
                Portfolio p = portfolioService.getPortfolioForUser(user.getId());
                PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(p);
                cashValue.setText(currencyFormat.format(summary.cash));
            } catch (DataAccessException ignored) { }
        }
    }

    private void updateList() {
        try {
            List<Stock> stocks = stockService.search(searchField.getValue());

            String selectedSector = sectorFilter.getValue();
            if (selectedSector != null && !selectedSector.isEmpty()) {
                stocks = stocks.stream().filter(s -> selectedSector.equals(s.getSector())).collect(Collectors.toList());
            }

            if (stocks.isEmpty()) {
                emptyState.setVisible(true);
                tableContainer.setVisible(false);
                grid.setItems();
            } else {
                emptyState.setVisible(false);
                tableContainer.setVisible(true);
                grid.setItems(stocks);
                stockCountValue.setText(String.valueOf(stocks.size()));
            }
        } catch (DataAccessException e) {
            emptyState.setVisible(true);
            tableContainer.setVisible(false);
        }
    }

    private void openBuyDialog(Stock stock) {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) {
            Notification.show("Please log in to trade.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Buy Order · " + stock.getSymbol());
        dialog.setWidth("min(420px, 94vw)");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setWidthFull();

        Span companySpan = new Span(stock.getCompanyName());
        companySpan.getStyle().set("font-size", "13px").set("color", "var(--st-text-secondary)").set("margin-bottom", "14px").set("display", "block");

        // Price row
        Div priceRow = new Div();
        priceRow.addClassName("st-dialog-row");
        Span priceLabel = new Span("Current Execution Price");
        priceLabel.addClassName("st-dialog-row-label");
        Span priceValue = new Span(currencyFormat.format(stock.getPrice()));
        priceValue.addClassName("st-dialog-row-value");
        priceRow.add(priceLabel, priceValue);

        // Cash row
        Div cashRow = new Div();
        cashRow.addClassName("st-dialog-row");
        Span cashLabel = new Span("Available Cash");
        cashLabel.addClassName("st-dialog-row-label");
        Span cashVal = new Span("-");
        cashVal.addClassName("st-dialog-row-value");
        BigDecimal currentCash = BigDecimal.ZERO;
        try {
            Portfolio p = portfolioService.getPortfolioForUser(user.getId());
            PortfolioService.PortfolioSummary s = portfolioService.buildSummary(p);
            currentCash = s.cash;
            cashVal.setText(currencyFormat.format(currentCash));
        } catch (DataAccessException ignored) { }
        cashRow.add(cashLabel, cashVal);

        // Quantity input
        IntegerField qtyField = new IntegerField("Order Quantity (Shares)");
        qtyField.setMin(1);
        qtyField.setMax(100000);
        qtyField.setStepButtonsVisible(true);
        qtyField.setValue(1);
        qtyField.setRequiredIndicatorVisible(true);
        qtyField.setWidthFull();
        qtyField.getStyle().set("margin-top", "12px");

        // Estimated order summary
        Div estDiv = new Div();
        estDiv.addClassName("st-dialog-estimate");

        Div estRow = new Div();
        estRow.addClassName("st-dialog-row");
        Span estLabel = new Span("Estimated Order Value");
        estLabel.addClassName("st-dialog-row-label");
        Span estValue = new Span(currencyFormat.format(stock.getPrice()));
        estValue.addClassName("st-dialog-row-value");
        estRow.add(estLabel, estValue);

        Div remCashRow = new Div();
        remCashRow.addClassName("st-dialog-row");
        Span remCashLabel = new Span("Estimated Remaining Cash");
        remCashLabel.addClassName("st-dialog-row-label");
        Span remCashValue = new Span("-");
        remCashValue.addClassName("st-dialog-row-value");
        remCashRow.add(remCashLabel, remCashValue);

        final BigDecimal userCash = currentCash;
        BigDecimal initialRemaining = userCash.subtract(stock.getPrice());
        remCashValue.setText(currencyFormat.format(initialRemaining));
        if (initialRemaining.compareTo(BigDecimal.ZERO) < 0) {
            remCashValue.addClassName("st-negative");
        }

        estDiv.add(estRow, remCashRow);

        qtyField.addValueChangeListener(e -> {
            if (e.getValue() != null && e.getValue() > 0) {
                BigDecimal totalCost = stock.getPrice().multiply(new BigDecimal(e.getValue()));
                estValue.setText(currencyFormat.format(totalCost));
                BigDecimal remaining = userCash.subtract(totalCost);
                remCashValue.setText(currencyFormat.format(remaining));
                if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                    remCashValue.addClassName("st-negative");
                } else {
                    remCashValue.removeClassName("st-negative");
                }
            }
        });

        Span note = new Span("Execution price is settled on the server at time of receipt.");
        note.addClassName("st-dialog-note");

        layout.add(companySpan, priceRow, cashRow, qtyField, estDiv, note);
        dialog.add(layout);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassNames("st-btn", "st-btn-secondary");
        cancelBtn.getElement().removeAttribute("theme");

        Button confirmBtn = new Button("Execute Buy");
        confirmBtn.addClassNames("st-btn", "st-btn-buy");
        confirmBtn.getElement().removeAttribute("theme");

        confirmBtn.addClickListener(e -> {
            Integer qty = qtyField.getValue();
            if (qty == null || qty < 1) {
                Notification.show("Please enter a valid share quantity.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            confirmBtn.setEnabled(false);
            confirmBtn.setText("Executing...");

            try {
                Trade trade = tradingService.buy(user.getId(), stock.getId(), qty);
                Notification.show("Executed BUY: " + trade.getQuantity() + " shares of " + trade.getStockSymbol() +
                                " at " + currencyFormat.format(trade.getPrice()) + " (Total: " + currencyFormat.format(trade.getTotalAmount()) + ")",
                        5000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                updateSummary();
            } catch (AppException ex) {
                Notification.show("Execution rejected: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception ex) {
                Notification.show("Order processing error.", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } finally {
                confirmBtn.setEnabled(true);
                confirmBtn.setText("Execute Buy");
            }
        });

        dialog.getFooter().add(cancelBtn, confirmBtn);
        dialog.open();
    }
}
