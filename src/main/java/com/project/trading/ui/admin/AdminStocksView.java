package com.project.trading.ui.admin;

import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
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

import java.util.List;

@Route(value = "admin/stocks", layout = AdminLayout.class)
@PageTitle("Stock Management | Admin")
public class AdminStocksView extends VerticalLayout {

    private final StockService stockService = new StockService();
    private final Grid<Stock> grid = new Grid<>(Stock.class, false);
    private final TextField searchField = new TextField();

    public AdminStocksView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Stock Management");
        title.getStyle().set("margin-top", "0").set("color", "var(--st-text-main)");

        configureGrid();

        searchField.setPlaceholder("Search by symbol or name...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> refreshData());
        searchField.setWidth("300px");

        Button addBtn = new Button("Add Stock", e -> openStockForm(null));
        addBtn.getStyle().set("background", "var(--st-primary-color)").set("color", "white");

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addBtn);
        toolbar.getStyle().set("margin-bottom", "16px");

        add(title, toolbar, grid);
        refreshData();
    }

    private void configureGrid() {
        grid.addColumn(Stock::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Stock::getSymbol).setHeader("Symbol").setSortable(true);
        grid.addColumn(Stock::getCompanyName).setHeader("Company Name").setSortable(true);
        grid.addColumn(Stock::getSector).setHeader("Sector").setSortable(true);
        grid.addColumn(Stock::getPrice).setHeader("Price").setSortable(true);
        
        grid.addComponentColumn(stock -> {
            Span statusSpan = new Span(stock.isActive() ? "ACTIVE" : "INACTIVE");
            statusSpan.getStyle().set("padding", "4px 8px").set("border-radius", "4px").set("font-size", "0.85rem");
            if (stock.isActive()) {
                statusSpan.getStyle().set("background", "rgba(16, 185, 129, 0.1)").set("color", "var(--st-success-color)");
            } else {
                statusSpan.getStyle().set("background", "rgba(239, 68, 68, 0.1)").set("color", "var(--st-danger-color)");
            }
            return statusSpan;
        }).setHeader("Status").setSortable(true);

        grid.addComponentColumn(stock -> {
            HorizontalLayout actions = new HorizontalLayout();
            Button editBtn = new Button("Edit", e -> openStockForm(stock));
            editBtn.getStyle().set("color", "var(--st-primary-color)");

            Button toggleBtn = new Button(stock.isActive() ? "Deactivate" : "Activate", e -> confirmToggleActive(stock));
            toggleBtn.getStyle().set("color", stock.isActive() ? "var(--st-danger-color)" : "var(--st-success-color)");

            actions.add(editBtn, toggleBtn);
            return actions;
        }).setHeader("Actions");

        grid.addThemeName("row-stripes");
    }

    private void refreshData() {
        String filter = searchField.getValue();
        List<Stock> stocks;
        if (filter != null && !filter.trim().isEmpty()) {
            stocks = stockService.search(filter);
            // StockService.search currently only returns active. Let's do client-side filter to include inactive.
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

        Paragraph text = new Paragraph("Are you sure you want to " + (stock.isActive() ? "deactivate" : "activate") + 
                                       " stock '" + stock.getSymbol() + "'?");
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
        confirm.getStyle().set("background", "var(--st-primary-color)").set("color", "white");

        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancel, confirm);
        dialog.open();
    }

    private void openStockForm(Stock stock) {
        Dialog dialog = new Dialog();
        boolean isNew = (stock == null);
        dialog.setHeaderTitle(isNew ? "Add New Stock" : "Edit Stock");

        FormLayout form = new FormLayout();

        TextField symbolField = new TextField("Symbol");
        TextField nameField = new TextField("Company Name");
        TextField sectorField = new TextField("Sector");
        TextArea descField = new TextArea("Description");
        BigDecimalField priceField = new BigDecimalField("Price");

        if (!isNew) {
            symbolField.setValue(stock.getSymbol());
            symbolField.setReadOnly(true); // symbol cannot be changed
            nameField.setValue(stock.getCompanyName());
            sectorField.setValue(stock.getSector() != null ? stock.getSector() : "");
            descField.setValue(stock.getDescription() != null ? stock.getDescription() : "");
            priceField.setValue(stock.getPrice());
            priceField.setReadOnly(true); // price managed separately
        }

        form.add(symbolField, nameField, sectorField, priceField, descField);
        dialog.add(form);

        Button save = new Button("Save", e -> {
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
        save.getStyle().set("background", "var(--st-primary-color)").set("color", "white");

        Button cancel = new Button("Cancel", e -> dialog.close());
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
