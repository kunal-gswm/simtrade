package com.project.trading.ui.admin;

import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "admin/stocks", layout = AdminLayout.class)
@PageTitle("Stock Catalog | SimTrade Admin")
public class AdminStocksView extends VerticalLayout {

    private final StockService stockService = new StockService();
    private final Grid<Stock> grid = new Grid<>(Stock.class, false);
    private final TextField searchField = new TextField();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public AdminStocksView() {
        addClassName("st-page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Div headerContainer = new Div();
        headerContainer.addClassName("st-header-container");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");

        H1 title = new H1("Stock Catalog Management");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Register new equity instruments, update company profiles, and manage tradability.");
        subtitle.addClassName("st-page-subtitle");

        titleGroup.add(title, subtitle);
        headerContainer.add(titleGroup);
        add(headerContainer);

        configureGrid();

        searchField.setPlaceholder("Search symbol or company name...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> refreshData());
        searchField.addClassName("st-search-field");
        searchField.setWidth("min(300px, 100%)");

        Button addBtn = new Button("Register New Stock", e -> openStockForm(null));
        addBtn.addClassNames("st-btn", "st-btn-primary");
        addBtn.getElement().removeAttribute("theme");

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addBtn);
        toolbar.getStyle().set("margin-bottom", "16px").set("align-items", "center").set("flex-wrap", "wrap");

        Div tableSurface = new Div(grid);
        tableSurface.addClassName("st-table-surface");
        tableSurface.setWidthFull();

        add(toolbar, tableSurface);
        refreshData();
    }

    private void configureGrid() {
        grid.addColumn(Stock::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);

        grid.addComponentColumn(stock -> {
            Span symbol = new Span(stock.getSymbol());
            symbol.addClassName("st-symbol");
            return symbol;
        }).setHeader("Symbol").setSortable(true).setAutoWidth(true);

        grid.addColumn(Stock::getCompanyName).setHeader("Company Name").setSortable(true).setFlexGrow(1);

        grid.addComponentColumn(stock -> {
            Span badge = new Span(stock.getSector() != null ? stock.getSector() : "General");
            badge.addClassName("st-badge-sector");
            return badge;
        }).setHeader("Sector").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(stock -> {
            Span price = new Span(currencyFormat.format(stock.getPrice()));
            price.addClassName("tabular-num");
            return price;
        }).setHeader("Current Price").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(stock -> {
            Span statusSpan = new Span(stock.isActive() ? "ACTIVE" : "INACTIVE");
            statusSpan.addClassName("st-badge");
            if (stock.isActive()) {
                statusSpan.addClassName("st-badge-buy");
            } else {
                statusSpan.addClassName("st-badge-sell");
            }
            return statusSpan;
        }).setHeader("Status").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(stock -> {
            HorizontalLayout actions = new HorizontalLayout();
            Button editBtn = new Button("Edit", e -> openStockForm(stock));
            editBtn.addClassNames("st-btn", "st-btn-secondary");
            editBtn.getElement().removeAttribute("theme");
            editBtn.getStyle().set("height", "26px").set("font-size", "11px").set("padding", "0 10px");

            Button toggleBtn = new Button(stock.isActive() ? "Deactivate" : "Activate", e -> confirmToggleActive(stock));
            toggleBtn.addClassNames("st-btn", stock.isActive() ? "st-btn-sell" : "st-btn-buy");
            toggleBtn.getElement().removeAttribute("theme");
            toggleBtn.getStyle().set("height", "26px").set("font-size", "11px").set("padding", "0 10px");

            actions.add(editBtn, toggleBtn);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        grid.setAllRowsVisible(true);
    }

    private void refreshData() {
        String filter = searchField.getValue();
        List<Stock> stocks;
        if (filter != null && !filter.trim().isEmpty()) {
            List<Stock> all = stockService.listAll();
            stocks = all.stream().filter(s ->
                    s.getSymbol().toLowerCase().contains(filter.toLowerCase()) ||
                            s.getCompanyName().toLowerCase().contains(filter.toLowerCase())
            ).toList();
        } else {
            stocks = stockService.listAll();
        }
        grid.setItems(stocks);
    }

    private void confirmToggleActive(Stock stock) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(stock.isActive() ? "Confirm Deactivation" : "Confirm Activation");
        dialog.setWidth("min(400px, 92vw)");

        Paragraph text = new Paragraph("Are you sure you want to " + (stock.isActive() ? "deactivate" : "activate") +
                " stock '" + stock.getSymbol() + "'? " +
                (stock.isActive() ? "Traders will no longer be able to place buy orders for this instrument." : "Traders will be able to trade this instrument again."));
        text.getStyle().set("color", "var(--st-text-secondary)").set("font-size", "13px").set("line-height", "1.5");
        dialog.add(text);

        Button confirm = new Button("Confirm", e -> {
            try {
                stockService.setActive(stock.getId(), !stock.isActive());
                showSuccess("Stock " + stock.getSymbol() + " successfully " + (stock.isActive() ? "deactivated" : "activated") + ".");
                refreshData();
                dialog.close();
            } catch (Exception ex) {
                showError("Failed to update stock status: " + ex.getMessage());
            }
        });
        confirm.addClassNames("st-btn", stock.isActive() ? "st-btn-sell" : "st-btn-buy");
        confirm.getElement().removeAttribute("theme");

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addClassNames("st-btn", "st-btn-secondary");
        cancel.getElement().removeAttribute("theme");

        dialog.getFooter().add(cancel, confirm);
        dialog.open();
    }

    private void openStockForm(Stock stock) {
        Dialog dialog = new Dialog();
        boolean isNew = (stock == null);
        dialog.setHeaderTitle(isNew ? "Register New Equity" : "Edit Stock · " + stock.getSymbol());
        dialog.setWidth("min(480px, 92vw)");

        FormLayout form = new FormLayout();

        TextField symbolField = new TextField("Symbol");
        TextField nameField = new TextField("Company Name");
        TextField sectorField = new TextField("Sector");
        TextArea descField = new TextArea("Description");
        BigDecimalField priceField = new BigDecimalField("Initial Reference Price");

        if (!isNew) {
            symbolField.setValue(stock.getSymbol());
            symbolField.setReadOnly(true);
            nameField.setValue(stock.getCompanyName());
            sectorField.setValue(stock.getSector() != null ? stock.getSector() : "");
            descField.setValue(stock.getDescription() != null ? stock.getDescription() : "");
            priceField.setValue(stock.getPrice());
            priceField.setReadOnly(true);
        }

        form.add(symbolField, nameField, sectorField, priceField, descField);
        dialog.add(form);

        Button save = new Button("Save Instrument", e -> {
            try {
                if (isNew) {
                    stockService.add(symbolField.getValue(), nameField.getValue(), sectorField.getValue(),
                            descField.getValue(), priceField.getValue());
                    showSuccess("Stock created successfully.");
                } else {
                    stockService.update(stock.getId(), nameField.getValue(), sectorField.getValue(), descField.getValue());
                    showSuccess("Stock updated successfully.");
                }
                refreshData();
                dialog.close();
            } catch (Exception ex) {
                showError("Validation Error: " + ex.getMessage());
            }
        });
        save.addClassNames("st-btn", "st-btn-primary");
        save.getElement().removeAttribute("theme");

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addClassNames("st-btn", "st-btn-secondary");
        cancel.getElement().removeAttribute("theme");

        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private void showSuccess(String msg) {
        Notification n = Notification.show(msg, 3000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String msg) {
        Notification n = Notification.show(msg, 5000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
