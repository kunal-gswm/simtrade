package com.project.trading.ui.views;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.service.MarketDataConnectionManager;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Portfolio;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.OrderValidity;
import com.project.trading.order.service.OrderService;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.StockService;
import com.project.trading.service.TradingService;
import com.project.trading.ui.components.TradingChartView;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.shared.Registration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Route(value = "ui/market", layout = MainLayout.class)
@PageTitle("Market Quotes | SimTrade")
public class MarketView extends Div {

    private final StockService stockService;
    private final TradingService tradingService;
    private final SecurityService securityService;
    private final PortfolioService portfolioService;
    private final OrderService orderService;
    private final Grid<Stock> grid;
    private final TextField searchField;
    private final ComboBox<String> sectorFilter;
    private final Span feedStatusBadge;
    private final NumberFormat currencyFormat;
    private final NumberFormat percentFormat;

    private final Span cashValue;
    private final Span stockCountValue;
    private final Div emptyState;
    private final Div tableContainer;
    private final TradingChartView chartView;

    private Registration priceListenerRegistration;
    private Registration tradeListenerRegistration;
    private Registration candleListenerRegistration;

    private static final String SVG_SEARCH = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.75\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z\" /></svg>";

    public MarketView() {
        this.stockService = new StockService();
        this.tradingService = new TradingService();
        this.securityService = new SecurityService();
        this.portfolioService = new PortfolioService();
        this.orderService = new OrderService();
        this.feedStatusBadge = new Span();
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
        Paragraph subtitle = new Paragraph("Institutional Paper Engine — Simulated Indian equity instruments, real-time spread data, session-aligned OHLCV and order execution.");
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

        feedStatusBadge.addClassName("st-brand-tag");
        updateFeedBadge();

        HorizontalLayout toolbar = new HorizontalLayout(searchField, sectorFilter, feedStatusBadge);
        toolbar.getStyle().set("margin-bottom", "16px").set("flex-wrap", "wrap").set("align-items", "center");

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
            MarketQuote quote = MarketDataConnectionManager.getInstance().getLatestQuote(stock.getId());
            BigDecimal bid = (quote != null && quote.bidPrice() != null) ? quote.bidPrice() : stock.getPrice().subtract(new BigDecimal("0.05"));
            BigDecimal ask = (quote != null && quote.askPrice() != null) ? quote.askPrice() : stock.getPrice().add(new BigDecimal("0.05"));
            Span bidAsk = new Span("₹" + bid + " / ₹" + ask);
            bidAsk.addClassName("tabular-num");
            bidAsk.getStyle().set("font-size", "11px").set("color", "var(--st-text-secondary)");
            return bidAsk;
        }).setHeader("Bid / Ask").setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(stock -> {
            Span price = new Span(currencyFormat.format(stock.getPrice()));
            price.addClassName("tabular-num");
            price.getStyle().set("font-weight", "600");
            return price;
        }).setHeader("Last Traded Price (LTP)").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

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
        }).setHeader("24h Change").setSortable(true).setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(stock -> {
            Div tradeBtns = new Div();
            tradeBtns.getStyle().set("display", "flex").set("gap", "4px").set("justify-content", "flex-end");

            Button buyBtn = new Button("Buy");
            buyBtn.addClassNames("st-btn", "st-btn-buy");
            buyBtn.getElement().removeAttribute("theme");
            buyBtn.getStyle().set("height", "26px").set("font-size", "11px").set("padding", "0 10px");
            buyBtn.addClickListener(e -> openTradeDialog(stock, OrderSide.BUY));

            Button sellBtn = new Button("Sell");
            sellBtn.addClassNames("st-btn", "st-btn-sell");
            sellBtn.getElement().removeAttribute("theme");
            sellBtn.getStyle().set("height", "26px").set("font-size", "11px").set("padding", "0 10px");
            sellBtn.addClickListener(e -> openTradeDialog(stock, OrderSide.SELL));

            tradeBtns.add(buyBtn, sellBtn);
            return tradeBtns;
        }).setHeader("Trade").setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        tableContainer = new Div(grid);
        tableContainer.addClassName("st-table-surface");

        chartView = new TradingChartView();
        chartView.setHeight(360);

        grid.asSingleSelect().addValueChangeListener(e -> {
            Stock selected = e.getValue();
            if (selected != null) {
                chartView.setStock(selected);
            }
        });

        add(headerContainer, toolbar, chartView, emptyState, tableContainer);

        updateList();
        updateSummary();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();

        // Immediate fresh sync
        updateList();
        updateSummary();

        // Subscribe to live multi-symbol price broadcasts with UI lifecycle binding
        priceListenerRegistration = MarketDataBroadcaster.registerPriceListener(ui, event -> {
            updateList();
        });

        // Subscribe to live order executions to update available liquidity with UI lifecycle binding
        tradeListenerRegistration = MarketDataBroadcaster.registerTradeListener(ui, trade -> {
            updateSummary();
        });

        // Subscribe to live streaming candles for the interactive chart
        candleListenerRegistration = MarketDataBroadcaster.registerCandleListener(ui, candle -> {
            chartView.onCandleUpdate(candle);
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (priceListenerRegistration != null) {
            priceListenerRegistration.remove();
            priceListenerRegistration = null;
        }
        if (tradeListenerRegistration != null) {
            tradeListenerRegistration.remove();
            tradeListenerRegistration = null;
        }
        if (candleListenerRegistration != null) {
            candleListenerRegistration.remove();
            candleListenerRegistration = null;
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

    private void updateSummary() {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user != null) {
            try {
                Portfolio p = portfolioService.getPortfolioForUser(user.getId());
                PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(p);
                cashValue.setText(currencyFormat.format(summary.cash));
            } catch (DataAccessException ignored) { }
        }
        updateFeedBadge();
    }

    private void updateFeedBadge() {
        MarketDataConnectionManager mgr = MarketDataConnectionManager.getInstance();
        if (mgr.getSourceType() == MarketDataSource.LIVE_PROVIDER && mgr.getStatus() == MarketDataStatus.CONNECTED) {
            feedStatusBadge.setText("● LIVE: " + (mgr.getActiveProvider() != null ? mgr.getActiveProvider().getProviderName().toUpperCase() : "FEED"));
            feedStatusBadge.getStyle().set("color", "var(--st-jade)").set("border-color", "var(--st-jade)");
        } else if (mgr.getStatus() == MarketDataStatus.NOT_CONFIGURED) {
            feedStatusBadge.setText("● LIVE FEED: NOT CONFIGURED");
            feedStatusBadge.getStyle().set("color", "var(--st-ruby)").set("border-color", "var(--st-ruby)");
        } else {
            feedStatusBadge.setText("● SIMULATED DATA STREAM");
            feedStatusBadge.getStyle().set("color", "var(--st-copper)").set("border-color", "var(--st-copper)");
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
                chartView.setVisible(false);
                grid.setItems();
            } else {
                emptyState.setVisible(false);
                tableContainer.setVisible(true);
                chartView.setVisible(true);
                grid.setItems(stocks);
                stockCountValue.setText(String.valueOf(stocks.size()));
                if (chartView.getStock() == null) {
                    Stock first = stocks.get(0);
                    grid.select(first);
                    chartView.setStock(first);
                }
            }
        } catch (DataAccessException e) {
            emptyState.setVisible(true);
            tableContainer.setVisible(false);
        }
    }

    private void openTradeDialog(Stock stock, OrderSide initialSide) {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) {
            Notification.show("Please log in to trade.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Order Entry · " + stock.getSymbol());
        dialog.setWidth("min(460px, 94vw)");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setWidthFull();

        Span companySpan = new Span(stock.getCompanyName() + " (NSE: " + stock.getSymbol() + ")");
        companySpan.getStyle().set("font-size", "12px").set("color", "var(--st-text-secondary)").set("margin-bottom", "12px").set("display", "block");

        // Side selector (BUY / SELL toggle)
        HorizontalLayout sideLayout = new HorizontalLayout();
        sideLayout.setWidthFull();
        sideLayout.getStyle().set("margin-bottom", "12px");

        final OrderSide[] currentSide = new OrderSide[]{initialSide};
        Button buyToggle = new Button("BUY");
        Button sellToggle = new Button("SELL");
        buyToggle.setWidth("50%");
        sellToggle.setWidth("50%");

        Runnable updateToggleStyles = () -> {
            if (currentSide[0] == OrderSide.BUY) {
                buyToggle.addClassNames("st-btn", "st-btn-buy");
                buyToggle.getStyle().set("opacity", "1.0");
                sellToggle.addClassNames("st-btn", "st-btn-secondary");
                sellToggle.removeClassName("st-btn-sell");
                sellToggle.getStyle().set("opacity", "0.6");
            } else {
                sellToggle.addClassNames("st-btn", "st-btn-sell");
                sellToggle.getStyle().set("opacity", "1.0");
                buyToggle.addClassNames("st-btn", "st-btn-secondary");
                buyToggle.removeClassName("st-btn-buy");
                buyToggle.getStyle().set("opacity", "0.6");
            }
        };
        updateToggleStyles.run();

        buyToggle.addClickListener(e -> {
            currentSide[0] = OrderSide.BUY;
            updateToggleStyles.run();
        });
        sellToggle.addClickListener(e -> {
            currentSide[0] = OrderSide.SELL;
            updateToggleStyles.run();
        });
        sideLayout.add(buyToggle, sellToggle);

        // Order Type Selector
        Select<OrderType> orderTypeSelect = new Select<>();
        orderTypeSelect.setLabel("Order Type");
        orderTypeSelect.setItems(OrderType.values());
        orderTypeSelect.setItemLabelGenerator(OrderType::getDisplayName);
        orderTypeSelect.setValue(OrderType.MARKET);
        orderTypeSelect.setWidthFull();

        // Validity Selector
        Select<OrderValidity> validitySelect = new Select<>();
        validitySelect.setLabel("Order Validity");
        validitySelect.setItems(OrderValidity.values());
        validitySelect.setItemLabelGenerator(OrderValidity::getLabel);
        validitySelect.setValue(OrderValidity.DAY);
        validitySelect.setWidthFull();

        // Quantity Field
        IntegerField qtyField = new IntegerField("Quantity (Shares)");
        qtyField.setMin(1);
        qtyField.setMax(10000);
        qtyField.setStepButtonsVisible(true);
        qtyField.setValue(1);
        qtyField.setWidthFull();

        // Dynamic price inputs
        BigDecimalField limitPriceField = new BigDecimalField("Limit Price (₹)");
        limitPriceField.setValue(stock.getPrice());
        limitPriceField.setWidthFull();
        limitPriceField.setVisible(false);

        BigDecimalField triggerPriceField = new BigDecimalField("Trigger Price (₹)");
        triggerPriceField.setValue(stock.getPrice());
        triggerPriceField.setWidthFull();
        triggerPriceField.setVisible(false);

        BigDecimalField targetPriceField = new BigDecimalField("Target Exit Price (₹)");
        targetPriceField.setValue(stock.getPrice().multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP));
        targetPriceField.setWidthFull();
        targetPriceField.setVisible(false);

        BigDecimalField stopLossField = new BigDecimalField("Stop-Loss Exit Price (₹)");
        stopLossField.setValue(stock.getPrice().multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_UP));
        stopLossField.setWidthFull();
        stopLossField.setVisible(false);

        Runnable updateFieldVisibility = () -> {
            OrderType type = orderTypeSelect.getValue();
            limitPriceField.setVisible(type == OrderType.LIMIT || type == OrderType.STOP_LOSS_LIMIT || type == OrderType.BRACKET);
            triggerPriceField.setVisible(type == OrderType.STOP_LOSS_MARKET || type == OrderType.STOP_LOSS_LIMIT);
            targetPriceField.setVisible(type == OrderType.TARGET || type == OrderType.BRACKET);
            stopLossField.setVisible(type == OrderType.BRACKET);
        };
        orderTypeSelect.addValueChangeListener(e -> updateFieldVisibility.run());

        // Price and Availability row
        Div liquidityRow = new Div();
        liquidityRow.addClassName("st-dialog-row");
        Span ltpLabel = new Span("LTP / Reference");
        ltpLabel.addClassName("st-dialog-row-label");
        Span ltpVal = new Span(currencyFormat.format(stock.getPrice()));
        ltpVal.addClassName("st-dialog-row-value");
        liquidityRow.add(ltpLabel, ltpVal);

        Div estDiv = new Div();
        estDiv.addClassName("st-dialog-estimate");
        estDiv.getStyle().set("margin-top", "12px");

        Div estRow = new Div();
        estRow.addClassName("st-dialog-row");
        Span estLabel = new Span("Estimated Order Value");
        estLabel.addClassName("st-dialog-row-label");
        Span estVal = new Span(currencyFormat.format(stock.getPrice()));
        estVal.addClassName("st-dialog-row-value");
        estRow.add(estLabel, estVal);

        estDiv.add(estRow);

        qtyField.addValueChangeListener(e -> {
            if (e.getValue() != null && e.getValue() > 0) {
                BigDecimal p = limitPriceField.isVisible() && limitPriceField.getValue() != null ? limitPriceField.getValue() : stock.getPrice();
                BigDecimal total = p.multiply(new BigDecimal(e.getValue())).setScale(2, RoundingMode.HALF_UP);
                estVal.setText(currencyFormat.format(total));
            }
        });
        limitPriceField.addValueChangeListener(e -> {
            if (e.getValue() != null && qtyField.getValue() != null) {
                BigDecimal total = e.getValue().multiply(new BigDecimal(qtyField.getValue())).setScale(2, RoundingMode.HALF_UP);
                estVal.setText(currencyFormat.format(total));
            }
        });

        Span note = new Span("Execution Notice: Orders execute in simulated paper mode according to institutional rules.");
        note.addClassName("st-dialog-note");

        layout.add(companySpan, sideLayout, orderTypeSelect, validitySelect, qtyField,
                limitPriceField, triggerPriceField, targetPriceField, stopLossField,
                liquidityRow, estDiv, note);
        dialog.add(layout);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassNames("st-btn", "st-btn-secondary");

        Button submitBtn = new Button("Submit Order");
        submitBtn.addClassNames("st-btn", "st-btn-primary");

        submitBtn.addClickListener(e -> {
            Integer qty = qtyField.getValue();
            if (qty == null || qty < 1) {
                Notification.show("Please enter a valid share quantity.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Order order = new Order();
            order.setUserId(user.getId());
            order.setStockId(stock.getId());
            order.setSide(currentSide[0]);
            order.setOrderType(orderTypeSelect.getValue());
            order.setValidity(validitySelect.getValue());
            order.setQuantity(qty);

            if (limitPriceField.isVisible()) order.setLimitPrice(limitPriceField.getValue());
            if (triggerPriceField.isVisible()) order.setTriggerPrice(triggerPriceField.getValue());
            if (targetPriceField.isVisible()) order.setTargetPrice(targetPriceField.getValue());
            if (stopLossField.isVisible()) order.setStopLossPrice(stopLossField.getValue());

            submitBtn.setEnabled(false);
            submitBtn.setText("Submitting...");

            try {
                Order placed = orderService.placeOrder(order);
                String msg = "Order #" + placed.getId() + " (" + placed.getOrderType().getDisplayName() + " " + placed.getSide() + ") placed successfully. Status: " + placed.getStatus().getDisplayName();
                Notification.show(msg, 5000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                updateSummary();
            } catch (AppException ex) {
                Notification.show("Order rejected: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception ex) {
                Notification.show("Order processing error: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } finally {
                submitBtn.setEnabled(true);
                submitBtn.setText("Submit Order");
            }
        });

        dialog.getFooter().add(cancelBtn, submitBtn);
        dialog.open();
    }
}
