package com.project.trading.ui.admin;

import com.project.trading.model.Trade;
import com.project.trading.service.AdminService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "admin/monitor", layout = AdminLayout.class)
@PageTitle("Global Trade Monitor | SimTrade Admin")
public class AdminTradeMonitorView extends VerticalLayout {

    private final AdminService adminService = new AdminService();
    private final Grid<Trade> grid = new Grid<>(Trade.class, false);
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

    public AdminTradeMonitorView() {
        addClassName("st-page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Div headerContainer = new Div();
        headerContainer.addClassName("st-header-container");

        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");

        H1 title = new H1("Global Trade Telemetry & Monitor");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Live order book settlement stream across all system users.");
        subtitle.addClassName("st-page-subtitle");

        titleGroup.add(title, subtitle);

        Button refreshBtn = new Button("Refresh Stream", e -> loadData());
        refreshBtn.addClassNames("st-btn", "st-btn-secondary");
        refreshBtn.getElement().removeAttribute("theme");

        headerContainer.add(titleGroup, refreshBtn);
        add(headerContainer);

        configureGrid();

        Div tableSurface = new Div(grid);
        tableSurface.addClassName("st-table-surface");
        tableSurface.setWidthFull();

        add(tableSurface);

        loadData();
    }

    private void configureGrid() {
        grid.addComponentColumn(trade -> {
            Span time = new Span(dateFormat.format(trade.getExecutedAt()));
            time.addClassName("tabular-num");
            return time;
        }).setHeader("Execution Timestamp").setSortable(true).setAutoWidth(true);

        grid.addColumn(Trade::getUsername).setHeader("Trader Account").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(trade -> {
            Span symbol = new Span(trade.getStockSymbol());
            symbol.addClassName("st-symbol");
            return symbol;
        }).setHeader("Symbol").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(trade -> {
            Span typeSpan = new Span(trade.getType().name());
            typeSpan.addClassName("st-badge");
            if ("BUY".equalsIgnoreCase(trade.getType().name())) {
                typeSpan.addClassName("st-badge-buy");
            } else {
                typeSpan.addClassName("st-badge-sell");
            }
            return typeSpan;
        }).setHeader("Side").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(trade -> {
            Span qty = new Span(String.valueOf(trade.getQuantity()));
            qty.addClassName("tabular-num");
            return qty;
        }).setHeader("Shares").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

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
        }).setHeader("Settled Amount").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.setAllRowsVisible(true);
    }

    private com.vaadin.flow.shared.Registration tradeRegistration;

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        loadData();
        tradeRegistration = com.project.trading.broadcaster.MarketDataBroadcaster.registerTradeListener(attachEvent.getUI(), trade -> {
            loadData();
        });
    }

    @Override
    protected void onDetach(com.vaadin.flow.component.DetachEvent detachEvent) {
        if (tradeRegistration != null) {
            tradeRegistration.remove();
            tradeRegistration = null;
        }
        super.onDetach(detachEvent);
    }

    private void loadData() {
        List<Trade> recentTrades = adminService.getRecentTrades(200);
        grid.setItems(recentTrades);
    }
}
