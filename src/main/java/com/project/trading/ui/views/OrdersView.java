package com.project.trading.ui.views;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.exception.AppException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.MarketSessionCalendar;
import com.project.trading.order.model.Fill;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderEvent;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.service.OrderService;
import com.project.trading.ui.security.SecurityService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.shared.Registration;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Route(value = "ui/orders", layout = MainLayout.class)
@PageTitle("Orders | SimTrade Terminal")
public class OrdersView extends Div {

    private final OrderService orderService;
    private final SecurityService securityService;
    private final NumberFormat currencyFormat;
    private final DateTimeFormatter timeFormatter;

    private final Span openOrdersStat;
    private final Span executedOrdersStat;
    private final Span lockedCashStat;
    private final Span totalOrdersStat;

    private final Tabs filterTabs;
    private final Grid<Order> grid;
    private final Div emptyState;
    private final Div tableContainer;

    private List<Order> allOrders = new ArrayList<>();
    private String currentFilter = "ALL";
    private Registration broadcasterRegistration;

    public OrdersView() {
        this.orderService = new OrderService();
        this.securityService = new SecurityService();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        this.timeFormatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm:ss").withZone(MarketSessionCalendar.ZONE_IST);

        addClassName("st-page");

        // Title Group
        Div titleGroup = new Div();
        titleGroup.addClassName("st-page-title-group");
        H2 title = new H2("Order Book & Executions");
        title.addClassName("st-page-title");
        Paragraph subtitle = new Paragraph("Track active pending orders, trigger conditions, executed fills, and atomic bracket exits.");
        subtitle.addClassName("st-page-subtitle");
        titleGroup.add(title, subtitle);

        // Stats Row
        Div statsRow = new Div();
        statsRow.addClassName("st-stats-row");

        openOrdersStat = new Span("-");
        openOrdersStat.addClassName("st-stat-value");
        statsRow.add(createStatCard("Open & Pending", openOrdersStat, "Awaiting execution / trigger"));

        executedOrdersStat = new Span("-");
        executedOrdersStat.addClassName("st-stat-value");
        statsRow.add(createStatCard("Executed Fills", executedOrdersStat, "Filled orders"));

        lockedCashStat = new Span("-");
        lockedCashStat.addClassName("st-stat-value");
        statsRow.add(createStatCard("Locked Margin", lockedCashStat, "Reserved funds for open buys"));

        totalOrdersStat = new Span("-");
        totalOrdersStat.addClassName("st-stat-value");
        statsRow.add(createStatCard("Total Orders", totalOrdersStat, "Lifetime order submissions"));

        Div headerContainer = new Div(titleGroup, statsRow);
        headerContainer.addClassName("st-header-container");

        // Filter Tabs
        Tab tabAll = new Tab("All Orders");
        Tab tabOpen = new Tab("Open & Pending");
        Tab tabExecuted = new Tab("Executed");
        Tab tabCancelled = new Tab("Cancelled / Rejected");

        filterTabs = new Tabs(tabAll, tabOpen, tabExecuted, tabCancelled);
        filterTabs.addClassName("st-filter-tabs");
        filterTabs.addSelectedChangeListener(e -> {
            Tab selected = e.getSelectedTab();
            if (selected == tabOpen) {
                currentFilter = "OPEN";
            } else if (selected == tabExecuted) {
                currentFilter = "EXECUTED";
            } else if (selected == tabCancelled) {
                currentFilter = "CANCELLED";
            } else {
                currentFilter = "ALL";
            }
            applyFilter();
        });

        // Grid
        grid = new Grid<>(Order.class, false);
        grid.setAllRowsVisible(true);

        grid.addComponentColumn(order -> {
            Span s = new Span("#" + order.getId());
            s.addClassName("tabular-num");
            s.getStyle().set("color", "var(--st-text-muted)").set("font-size", "12px").set("font-weight", "600");
            return s;
        }).setHeader("Order ID").setAutoWidth(true);

        grid.addComponentColumn(order -> {
            String timeStr = order.getCreatedAt() != null ? timeFormatter.format(order.getCreatedAt()) : "-";
            Span s = new Span(timeStr);
            s.addClassName("tabular-num");
            s.getStyle().set("font-size", "12px");
            return s;
        }).setHeader("Placed Time").setAutoWidth(true).setSortable(true);

        grid.addComponentColumn(order -> {
            Div stockInfo = new Div();
            stockInfo.getStyle().set("display", "flex").set("flex-direction", "column");
            Span symbol = new Span(order.getStock() != null ? order.getStock().getSymbol() : "Stock #" + order.getStockId());
            symbol.addClassName("st-symbol");
            stockInfo.add(symbol);
            return stockInfo;
        }).setHeader("Instrument").setAutoWidth(true);

        grid.addComponentColumn(order -> {
            Span side = new Span(order.getSide().name());
            side.addClassName(order.getSide() == OrderSide.BUY ? "st-badge-buy" : "st-badge-sell");
            return side;
        }).setHeader("Side").setAutoWidth(true);

        grid.addComponentColumn(order -> {
            Span typeBadge = new Span(order.getOrderType().getDisplayName());
            typeBadge.addClassName("st-brand-tag");
            return typeBadge;
        }).setHeader("Type").setAutoWidth(true);

        grid.addComponentColumn(order -> {
            Span qty = new Span(order.getFilledQuantity() + " / " + order.getQuantity());
            qty.addClassName("tabular-num");
            qty.getStyle().set("font-weight", "600");
            return qty;
        }).setHeader("Qty Filled").setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(order -> {
            String priceStr;
            if (order.getLimitPrice() != null) {
                priceStr = "₹" + order.getLimitPrice();
            } else if (order.getTriggerPrice() != null) {
                priceStr = "Trig: ₹" + order.getTriggerPrice();
            } else {
                priceStr = "MKT";
            }
            Span price = new Span(priceStr);
            price.addClassName("tabular-num");
            return price;
        }).setHeader("Price").setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(order -> {
            String fillStr = order.getAverageFillPrice() != null ? "₹" + order.getAverageFillPrice() : "-";
            Span fill = new Span(fillStr);
            fill.addClassName("tabular-num");
            return fill;
        }).setHeader("Avg Price").setTextAlign(ColumnTextAlign.END).setAutoWidth(true);

        grid.addComponentColumn(order -> {
            Span statusBadge = new Span(order.getStatus().getDisplayName());
            switch (order.getStatus()) {
                case OPEN, TRIGGER_PENDING -> statusBadge.addClassName("st-badge-buy");
                case FILLED -> {
                    statusBadge.getStyle().set("color", "var(--st-jade)").set("font-weight", "600");
                }
                case CANCELLED, REJECTED, EXPIRED -> {
                    statusBadge.getStyle().set("color", "var(--st-ruby)").set("font-weight", "500");
                }
                default -> statusBadge.addClassName("st-badge-sell");
            }
            return statusBadge;
        }).setHeader("Status").setAutoWidth(true);

        // Actions: Cancel & Details
        grid.addComponentColumn(order -> {
            Div actions = new Div();
            actions.getStyle().set("display", "flex").set("gap", "6px").set("align-items", "center");

            if (order.isCancellable()) {
                Button cancelBtn = new Button("Cancel", e -> handleCancel(order));
                cancelBtn.addClassName("st-btn-secondary");
                cancelBtn.getStyle().set("color", "var(--st-ruby)").set("padding", "2px 8px").set("font-size", "11px");
                actions.add(cancelBtn);
            }

            Button detailsBtn = new Button("Details", e -> showOrderDetails(order));
            detailsBtn.addClassName("st-btn-secondary");
            detailsBtn.getStyle().set("padding", "2px 8px").set("font-size", "11px");
            actions.add(detailsBtn);

            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        tableContainer = new Div(grid);
        tableContainer.addClassName("st-table-container");

        // Empty state
        emptyState = new Div();
        emptyState.addClassName("st-empty-state");
        emptyState.setVisible(false);

        Span emptyIcon = new Span();
        emptyIcon.getElement().setProperty("innerHTML", "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M9 12h3.75M9 15h3.75M9 18h3.75m3 .75H18a2.25 2.25 0 002.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 00-1.123-.08m-5.801 0c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 00.75-.75 2.25 2.25 0 00-.1-.664m-5.8 0A2.251 2.251 0 0113.5 2.25H15c1.012 0 1.867.668 2.15 1.586m-5.8 0c-.376.023-.75.05-1.124.08C9.095 4.01 8.25 4.973 8.25 6.108V8.25m0 0H4.875c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V9.375c0-.621-.504-1.125-1.125-1.125H8.25zM6.75 12h.008v.008H6.75V12zm0 3h.008v.008H6.75V15zm0 3h.008v.008H6.75V18z\" /></svg>");
        emptyIcon.addClassName("st-empty-icon");

        H3 emptyTitle = new H3("No Orders Found");
        emptyTitle.addClassName("st-empty-title");

        Paragraph emptyDesc = new Paragraph("You haven't placed any orders in this category yet. Visit Market Quotes to submit limit, stop-loss, or bracket orders.");
        emptyDesc.addClassName("st-empty-desc");

        RouterLink tradeLink = new RouterLink("Go to Market Quotes", MarketView.class);
        tradeLink.addClassName("st-btn-primary");
        tradeLink.getStyle().set("display", "inline-flex").set("margin-top", "12px");

        emptyState.add(emptyIcon, emptyTitle, emptyDesc, tradeLink);

        add(headerContainer, filterTabs, tableContainer, emptyState);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        loadOrders();
        UI ui = attachEvent.getUI();
        broadcasterRegistration = MarketDataBroadcaster.register(ui, new MarketDataBroadcaster.BroadcastListener() {
            @Override
            public void onTradeUpdate(MarketDataBroadcaster.TradeUpdate event) {
                ui.access(() -> loadOrders());
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

    private void loadOrders() {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) return;

        try {
            allOrders = orderService.getUserOrders(user.getId(), null, 200);

            // Compute Stats
            long openCount = allOrders.stream().filter(o -> o.getStatus().isActive()).count();
            long executedCount = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.FILLED).count();
            BigDecimal lockedCash = orderService.getUserReservedFunds(user.getId());

            openOrdersStat.setText(String.valueOf(openCount));
            executedOrdersStat.setText(String.valueOf(executedCount));
            lockedCashStat.setText(currencyFormat.format(lockedCash));
            totalOrdersStat.setText(String.valueOf(allOrders.size()));

            applyFilter();
        } catch (Exception e) {
            Notification.show("Error loading orders: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void applyFilter() {
        List<Order> filtered = allOrders.stream().filter(o -> {
            return switch (currentFilter) {
                case "OPEN" -> o.getStatus().isActive();
                case "EXECUTED" -> o.getStatus() == OrderStatus.FILLED;
                case "CANCELLED" -> o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.REJECTED || o.getStatus() == OrderStatus.EXPIRED;
                default -> true;
            };
        }).toList();

        grid.setItems(filtered);
        boolean isEmpty = filtered.isEmpty();
        tableContainer.setVisible(!isEmpty);
        emptyState.setVisible(isEmpty);
    }

    private void handleCancel(Order order) {
        AuthUser user = securityService.getAuthenticatedUser();
        if (user == null) return;

        try {
            orderService.cancelOrder(order.getId(), user.getId());
            Notification.show("Order #" + order.getId() + " cancelled successfully", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadOrders();
        } catch (AppException e) {
            Notification.show(e.getMessage(), 4000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showOrderDetails(Order order) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Order #" + order.getId() + " — Execution Details");
        dialog.setWidth("640px");

        Div content = new Div();
        content.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "16px");

        // Summary Info
        Div infoGrid = new Div();
        infoGrid.getStyle().set("display", "grid").set("grid-template-columns", "1fr 1fr").set("gap", "8px")
                .set("background", "var(--st-bg-surface)").set("padding", "12px").set("border-radius", "6px");

        infoGrid.add(createDetailItem("Instrument", order.getStock() != null ? order.getStock().getSymbol() : String.valueOf(order.getStockId())));
        infoGrid.add(createDetailItem("Side & Type", order.getSide() + " " + order.getOrderType().getDisplayName()));
        infoGrid.add(createDetailItem("Quantity", order.getFilledQuantity() + " / " + order.getQuantity()));
        infoGrid.add(createDetailItem("Status", order.getStatus().getDisplayName()));
        if (order.getLimitPrice() != null) infoGrid.add(createDetailItem("Limit Price", "₹" + order.getLimitPrice()));
        if (order.getTriggerPrice() != null) infoGrid.add(createDetailItem("Trigger Price", "₹" + order.getTriggerPrice()));
        if (order.getTargetPrice() != null) infoGrid.add(createDetailItem("Target Price", "₹" + order.getTargetPrice()));
        if (order.getStopLossPrice() != null) infoGrid.add(createDetailItem("Stop-Loss", "₹" + order.getStopLossPrice()));
        if (order.getRejectionReason() != null) infoGrid.add(createDetailItem("Note / Reason", order.getRejectionReason()));

        content.add(infoGrid);

        // Fills Section
        List<Fill> fills = orderService.getOrderFills(order.getId());
        if (!fills.isEmpty()) {
            H3 fillsTitle = new H3("Execution Fills");
            fillsTitle.getStyle().set("font-size", "14px").set("margin-top", "8px").set("color", "var(--st-text-primary)");
            Grid<Fill> fillsGrid = new Grid<>(Fill.class, false);
            fillsGrid.setAllRowsVisible(true);
            fillsGrid.addColumn(f -> timeFormatter.format(f.getFillTime())).setHeader("Time").setAutoWidth(true);
            fillsGrid.addColumn(Fill::getFillQuantity).setHeader("Qty").setAutoWidth(true);
            fillsGrid.addColumn(f -> "₹" + f.getFillPrice()).setHeader("Price").setAutoWidth(true);
            fillsGrid.addColumn(f -> "₹" + f.getTotalAmount()).setHeader("Turnover").setAutoWidth(true);
            fillsGrid.addColumn(f -> "₹" + f.getTotalFees()).setHeader("Fees").setAutoWidth(true);
            fillsGrid.setItems(fills);
            content.add(fillsTitle, fillsGrid);
        }

        // Audit Trail Events
        List<OrderEvent> events = orderService.getOrderEvents(order.getId());
        if (!events.isEmpty()) {
            H3 eventsTitle = new H3("Audit Trail");
            eventsTitle.getStyle().set("font-size", "14px").set("margin-top", "8px").set("color", "var(--st-text-primary)");
            Grid<OrderEvent> eventsGrid = new Grid<>(OrderEvent.class, false);
            eventsGrid.setAllRowsVisible(true);
            eventsGrid.addColumn(e -> timeFormatter.format(e.eventTime())).setHeader("Time").setAutoWidth(true);
            eventsGrid.addColumn(e -> (e.fromStatus() != null ? e.fromStatus() : "START") + " ➔ " + e.toStatus()).setHeader("Transition").setAutoWidth(true);
            eventsGrid.addColumn(OrderEvent::reason).setHeader("Reason / Trigger").setAutoWidth(true);
            eventsGrid.setItems(events);
            content.add(eventsTitle, eventsGrid);
        }

        dialog.add(content);
        Button closeBtn = new Button("Close", e -> dialog.close());
        closeBtn.addClassName("st-btn-secondary");
        dialog.getFooter().add(closeBtn);
        dialog.open();
    }

    private Component createDetailItem(String label, String value) {
        Div box = new Div();
        Span l = new Span(label);
        l.getStyle().set("font-size", "11px").set("color", "var(--st-text-muted)").set("display", "block");
        Span v = new Span(value);
        v.getStyle().set("font-size", "13px").set("font-weight", "600").set("color", "var(--st-text-primary)");
        box.add(l, v);
        return box;
    }

    private Div createStatCard(String label, Span valueSpan, String subtitle) {
        Div card = new Div();
        card.addClassName("st-stat-card");
        Span labelSpan = new Span(label);
        labelSpan.addClassName("st-stat-label");
        Span subSpan = new Span(subtitle);
        subSpan.addClassName("st-stat-sub");
        card.add(labelSpan, valueSpan, subSpan);
        return card;
    }
}
