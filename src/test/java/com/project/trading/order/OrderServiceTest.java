package com.project.trading.order;

import com.project.trading.DBCreator;
import com.project.trading.exception.InsufficientBalanceException;
import com.project.trading.exception.InsufficientHoldingsException;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.order.dao.OrderDAO;
import com.project.trading.order.dao.ReservationDAO;
import com.project.trading.order.model.BracketRole;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.OrderValidity;
import com.project.trading.order.service.OrderService;
import com.project.trading.service.PortfolioService;
import com.project.trading.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private OrderService orderService;
    private OrderDAO orderDAO;
    private ReservationDAO reservationDAO;
    private PortfolioService portfolioService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        orderService = new OrderService();
        orderDAO = new OrderDAO();
        reservationDAO = new ReservationDAO();
        portfolioService = new PortfolioService();
    }

    @Test
    public void testPlaceLimitBuy_ReservesFundsAndProtectsAvailableCash() throws Exception {
        // User 2 has 100,000.00 cash
        Order order = new Order();
        order.setUserId(2L);
        order.setStockId(1L); // RELIANCE
        order.setSide(OrderSide.BUY);
        order.setOrderType(OrderType.LIMIT);
        order.setQuantity(10);
        order.setRemainingQuantity(10);
        order.setLimitPrice(new BigDecimal("2800.00"));
        order.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(order);
        assertNotNull(placed);
        assertEquals(OrderStatus.OPEN, placed.getStatus());
        assertEquals(10, placed.getRemainingQuantity());

        try (Connection c = DBConnection.getConnection()) {
            BigDecimal reserved = reservationDAO.getTotalReservedFunds(c, 2L);
            assertEquals(new BigDecimal("28000.00"), reserved, "Reserved funds must equal limit_price * quantity");

            var summary = portfolioService.getSummaryForUser(2L);
            assertEquals(new BigDecimal("100000.00"), summary.cash, "Total cash balance must not change on reservation");
            assertEquals(new BigDecimal("28000.00"), summary.reservedCash);
            assertEquals(new BigDecimal("72000.00"), summary.availableCash, "Available cash must be total cash - reserved funds");
        }
    }

    @Test
    public void testPlaceLimitBuy_DoubleSpendingPrevention() throws Exception {
        // User 3 has 21,600.00 cash
        Order order1 = new Order();
        order1.setUserId(3L);
        order1.setStockId(3L); // INFY
        order1.setSide(OrderSide.BUY);
        order1.setOrderType(OrderType.LIMIT);
        order1.setQuantity(10);
        order1.setRemainingQuantity(10);
        order1.setLimitPrice(new BigDecimal("1400.00")); // Total ₹14,000.00
        order1.setValidity(OrderValidity.DAY);

        orderService.placeOrder(order1);

        // Remaining available cash = 21,600 - 14,000 = 7,600
        // Attempt to place second buy order for ₹10,000 (10 shares @ 1000)
        Order order2 = new Order();
        order2.setUserId(3L);
        order2.setStockId(3L);
        order2.setSide(OrderSide.BUY);
        order2.setOrderType(OrderType.LIMIT);
        order2.setQuantity(10);
        order2.setRemainingQuantity(10);
        order2.setLimitPrice(new BigDecimal("1000.00"));
        order2.setValidity(OrderValidity.DAY);

        assertThrows(InsufficientBalanceException.class, () -> orderService.placeOrder(order2),
                "Placing order exceeding available cash after active reservations must throw InsufficientBalanceException");
    }

    @Test
    public void testPlaceLimitSell_ReservesHoldingsAndProtectsAvailableShares() throws Exception {
        // User 3 holds 30 shares of Stock 7 (ITC)
        Order order = new Order();
        order.setUserId(3L);
        order.setStockId(7L);
        order.setSide(OrderSide.SELL);
        order.setOrderType(OrderType.LIMIT);
        order.setQuantity(20);
        order.setRemainingQuantity(20);
        order.setLimitPrice(new BigDecimal("450.00"));
        order.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(order);
        assertNotNull(placed);
        assertEquals(OrderStatus.OPEN, placed.getStatus());

        try (Connection c = DBConnection.getConnection()) {
            int reservedHoldings = reservationDAO.getTotalReservedHoldings(c, 3L, 7L);
            assertEquals(20, reservedHoldings, "Reserved holdings must equal sell order quantity");

            var summary = portfolioService.getSummaryForUser(3L);
            var itc = summary.rows.stream().filter(h -> h.stockId == 7L).findFirst().orElseThrow();
            assertEquals(30, itc.quantity);
            assertEquals(20, itc.reservedQuantity);
            assertEquals(10, itc.availableQuantity, "Available quantity must be total - reserved");
        }
    }

    @Test
    public void testPlaceLimitSell_OversellingPrevention() throws Exception {
        // User 3 holds 30 shares of Stock 7. Place sell for 20 shares.
        Order order1 = new Order();
        order1.setUserId(3L);
        order1.setStockId(7L);
        order1.setSide(OrderSide.SELL);
        order1.setOrderType(OrderType.LIMIT);
        order1.setQuantity(20);
        order1.setRemainingQuantity(20);
        order1.setLimitPrice(new BigDecimal("450.00"));
        order1.setValidity(OrderValidity.DAY);
        orderService.placeOrder(order1);

        // Attempt to place second sell for 15 shares (only 10 available)
        Order order2 = new Order();
        order2.setUserId(3L);
        order2.setStockId(7L);
        order2.setSide(OrderSide.SELL);
        order2.setOrderType(OrderType.LIMIT);
        order2.setQuantity(15);
        order2.setRemainingQuantity(15);
        order2.setLimitPrice(new BigDecimal("460.00"));
        order2.setValidity(OrderValidity.DAY);

        assertThrows(InsufficientHoldingsException.class, () -> orderService.placeOrder(order2),
                "Overselling beyond available unreserved shares must throw InsufficientHoldingsException");
    }

    @Test
    public void testCancelOrder_ReleasesReservationAtomically() throws Exception {
        // User 2: Place Limit Buy of 10 shares @ 2800 = 28,000 reserved
        Order order = new Order();
        order.setUserId(2L);
        order.setStockId(1L);
        order.setSide(OrderSide.BUY);
        order.setOrderType(OrderType.LIMIT);
        order.setQuantity(10);
        order.setRemainingQuantity(10);
        order.setLimitPrice(new BigDecimal("2800.00"));
        order.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(order);

        // Cancel order
        orderService.cancelOrder(placed.getId(), 2L);

        try (Connection c = DBConnection.getConnection()) {
            Order cancelled = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());

            BigDecimal reserved = reservationDAO.getTotalReservedFunds(c, 2L);
            assertEquals(0, BigDecimal.ZERO.compareTo(reserved), "Reserved funds must be released to 0 on cancellation");

            var summary = portfolioService.getSummaryForUser(2L);
            assertEquals(new BigDecimal("100000.00"), summary.availableCash, "Available cash must be restored to full balance");
        }
    }

    @Test
    public void testRiskLimits_MaxOrderValueExceeded() {
        // Attempt order of 2,000 shares of TCS @ ₹3900 = ₹78,00,000 (> ₹50L max order value)
        Order order = new Order();
        order.setUserId(2L);
        order.setStockId(2L);
        order.setSide(OrderSide.BUY);
        order.setOrderType(OrderType.LIMIT);
        order.setQuantity(2000);
        order.setRemainingQuantity(2000);
        order.setLimitPrice(new BigDecimal("3900.00"));
        order.setValidity(OrderValidity.DAY);

        InvalidOrderException ex = assertThrows(InvalidOrderException.class, () -> orderService.placeOrder(order));
        assertTrue(ex.getMessage().contains("exceeds maximum allowed"));
    }

    @Test
    public void testPlaceBracketOrder_CreatesParentOrderWithTargets() throws Exception {
        Order bracket = new Order();
        bracket.setUserId(2L);
        bracket.setStockId(1L);
        bracket.setSide(OrderSide.BUY);
        bracket.setOrderType(OrderType.BRACKET);
        bracket.setQuantity(5);
        bracket.setRemainingQuantity(5);
        bracket.setLimitPrice(new BigDecimal("2800.00"));
        bracket.setTargetPrice(new BigDecimal("2900.00"));
        bracket.setStopLossPrice(new BigDecimal("2750.00"));
        bracket.setBracketRole(BracketRole.ENTRY);
        bracket.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(bracket);
        assertNotNull(placed);
        assertEquals(OrderType.BRACKET, placed.getOrderType());
        assertEquals(new BigDecimal("2900.00"), placed.getTargetPrice());
        assertEquals(new BigDecimal("2750.00"), placed.getStopLossPrice());
        assertEquals(BracketRole.ENTRY, placed.getBracketRole());
    }
}
