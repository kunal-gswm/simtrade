package com.project.trading.order.service;

import com.project.trading.dao.HoldingDAO;
import com.project.trading.dao.StockDAO;
import com.project.trading.dao.UserDAO;
import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.exception.InsufficientBalanceException;
import com.project.trading.exception.InsufficientHoldingsException;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.service.MarketDataConnectionManager;
import com.project.trading.model.Holding;
import com.project.trading.model.Stock;
import com.project.trading.model.User;
import com.project.trading.order.dao.FillDAO;
import com.project.trading.order.dao.OrderDAO;
import com.project.trading.order.dao.ReservationDAO;
import com.project.trading.order.engine.PaperExecutionEngine;
import com.project.trading.order.model.Fill;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderEvent;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.model.OrderType;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO;
    private final ReservationDAO reservationDAO;
    private final FillDAO fillDAO;
    private final UserDAO userDAO;
    private final HoldingDAO holdingDAO;
    private final StockDAO stockDAO;
    private final PaperExecutionEngine executionEngine;

    // Institutional Risk Limit Parameters
    public static final BigDecimal MAX_ORDER_VALUE = new BigDecimal("5000000.00"); // ₹50 Lakhs
    public static final int MAX_ORDER_QUANTITY = 10000;
    public static final int MAX_OPEN_ORDERS_PER_USER = 50;

    public OrderService() {
        this(new OrderDAO(), new ReservationDAO(), new FillDAO(), new UserDAO(), new HoldingDAO(), new StockDAO(), PaperExecutionEngine.getInstance());
    }

    public OrderService(OrderDAO orderDAO, ReservationDAO reservationDAO, FillDAO fillDAO,
                        UserDAO userDAO, HoldingDAO holdingDAO, StockDAO stockDAO,
                        PaperExecutionEngine executionEngine) {
        this.orderDAO = orderDAO;
        this.reservationDAO = reservationDAO;
        this.fillDAO = fillDAO;
        this.userDAO = userDAO;
        this.holdingDAO = holdingDAO;
        this.stockDAO = stockDAO;
        this.executionEngine = executionEngine;
    }

    /**
     * Places a new paper order with strict validation, risk controls, and transaction-safe reservations.
     */
    public Order placeOrder(Order order) throws AppException {
        validateOrder(order);

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1. Verify User and Stock
                User user = userDAO.findByIdForUpdate(c, order.getUserId());
                if (user == null) throw new InvalidOrderException("User not found");

                Stock stock = stockDAO.findById(c, order.getStockId());
                if (stock == null || !stock.isActive()) throw new InvalidOrderException("Stock not available for trading");

                // 2. Enforce Open Orders Limit
                List<Order> activeOrders = orderDAO.findByUser(c, user.getId(), null, 100);
                long openCount = activeOrders.stream().filter(o -> o.getStatus().isActive()).count();
                if (openCount >= MAX_OPEN_ORDERS_PER_USER) {
                    throw new InvalidOrderException("Maximum open orders limit (" + MAX_OPEN_ORDERS_PER_USER + ") reached");
                }

                // 3. Compute Estimated Price and Turnover
                BigDecimal refPrice = resolveReferencePrice(order, stock);
                BigDecimal estimatedTurnover = refPrice.multiply(new BigDecimal(order.getQuantity())).setScale(2, RoundingMode.HALF_UP);

                if (estimatedTurnover.compareTo(MAX_ORDER_VALUE) > 0) {
                    throw new InvalidOrderException("Order value ₹" + estimatedTurnover + " exceeds maximum allowed ₹" + MAX_ORDER_VALUE);
                }

                // Enforce provider authentication, quote availability, and freshness for MARKET orders
                if (order.getOrderType() == OrderType.MARKET) {
                    MarketQuote latest = MarketDataConnectionManager.getInstance().getLatestQuote(order.getStockId());
                    if (latest == null) {
                        throw new InvalidOrderException("Market order rejected: Live market quotes currently unavailable for " + stock.getSymbol());
                    }
                    com.project.trading.marketdata.model.MarketDataStatus feedStatus = MarketDataConnectionManager.getInstance().getStatus();
                    if (feedStatus == com.project.trading.marketdata.model.MarketDataStatus.STALE || latest.isStale() || MarketDataConnectionManager.getInstance().isStale(order.getStockId())) {
                        throw new InvalidOrderException("Market order rejected: Market data feed is stale (>15s since last tick) for " + stock.getSymbol());
                    }
                    if (!feedStatus.isOrderSubmissionAllowed()) {
                        throw new InvalidOrderException("Market order rejected: Market data feed is not live (status: " + feedStatus.getLabel() + ")");
                    }
                }

                // 4. Handle Reservations
                if (order.getSide() == OrderSide.BUY) {
                    BigDecimal existingReserved = reservationDAO.getTotalReservedFunds(c, user.getId());
                    BigDecimal availableCash = user.getCashBalance().subtract(existingReserved).setScale(2, RoundingMode.HALF_UP);

                    // Add 1% safety buffer for MARKET orders to protect against adverse slippage
                    BigDecimal bufferMultiplier = order.getOrderType() == OrderType.MARKET ? new BigDecimal("1.01") : BigDecimal.ONE;
                    BigDecimal requiredReservation = estimatedTurnover.multiply(bufferMultiplier).setScale(2, RoundingMode.HALF_UP);

                    if (availableCash.compareTo(requiredReservation) < 0) {
                        throw new InsufficientBalanceException(availableCash, requiredReservation);
                    }

                    // Initial status
                    if (order.getOrderType() == OrderType.LIMIT || order.getOrderType() == OrderType.BRACKET) {
                        order.setStatus(OrderStatus.OPEN);
                    } else if (order.getOrderType() == OrderType.STOP_LOSS_MARKET || order.getOrderType() == OrderType.STOP_LOSS_LIMIT || order.getOrderType() == OrderType.TARGET) {
                        order.setStatus(OrderStatus.TRIGGER_PENDING);
                    } else {
                        order.setStatus(OrderStatus.PENDING);
                    }

                    long orderId = orderDAO.insert(c, order);
                    reservationDAO.reserveFunds(c, orderId, user.getId(), requiredReservation);

                } else {
                    // SELL order: Verify available holdings
                    Holding holding = holdingDAO.findForUpdate(c, user.getId(), order.getStockId());
                    int heldQty = holding != null ? holding.getQuantity() : 0;
                    int reservedQty = reservationDAO.getTotalReservedHoldings(c, user.getId(), order.getStockId());
                    int availableQty = heldQty - reservedQty;

                    if (availableQty < order.getQuantity()) {
                        throw new InsufficientHoldingsException(availableQty, order.getQuantity());
                    }

                    if (order.getOrderType() == OrderType.LIMIT || order.getOrderType() == OrderType.BRACKET) {
                        order.setStatus(OrderStatus.OPEN);
                    } else if (order.getOrderType() == OrderType.STOP_LOSS_MARKET || order.getOrderType() == OrderType.STOP_LOSS_LIMIT || order.getOrderType() == OrderType.TARGET) {
                        order.setStatus(OrderStatus.TRIGGER_PENDING);
                    } else {
                        order.setStatus(OrderStatus.PENDING);
                    }

                    long orderId = orderDAO.insert(c, order);
                    reservationDAO.reserveHoldings(c, orderId, user.getId(), order.getStockId(), order.getQuantity());
                }

                c.commit();

                // 5. If this is a MARKET order, immediately trigger execution engine evaluation
                MarketQuote latest = MarketDataConnectionManager.getInstance().getLatestQuote(order.getStockId());
                if (latest != null && !latest.isStale()) {
                    executionEngine.evaluateSingleOrder(latest, order.getId());
                }

                return orderDAO.findById(c, order.getId());

            } catch (AppException e) {
                c.rollback();
                throw e;
            } catch (SQLException e) {
                c.rollback();
                throw new DataAccessException("Failed to place order: " + e.getMessage(), e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database connection error: " + e.getMessage(), e);
        }
    }

    /**
     * Cancels an active order and releases reserved cash or holdings atomically.
     */
    public void cancelOrder(long orderId, long userId) throws AppException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Order order = orderDAO.findByIdForUpdate(c, orderId);
                if (order == null) throw new InvalidOrderException("Order #" + orderId + " not found");
                if (order.getUserId() != userId) throw new InvalidOrderException("Unauthorized to cancel order #" + orderId);

                if (!order.isCancellable()) {
                    throw new InvalidOrderException("Order #" + orderId + " is in " + order.getStatus() + " status and cannot be cancelled");
                }

                orderDAO.updateStatus(c, orderId, OrderStatus.CANCELLED, "Cancelled by user");

                // Release reservations
                if (order.getSide() == OrderSide.BUY) {
                    reservationDAO.releaseReservedFunds(c, orderId);
                } else {
                    reservationDAO.releaseReservedHoldings(c, orderId);
                }

                // If bracket parent cancelled, cancel any child exit orders
                List<Order> children = orderDAO.findChildOrders(c, orderId);
                for (Order child : children) {
                    if (child.getStatus().isActive()) {
                        orderDAO.updateStatus(c, child.getId(), OrderStatus.CANCELLED, "Parent bracket order cancelled");
                        reservationDAO.releaseReservedHoldings(c, child.getId());
                    }
                }

                c.commit();
            } catch (AppException e) {
                c.rollback();
                throw e;
            } catch (SQLException e) {
                c.rollback();
                throw new DataAccessException("Failed to cancel order: " + e.getMessage(), e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database connection error: " + e.getMessage(), e);
        }
    }

    public List<Order> getUserOrders(long userId, OrderStatus statusFilter, int limit) {
        try (Connection c = DBConnection.getConnection()) {
            return orderDAO.findByUser(c, userId, statusFilter, limit);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load user orders", e);
        }
    }

    public List<Fill> getOrderFills(long orderId) {
        try (Connection c = DBConnection.getConnection()) {
            return fillDAO.findByOrder(c, orderId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load fills for order #" + orderId, e);
        }
    }

    public List<OrderEvent> getOrderEvents(long orderId) {
        try (Connection c = DBConnection.getConnection()) {
            return orderDAO.findEventsByOrder(c, orderId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load events for order #" + orderId, e);
        }
    }

    public BigDecimal getUserReservedFunds(long userId) {
        try (Connection c = DBConnection.getConnection()) {
            return reservationDAO.getTotalReservedFunds(c, userId);
        } catch (SQLException e) {
            return BigDecimal.ZERO;
        }
    }

    public int getUserReservedHoldings(long userId, long stockId) {
        try (Connection c = DBConnection.getConnection()) {
            return reservationDAO.getTotalReservedHoldings(c, userId, stockId);
        } catch (SQLException e) {
            return 0;
        }
    }

    private void validateOrder(Order order) throws InvalidOrderException {
        if (order == null) throw new InvalidOrderException("Order cannot be null");
        if (order.getUserId() == null || order.getUserId() <= 0) throw new InvalidOrderException("Invalid user ID");
        if (order.getStockId() == null || order.getStockId() <= 0) throw new InvalidOrderException("Invalid stock ID");
        if (order.getSide() == null) throw new InvalidOrderException("Order side (BUY/SELL) is required");
        if (order.getOrderType() == null) throw new InvalidOrderException("Order type is required");

        if (order.getQuantity() <= 0 || order.getQuantity() > MAX_ORDER_QUANTITY) {
            throw new InvalidOrderException("Quantity must be between 1 and " + MAX_ORDER_QUANTITY);
        }

        switch (order.getOrderType()) {
            case LIMIT -> {
                if (order.getLimitPrice() == null || order.getLimitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidOrderException("Limit price must be positive for LIMIT order");
                }
            }
            case STOP_LOSS_MARKET -> {
                if (order.getTriggerPrice() == null || order.getTriggerPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidOrderException("Trigger price must be positive for SL-M order");
                }
            }
            case STOP_LOSS_LIMIT -> {
                if (order.getTriggerPrice() == null || order.getTriggerPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidOrderException("Trigger price must be positive for SL order");
                }
                if (order.getLimitPrice() == null || order.getLimitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidOrderException("Limit price must be positive for SL order");
                }
            }
            case TARGET -> {
                if (order.getTargetPrice() == null || order.getTargetPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidOrderException("Target price must be positive for TARGET order");
                }
            }
            case BRACKET -> {
                if (order.getTargetPrice() == null || order.getTargetPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidOrderException("Target price is required for Bracket order");
                }
                if (order.getStopLossPrice() == null || order.getStopLossPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidOrderException("Stop-loss price is required for Bracket order");
                }
            }
            case MARKET -> {}
        }
    }

    private BigDecimal resolveReferencePrice(Order order, Stock stock) {
        if (order.getLimitPrice() != null) return order.getLimitPrice();
        if (order.getTriggerPrice() != null) return order.getTriggerPrice();
        MarketQuote quote = MarketDataConnectionManager.getInstance().getLatestQuote(order.getStockId());
        if (quote != null && quote.ltp() != null) return quote.ltp();
        return stock.getPrice();
    }
}
