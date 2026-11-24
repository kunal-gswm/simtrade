package com.project.trading.order;

import com.project.trading.DBCreator;
import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.order.dao.FillDAO;
import com.project.trading.order.dao.OrderDAO;
import com.project.trading.order.engine.PaperExecutionEngine;
import com.project.trading.order.model.BracketRole;
import com.project.trading.order.model.Fill;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.OrderValidity;
import com.project.trading.order.service.OrderService;
import com.project.trading.order.service.SimulationCostModel;
import com.project.trading.service.PortfolioService;
import com.project.trading.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PaperExecutionEngineTest {

    private PaperExecutionEngine engine;
    private OrderService orderService;
    private OrderDAO orderDAO;
    private FillDAO fillDAO;
    private PortfolioService portfolioService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        engine = new PaperExecutionEngine();
        orderService = new OrderService();
        orderDAO = new OrderDAO();
        fillDAO = new FillDAO();
        portfolioService = new PortfolioService();
    }

    private MarketQuote createQuote(long stockId, String symbol, BigDecimal ltp, BigDecimal bid, BigDecimal ask) {
        return new MarketQuote(
                stockId,
                symbol,
                "NSE",
                "NSE_EQ",
                MarketDataSource.LIVE_PROVIDER,
                Instant.now(),
                Instant.now(),
                ltp,
                ltp,
                ltp, ltp, ltp,
                100000L,
                bid, 1000,
                ask, 1000,
                false,
                "VALID"
        );
    }

    @Test
    public void testLimitBuyMatching_RespectsLimitPrice() throws Exception {
        // User 2 places Limit Buy of Stock 1 (RELIANCE) @ 2800.00, qty 10
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
        assertEquals(OrderStatus.OPEN, placed.getStatus());

        // Quote 1: ask = 2820.00 (> limit) -> No fill
        MarketQuote quoteHigh = createQuote(1L, "RELIANCE", new BigDecimal("2820.00"), new BigDecimal("2819.90"), new BigDecimal("2820.00"));
        engine.evaluateEligibleOrders(quoteHigh);

        try (Connection c = DBConnection.getConnection()) {
            Order check1 = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.OPEN, check1.getStatus(), "Order must remain OPEN when market ask exceeds limit price");
        }

        // Quote 2: ask = 2795.00 (<= limit) -> Fills!
        MarketQuote quoteMatch = createQuote(1L, "RELIANCE", new BigDecimal("2795.00"), new BigDecimal("2794.90"), new BigDecimal("2795.00"));
        engine.evaluateEligibleOrders(quoteMatch);

        try (Connection c = DBConnection.getConnection()) {
            Order check2 = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.FILLED, check2.getStatus(), "Order must be FILLED when market ask matches limit");
            assertEquals(10, check2.getFilledQuantity());
            assertEquals(0, check2.getRemainingQuantity());
            assertEquals(new BigDecimal("2795.00"), check2.getAverageFillPrice());

            List<Fill> fills = fillDAO.findByOrder(c, placed.getId());
            assertEquals(1, fills.size());
            Fill fill = fills.get(0);
            assertEquals(10, fill.getFillQuantity());
            assertEquals(new BigDecimal("2795.00"), fill.getFillPrice());
            assertTrue(fill.getSimulatedBrokerage().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(fill.getSimulatedTaxes().compareTo(BigDecimal.ZERO) > 0);

            // Holdings verified
            var summary = portfolioService.getSummaryForUser(2L);
            var reliance = summary.rows.stream().filter(h -> h.stockId == 1L).findFirst().orElseThrow();
            assertEquals(10, reliance.quantity);
        }
    }

    @Test
    public void testLimitSellMatching_RespectsLimitPrice() throws Exception {
        // User 3 holds 30 shares of Stock 7 (ITC). Place Limit Sell @ 440.00, qty 10
        Order order = new Order();
        order.setUserId(3L);
        order.setStockId(7L);
        order.setSide(OrderSide.SELL);
        order.setOrderType(OrderType.LIMIT);
        order.setQuantity(10);
        order.setRemainingQuantity(10);
        order.setLimitPrice(new BigDecimal("440.00"));
        order.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(order);
        assertEquals(OrderStatus.OPEN, placed.getStatus());

        // Quote 1: bid = 435.00 (< limit) -> No fill
        MarketQuote quoteLow = createQuote(7L, "ITC", new BigDecimal("435.00"), new BigDecimal("435.00"), new BigDecimal("435.10"));
        engine.evaluateEligibleOrders(quoteLow);

        try (Connection c = DBConnection.getConnection()) {
            Order check1 = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.OPEN, check1.getStatus());
        }

        // Quote 2: bid = 442.00 (>= limit) -> Fills!
        MarketQuote quoteMatch = createQuote(7L, "ITC", new BigDecimal("442.00"), new BigDecimal("442.00"), new BigDecimal("442.10"));
        engine.evaluateEligibleOrders(quoteMatch);

        try (Connection c = DBConnection.getConnection()) {
            Order check2 = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.FILLED, check2.getStatus());
            assertEquals(new BigDecimal("442.00"), check2.getAverageFillPrice());

            var summary = portfolioService.getSummaryForUser(3L);
            var itc = summary.rows.stream().filter(h -> h.stockId == 7L).findFirst().orElseThrow();
            assertEquals(20, itc.quantity, "Holding quantity must decrease by filled quantity");
        }
    }

    @Test
    public void testStopLossMarket_TriggersAndFills() throws Exception {
        // User 3 holds 20 shares of Stock 3 (INFY). Place SL-M sell order with trigger @ 1480.00
        Order order = new Order();
        order.setUserId(3L);
        order.setStockId(3L);
        order.setSide(OrderSide.SELL);
        order.setOrderType(OrderType.STOP_LOSS_MARKET);
        order.setQuantity(10);
        order.setRemainingQuantity(10);
        order.setTriggerPrice(new BigDecimal("1480.00"));
        order.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(order);
        assertEquals(OrderStatus.TRIGGER_PENDING, placed.getStatus());

        // Market above trigger -> No trigger
        MarketQuote quoteAbove = createQuote(3L, "INFY", new BigDecimal("1490.00"), new BigDecimal("1489.90"), new BigDecimal("1490.00"));
        engine.evaluateEligibleOrders(quoteAbove);

        try (Connection c = DBConnection.getConnection()) {
            Order check1 = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.TRIGGER_PENDING, check1.getStatus());
        }

        // Market breaches trigger -> Triggers and executes!
        MarketQuote quoteTriggered = createQuote(3L, "INFY", new BigDecimal("1475.00"), new BigDecimal("1475.00"), new BigDecimal("1475.10"));
        engine.evaluateEligibleOrders(quoteTriggered);

        try (Connection c = DBConnection.getConnection()) {
            Order check2 = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.FILLED, check2.getStatus(), "SL-M order must be FILLED after trigger price breached");
            assertTrue(check2.getAverageFillPrice().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Test
    public void testBracketOrder_SpawnsChildrenAndOcoCancellation() throws Exception {
        // User 2 places Bracket Buy order for Stock 1 (RELIANCE): limit 2800.00, target 2900.00, stopLoss 2750.00
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
        assertEquals(OrderStatus.OPEN, placed.getStatus());

        // 1. Entry fills at 2795.00
        MarketQuote entryQuote = createQuote(1L, "RELIANCE", new BigDecimal("2795.00"), new BigDecimal("2794.90"), new BigDecimal("2795.00"));
        engine.evaluateEligibleOrders(entryQuote);

        long targetOrderId;
        long slOrderId;
        try (Connection c = DBConnection.getConnection()) {
            Order entryOrder = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.FILLED, entryOrder.getStatus());

            List<Order> children = orderDAO.findChildOrders(c, placed.getId());
            assertEquals(2, children.size(), "Entry fill must spawn 2 child exit orders");

            Order targetOrder = children.stream().filter(o -> o.getBracketRole() == BracketRole.TARGET_EXIT).findFirst().orElseThrow();
            Order slOrder = children.stream().filter(o -> o.getBracketRole() == BracketRole.STOP_LOSS_EXIT).findFirst().orElseThrow();

            targetOrderId = targetOrder.getId();
            slOrderId = slOrder.getId();

            assertEquals(new BigDecimal("2900.00"), targetOrder.getTargetPrice());
            assertEquals(new BigDecimal("2750.00"), slOrder.getTriggerPrice());
            assertEquals(OrderStatus.TRIGGER_PENDING, targetOrder.getStatus());
            assertEquals(OrderStatus.TRIGGER_PENDING, slOrder.getStatus());
        }

        // 2. Market hits target price (2905.00 >= 2900.00)
        MarketQuote targetQuote = createQuote(1L, "RELIANCE", new BigDecimal("2905.00"), new BigDecimal("2905.00"), new BigDecimal("2905.10"));
        engine.evaluateEligibleOrders(targetQuote);

        // 3. Verify Target is FILLED and SL is atomically CANCELLED (OCO)
        try (Connection c = DBConnection.getConnection()) {
            Order targetCheck = orderDAO.findById(c, targetOrderId);
            Order slCheck = orderDAO.findById(c, slOrderId);

            assertEquals(OrderStatus.FILLED, targetCheck.getStatus(), "Target exit order must be FILLED");
            assertEquals(OrderStatus.CANCELLED, slCheck.getStatus(), "Stop-loss sibling order must be CANCELLED (OCO)");
        }
    }

    @Test
    public void testSimulationCostModel_SlippageAndBrokerageCalculations() {
        SimulationCostModel cost = new SimulationCostModel();

        // Turnover ₹100,000: 0.03% = ₹30 -> capped at ₹20 max
        BigDecimal turnover1 = new BigDecimal("100000.00");
        BigDecimal brokerage1 = cost.calculateBrokerage(turnover1);
        assertEquals(new BigDecimal("20.00"), brokerage1, "Brokerage must be capped at ₹20 per trade");

        // Small turnover ₹10,000: 0.03% = ₹3.00 (< ₹20 cap)
        BigDecimal turnover2 = new BigDecimal("10000.00");
        BigDecimal brokerage2 = cost.calculateBrokerage(turnover2);
        assertEquals(new BigDecimal("3.00"), brokerage2);

        // Taxes on ₹100,000 turnover
        BigDecimal taxes = cost.calculateTaxesAndCharges(turnover1);
        assertTrue(taxes.compareTo(BigDecimal.ZERO) > 0);

        // Slippage: 2 bps (0.02%)
        // Buy: ₹1000.00 -> 1000 * 1.0002 = 1000.20
        BigDecimal buyPrice = cost.applySlippage(new BigDecimal("1000.00"), true);
        assertEquals(new BigDecimal("1000.20"), buyPrice);

        // Sell: ₹1000.00 -> 1000 * 0.9998 = 999.80
        BigDecimal sellPrice = cost.applySlippage(new BigDecimal("1000.00"), false);
        assertEquals(new BigDecimal("999.80"), sellPrice);
    }
}
