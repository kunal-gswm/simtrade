package com.project.trading.ui.admin;

import com.project.trading.model.Trade;
import com.project.trading.service.AdminService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "admin/monitor", layout = AdminLayout.class)
@PageTitle("Trade Monitor | Admin")
public class AdminTradeMonitorView extends VerticalLayout {

    private final AdminService adminService = new AdminService();
    private final Grid<Trade> grid = new Grid<>(Trade.class, false);
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

    public AdminTradeMonitorView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Global Trade Monitor");
        title.getStyle().set("margin-top", "0").set("color", "var(--st-text-main)");

        configureGrid();
        
        add(title, grid);
        
        loadData();
    }

    private void configureGrid() {
        grid.addColumn(trade -> dateFormat.format(trade.getExecutedAt())).setHeader("Timestamp").setSortable(true).setAutoWidth(true);
        grid.addColumn(Trade::getUsername).setHeader("User").setSortable(true);
        grid.addColumn(Trade::getStockSymbol).setHeader("Stock").setSortable(true);
        
        grid.addComponentColumn(trade -> {
            Span typeSpan = new Span(trade.getType().name());
            typeSpan.getStyle().set("padding", "4px 8px").set("border-radius", "4px").set("font-size", "0.85rem").set("font-weight", "500");
            if (trade.getType().name().equals("BUY")) {
                typeSpan.getStyle().set("background", "rgba(16, 185, 129, 0.1)").set("color", "var(--st-success-color)");
            } else {
                typeSpan.getStyle().set("background", "rgba(239, 68, 68, 0.1)").set("color", "var(--st-danger-color)");
            }
            return typeSpan;
        }).setHeader("Type").setSortable(true);
        
        grid.addColumn(Trade::getQuantity).setHeader("Quantity").setSortable(true);
        grid.addColumn(trade -> currencyFormat.format(trade.getPrice())).setHeader("Execution Price").setSortable(true);
        grid.addColumn(trade -> currencyFormat.format(trade.getTotalAmount())).setHeader("Total Value").setSortable(true);

        grid.addThemeName("row-stripes");
    }

    private void loadData() {
        // Load latest 200 trades across all users
        List<Trade> recentTrades = adminService.getRecentTrades(200);
        grid.setItems(recentTrades);
    }
}
