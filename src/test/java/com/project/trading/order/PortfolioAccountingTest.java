package com.project.trading.order;

import com.project.trading.DBCreator;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.OrderValidity;
import com.project.trading.order.service.OrderService;
import com.project.trading.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PortfolioAccountingTest {

    private OrderService orderService;
    private PortfolioService portfolioService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        orderService = new OrderService();
        portfolioService = new PortfolioService();
    }

    @Test
    public void testPortfolioAccountingIdentity_InitialState() {
        var summary = portfolioService.getSummaryForUser(3L);

        // Core Accounting Identity: Net Worth (Total Equity) == Cash Balance + Holdings Current Value
        BigDecimal expectedEquity = summary.cash.add(summary.totalCurrentValue);
        assertEquals(0, expectedEquity.compareTo(summary.netWorth),
                "Accounting Invariant: Net Worth must equal Cash Balance + Holdings Value");

        // Available Cash Identity: Available Cash == Cash Balance - Reserved Cash
        BigDecimal expectedAvailable = summary.cash.subtract(summary.reservedCash);
        assertEquals(0, expectedAvailable.compareTo(summary.availableCash),
                "Cash Availability Invariant: Available Cash must equal Cash Balance - Reserved Cash");

        assertEquals(0, BigDecimal.ZERO.compareTo(summary.reservedCash));
        assertEquals(new BigDecimal("21600.00"), summary.availableCash);
    }

    @Test
    public void testPortfolioAccountingIdentity_WithActiveBuyReservation() throws Exception {
        // Priya has 21,600 cash. Place Limit Buy for ₹14,000.00
        Order order = new Order();
        order.setUserId(3L);
        order.setStockId(3L); // INFY
        order.setSide(OrderSide.BUY);
        order.setOrderType(OrderType.LIMIT);
        order.setQuantity(10);
        order.setRemainingQuantity(10);
        order.setLimitPrice(new BigDecimal("1400.00"));
        order.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(order);
        assertNotNull(placed);

        var summary = portfolioService.getSummaryForUser(3L);

        // Cash balance remains intact in account
        assertEquals(new BigDecimal("21600.00"), summary.cash);
        assertEquals(new BigDecimal("14000.00"), summary.reservedCash);
        assertEquals(new BigDecimal("7600.00"), summary.availableCash);

        // Invariant holds
        BigDecimal expectedEquity = summary.cash.add(summary.totalCurrentValue);
        assertEquals(0, expectedEquity.compareTo(summary.netWorth));
        BigDecimal expectedAvailable = summary.cash.subtract(summary.reservedCash);
        assertEquals(0, expectedAvailable.compareTo(summary.availableCash));

        // Release on cancel
        orderService.cancelOrder(placed.getId(), 3L);

        var summaryAfterCancel = portfolioService.getSummaryForUser(3L);
        assertEquals(new BigDecimal("21600.00"), summaryAfterCancel.cash);
        assertEquals(0, BigDecimal.ZERO.compareTo(summaryAfterCancel.reservedCash));
        assertEquals(new BigDecimal("21600.00"), summaryAfterCancel.availableCash);
    }

    @Test
    public void testPortfolioHoldingsReservationIdentity() throws Exception {
        // Priya has 30 shares of ITC (Stock 7)
        var initialSummary = portfolioService.getSummaryForUser(3L);
        var itcInitial = initialSummary.rows.stream().filter(h -> h.stockId == 7L).findFirst().orElseThrow();
        assertEquals(30, itcInitial.quantity);
        assertEquals(0, itcInitial.reservedQuantity);
        assertEquals(30, itcInitial.availableQuantity);

        // Place Sell order for 15 shares
        Order order = new Order();
        order.setUserId(3L);
        order.setStockId(7L);
        order.setSide(OrderSide.SELL);
        order.setOrderType(OrderType.LIMIT);
        order.setQuantity(15);
        order.setRemainingQuantity(15);
        order.setLimitPrice(new BigDecimal("450.00"));
        order.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(order);

        var reservedSummary = portfolioService.getSummaryForUser(3L);
        var itcReserved = reservedSummary.rows.stream().filter(h -> h.stockId == 7L).findFirst().orElseThrow();
        assertEquals(30, itcReserved.quantity, "Total holdings must remain unchanged while order is open");
        assertEquals(15, itcReserved.reservedQuantity);
        assertEquals(15, itcReserved.availableQuantity);

        // Identity: Total Quantity == Available Quantity + Reserved Quantity
        assertEquals(itcReserved.quantity, itcReserved.availableQuantity + itcReserved.reservedQuantity);

        // Cancel order
        orderService.cancelOrder(placed.getId(), 3L);

        var cancelledSummary = portfolioService.getSummaryForUser(3L);
        var itcCancelled = cancelledSummary.rows.stream().filter(h -> h.stockId == 7L).findFirst().orElseThrow();
        assertEquals(30, itcCancelled.quantity);
        assertEquals(0, itcCancelled.reservedQuantity);
        assertEquals(30, itcCancelled.availableQuantity);
    }
}
