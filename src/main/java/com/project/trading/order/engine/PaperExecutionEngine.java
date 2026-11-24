package com.project.trading.order.engine;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.dao.HoldingDAO;
import com.project.trading.dao.TradeDAO;
import com.project.trading.dao.UserDAO;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.MarketDataListener;
import com.project.trading.marketdata.service.MarketDataConnectionManager;
import com.project.trading.model.Holding;
import com.project.trading.model.Trade;
import com.project.trading.model.TradeType;
import com.project.trading.model.User;
import com.project.trading.order.dao.FillDAO;
import com.project.trading.order.dao.OrderDAO;
import com.project.trading.order.dao.ReservationDAO;
import com.project.trading.order.model.BracketRole;
import com.project.trading.order.model.Fill;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.ReservedFund;
import com.project.trading.order.model.ReservedHolding;
import com.project.trading.order.service.SimulationCostModel;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Separate, institutional simulated order execution engine.
 *
 * Fully decoupled from market data providers and controllers.
 * Consumes normalized MarketQuote events and executes eligible paper orders
 * with row-level locks, ACID cash/holdings accounting, OCO bracket exits,
 * and realistic spread/slippage models.
 */
public class PaperExecutionEngine implements MarketDataListener {

    private final OrderDAO orderDAO;
    private final FillDAO fillDAO;
    private final ReservationDAO reservationDAO;
    private final UserDAO userDAO;
    private final HoldingDAO holdingDAO;
    private final TradeDAO tradeDAO;
    private final SimulationCostModel costModel;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private static final PaperExecutionEngine INSTANCE = new PaperExecutionEngine();

    public static PaperExecutionEngine getInstance() {
        return INSTANCE;
    }

    public PaperExecutionEngine() {
        this(new OrderDAO(), new FillDAO(), new ReservationDAO(), new UserDAO(), new HoldingDAO(), new TradeDAO(), new SimulationCostModel());
    }

    public PaperExecutionEngine(OrderDAO orderDAO, FillDAO fillDAO, ReservationDAO reservationDAO,
                                UserDAO userDAO, HoldingDAO holdingDAO, TradeDAO tradeDAO,
                                SimulationCostModel costModel) {
        this.orderDAO = orderDAO;
        this.fillDAO = fillDAO;
        this.reservationDAO = reservationDAO;
        this.userDAO = userDAO;
        this.holdingDAO = holdingDAO;
        this.tradeDAO = tradeDAO;
        this.costModel = costModel;
    }

    public synchronized void start() {
        if (running.compareAndSet(false, true)) {
            MarketDataConnectionManager.getInstance().addGlobalListener(this);
        }
    }

    public synchronized void stop() {
        if (running.compareAndSet(true, false)) {
            MarketDataConnectionManager.getInstance().removeGlobalListener(this);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void onQuote(MarketQuote quote) {
        if (quote == null || quote.ltp() == null || quote.isStale()) {
            return;
        }
        evaluateEligibleOrders(quote);
    }

    /**
     * Evaluates all open and trigger-pending orders matching the current market quote.
     */
    public void evaluateEligibleOrders(MarketQuote quote) {
        long stockId = quote.stockId();
        try (Connection c = DBConnection.getConnection()) {
            List<Order> activeOrders = orderDAO.findActiveByStock(c, stockId);
            if (activeOrders.isEmpty()) {
                return;
            }

            for (Order order : activeOrders) {
                try {
                    evaluateSingleOrder(quote, order.getId());
                } catch (Throwable t) {
                    System.err.println("[PaperExecutionEngine] Error evaluating order #" + order.getId() + ": " + t.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("[PaperExecutionEngine] Database connection error: " + e.getMessage());
        }
    }

    public void evaluateSingleOrder(MarketQuote quote, long orderId) {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Order order = orderDAO.findByIdForUpdate(c, orderId);
                if (order == null || order.getStatus().isTerminal()) {
                    c.rollback();
                    return;
                }

                BigDecimal ltp = quote.ltp();
                BigDecimal bid = quote.bidPrice() != null ? quote.bidPrice() : ltp;
                BigDecimal ask = quote.askPrice() != null ? quote.askPrice() : ltp;

                // Handle order types
                switch (order.getOrderType()) {
                    case MARKET -> {
                        BigDecimal execPrice = order.getSide() == OrderSide.BUY ? ask : bid;
                        execPrice = costModel.applySlippage(execPrice, order.getSide() == OrderSide.BUY);
                        executeFill(c, order, execPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                    }
                    case LIMIT -> {
                        if (order.getSide() == OrderSide.BUY) {
                            if (ask.compareTo(order.getLimitPrice()) <= 0) {
                                BigDecimal fillPrice = ask.min(order.getLimitPrice());
                                executeFill(c, order, fillPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                            }
                        } else {
                            if (bid.compareTo(order.getLimitPrice()) >= 0) {
                                BigDecimal fillPrice = bid.max(order.getLimitPrice());
                                executeFill(c, order, fillPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                            }
                        }
                    }
                    case STOP_LOSS_MARKET -> {
                        if (order.getStatus() == OrderStatus.TRIGGER_PENDING) {
                            boolean triggered = (order.getSide() == OrderSide.SELL && ltp.compareTo(order.getTriggerPrice()) <= 0)
                                    || (order.getSide() == OrderSide.BUY && ltp.compareTo(order.getTriggerPrice()) >= 0);
                            if (triggered) {
                                orderDAO.recordEvent(c, order.getId(), OrderStatus.TRIGGER_PENDING.name(), OrderStatus.OPEN.name(), "SL-M triggered at " + ltp);
                                BigDecimal fillPrice = order.getSide() == OrderSide.BUY ? ask : bid;
                                fillPrice = costModel.applySlippage(fillPrice, order.getSide() == OrderSide.BUY);
                                executeFill(c, order, fillPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                            }
                        }
                    }
                    case STOP_LOSS_LIMIT -> {
                        if (order.getStatus() == OrderStatus.TRIGGER_PENDING) {
                            boolean triggered = (order.getSide() == OrderSide.SELL && ltp.compareTo(order.getTriggerPrice()) <= 0)
                                    || (order.getSide() == OrderSide.BUY && ltp.compareTo(order.getTriggerPrice()) >= 0);
                            if (triggered) {
                                orderDAO.updateStatus(c, order.getId(), OrderStatus.OPEN, "SL triggered at " + ltp + "; converted to LIMIT order");
                            }
                        } else if (order.getStatus() == OrderStatus.OPEN) {
                            if (order.getSide() == OrderSide.BUY && ask.compareTo(order.getLimitPrice()) <= 0) {
                                BigDecimal fillPrice = ask.min(order.getLimitPrice());
                                executeFill(c, order, fillPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                            } else if (order.getSide() == OrderSide.SELL && bid.compareTo(order.getLimitPrice()) >= 0) {
                                BigDecimal fillPrice = bid.max(order.getLimitPrice());
                                executeFill(c, order, fillPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                            }
                        }
                    }
                    case TARGET -> {
                        if (order.getStatus() == OrderStatus.TRIGGER_PENDING || order.getStatus() == OrderStatus.OPEN) {
                            boolean triggered = (order.getSide() == OrderSide.SELL && bid.compareTo(order.getTargetPrice()) >= 0)
                                    || (order.getSide() == OrderSide.BUY && ask.compareTo(order.getTargetPrice()) <= 0);
                            if (triggered) {
                                BigDecimal fillPrice = order.getSide() == OrderSide.BUY ? ask.min(order.getTargetPrice()) : bid.max(order.getTargetPrice());
                                executeFill(c, order, fillPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                            }
                        }
                    }
                    case BRACKET -> {
                        // Bracket entry behaves as a limit or market order
                        if (order.getSide() == OrderSide.BUY && ask.compareTo(order.getLimitPrice() != null ? order.getLimitPrice() : ask) <= 0) {
                            BigDecimal fillPrice = order.getLimitPrice() != null ? ask.min(order.getLimitPrice()) : ask;
                            executeFill(c, order, fillPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                        } else if (order.getSide() == OrderSide.SELL && bid.compareTo(order.getLimitPrice() != null ? order.getLimitPrice() : bid) >= 0) {
                            BigDecimal fillPrice = order.getLimitPrice() != null ? bid.max(order.getLimitPrice()) : bid;
                            executeFill(c, order, fillPrice, order.getRemainingQuantity(), quote.exchangeTimestamp());
                        }
                    }
                }

                c.commit();
            } catch (Throwable t) {
                c.rollback();
                throw t;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Throwable t) {
            System.err.println("[PaperExecutionEngine] Transaction failure on order #" + orderId + ": " + t.getMessage());
        }
    }

    /**
     * Executes an atomic fill, adjusting cash, holdings, reservations, and linked OCO bracket orders.
     */
    public void executeFill(Connection c, Order order, BigDecimal fillPrice, int fillQty, Instant eventTime) throws SQLException {
        if (fillQty <= 0 || fillPrice.compareTo(BigDecimal.ZERO) <= 0) return;

        User user = userDAO.findByIdForUpdate(c, order.getUserId());
        if (user == null) throw new SQLException("User not found: " + order.getUserId());

        BigDecimal grossAmount = fillPrice.multiply(new BigDecimal(fillQty)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal brokerage = costModel.calculateBrokerage(grossAmount);
        BigDecimal taxes = costModel.calculateTaxesAndCharges(grossAmount);
        BigDecimal totalFees = brokerage.add(taxes).setScale(2, RoundingMode.HALF_UP);

        // Record Fill record
        Fill fill = new Fill();
        fill.setOrderId(order.getId());
        fill.setUserId(order.getUserId());
        fill.setStockId(order.getStockId());
        fill.setFillQuantity(fillQty);
        fill.setFillPrice(fillPrice);
        fill.setTotalAmount(grossAmount);
        fill.setSimulatedBrokerage(brokerage);
        fill.setSimulatedTaxes(taxes);
        fill.setFillTime(eventTime != null ? eventTime : Instant.now());
        fillDAO.insert(c, fill);

        Trade recordedTrade = new Trade();
        recordedTrade.setUserId(user.getId());
        recordedTrade.setStockId(order.getStockId());
        recordedTrade.setQuantity(fillQty);
        recordedTrade.setPrice(fillPrice);
        recordedTrade.setTotalAmount(grossAmount);

        // Handle BUY side accounting
        if (order.getSide() == OrderSide.BUY) {
            BigDecimal totalDebit = grossAmount.add(totalFees);
            userDAO.adjustCash(c, user.getId(), totalDebit.negate());

            // Update holdings
            Holding h = holdingDAO.findForUpdate(c, user.getId(), order.getStockId());
            if (h == null) {
                Holding newHolding = new Holding();
                newHolding.setUserId(user.getId());
                newHolding.setStockId(order.getStockId());
                newHolding.setQuantity(fillQty);
                newHolding.setAvgBuyPrice(fillPrice);
                holdingDAO.insert(c, newHolding);
            } else {
                int newTotalQty = h.getQuantity() + fillQty;
                BigDecimal oldCost = h.getAvgBuyPrice().multiply(new BigDecimal(h.getQuantity()));
                BigDecimal newCost = fillPrice.multiply(new BigDecimal(fillQty));
                BigDecimal newAvg = oldCost.add(newCost).divide(new BigDecimal(newTotalQty), 4, RoundingMode.HALF_UP);
                holdingDAO.update(c, user.getId(), order.getStockId(), newTotalQty, newAvg);
            }

            // Commit/Release funds reservation
            reservationDAO.commitReservedFunds(c, order.getId());

            // Record Trade in trades table
            recordedTrade.setType(TradeType.BUY);
            recordedTrade.setRealizedPnl(BigDecimal.ZERO);
            tradeDAO.insert(c, recordedTrade);

            // If this was a Bracket Entry order, spawn child Target and Stop-Loss exits atomically
            if (order.getOrderType() == OrderType.BRACKET && order.getTargetPrice() != null && order.getStopLossPrice() != null) {
                spawnBracketExitOrders(c, order, fillQty, fillPrice);
            }

        } else {
            // Handle SELL side accounting
            BigDecimal netCredit = grossAmount.subtract(totalFees);
            userDAO.adjustCash(c, user.getId(), netCredit);

            Holding h = holdingDAO.findForUpdate(c, user.getId(), order.getStockId());
            BigDecimal avgBuyPrice = (h != null && h.getAvgBuyPrice() != null) ? h.getAvgBuyPrice() : fillPrice;
            BigDecimal realizedPnl = fillPrice.subtract(avgBuyPrice).multiply(new BigDecimal(fillQty)).subtract(totalFees).setScale(2, RoundingMode.HALF_UP);

            if (h != null) {
                int newQty = h.getQuantity() - fillQty;
                if (newQty <= 0) {
                    holdingDAO.delete(c, user.getId(), order.getStockId());
                } else {
                    holdingDAO.update(c, user.getId(), order.getStockId(), newQty, h.getAvgBuyPrice());
                }
            }

            // Commit/Release holdings reservation
            reservationDAO.commitReservedHoldings(c, order.getId());

            // Record Trade in trades table
            recordedTrade.setType(TradeType.SELL);
            recordedTrade.setRealizedPnl(realizedPnl);
            tradeDAO.insert(c, recordedTrade);

            // If this was a bracket exit order (TARGET_EXIT or STOP_LOSS_EXIT), atomically cancel sibling exit (OCO)!
            if (order.getParentOrderId() != null && order.getBracketRole() != null) {
                handleOcoSiblingCancellation(c, order);
            }
        }

        // Update Order Fill Progress
        int newFilled = order.getFilledQuantity() + fillQty;
        int newRemaining = order.getQuantity() - newFilled;
        OrderStatus nextStatus = newRemaining == 0 ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED;
        orderDAO.updateFillProgress(c, order.getId(), newFilled, newRemaining, fillPrice, nextStatus);

        // Broadcast trade update to Vaadin UI push sessions
        MarketDataBroadcaster.broadcastTrade(recordedTrade);
    }

    private void spawnBracketExitOrders(Connection c, Order parent, int fillQty, BigDecimal entryPrice) throws SQLException {
        // Reserve holdings for exit orders
        reservationDAO.reserveHoldings(c, parent.getId(), parent.getUserId(), parent.getStockId(), fillQty);

        // 1. Target Exit Order
        Order targetOrder = new Order();
        targetOrder.setUserId(parent.getUserId());
        targetOrder.setStockId(parent.getStockId());
        targetOrder.setSide(OrderSide.SELL);
        targetOrder.setOrderType(OrderType.TARGET);
        targetOrder.setQuantity(fillQty);
        targetOrder.setTargetPrice(parent.getTargetPrice());
        targetOrder.setLimitPrice(parent.getTargetPrice());
        targetOrder.setStatus(OrderStatus.TRIGGER_PENDING);
        targetOrder.setParentOrderId(parent.getId());
        targetOrder.setBracketRole(BracketRole.TARGET_EXIT);
        long targetId = orderDAO.insert(c, targetOrder);
        reservationDAO.reserveHoldings(c, targetId, parent.getUserId(), parent.getStockId(), fillQty);

        // 2. Stop-Loss Exit Order
        Order slOrder = new Order();
        slOrder.setUserId(parent.getUserId());
        slOrder.setStockId(parent.getStockId());
        slOrder.setSide(OrderSide.SELL);
        slOrder.setOrderType(OrderType.STOP_LOSS_MARKET);
        slOrder.setQuantity(fillQty);
        slOrder.setTriggerPrice(parent.getStopLossPrice());
        slOrder.setStatus(OrderStatus.TRIGGER_PENDING);
        slOrder.setParentOrderId(parent.getId());
        slOrder.setBracketRole(BracketRole.STOP_LOSS_EXIT);
        long slId = orderDAO.insert(c, slOrder);
        reservationDAO.reserveHoldings(c, slId, parent.getUserId(), parent.getStockId(), fillQty);
    }

    private void handleOcoSiblingCancellation(Connection c, Order filledChildOrder) throws SQLException {
        List<Order> siblings = orderDAO.findChildOrders(c, filledChildOrder.getParentOrderId());
        for (Order sibling : siblings) {
            if (!sibling.getId().equals(filledChildOrder.getId()) && sibling.getStatus().isActive()) {
                orderDAO.updateStatus(c, sibling.getId(), OrderStatus.CANCELLED, "OCO: Sibling bracket exit #" + filledChildOrder.getId() + " filled");
                reservationDAO.releaseReservedHoldings(c, sibling.getId());
            }
        }
    }
}
