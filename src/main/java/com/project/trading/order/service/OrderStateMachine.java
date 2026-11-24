package com.project.trading.order.service;

import com.project.trading.exception.InvalidOrderException;
import com.project.trading.order.model.OrderStatus;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Centralized finite state machine enforcing strict, legal transitions
 * across the entire paper order lifecycle.
 */
public class OrderStateMachine {

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
            OrderStatus.PENDING, EnumSet.of(
                    OrderStatus.OPEN,
                    OrderStatus.TRIGGER_PENDING,
                    OrderStatus.PARTIALLY_FILLED,
                    OrderStatus.FILLED,
                    OrderStatus.REJECTED,
                    OrderStatus.CANCELLED
            ),
            OrderStatus.TRIGGER_PENDING, EnumSet.of(
                    OrderStatus.OPEN,
                    OrderStatus.PARTIALLY_FILLED,
                    OrderStatus.FILLED,
                    OrderStatus.CANCELLED,
                    OrderStatus.EXPIRED
            ),
            OrderStatus.OPEN, EnumSet.of(
                    OrderStatus.PARTIALLY_FILLED,
                    OrderStatus.FILLED,
                    OrderStatus.CANCELLED,
                    OrderStatus.EXPIRED
            ),
            OrderStatus.PARTIALLY_FILLED, EnumSet.of(
                    OrderStatus.FILLED,
                    OrderStatus.CANCELLED,
                    OrderStatus.EXPIRED
            ),
            OrderStatus.FILLED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.REJECTED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.EXPIRED, EnumSet.noneOf(OrderStatus.class)
    );

    public static boolean isValidTransition(OrderStatus from, OrderStatus to) {
        if (from == null || to == null) return false;
        if (from == to) return true; // Idempotent same-state check
        Set<OrderStatus> allowed = VALID_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public static void validateTransition(OrderStatus from, OrderStatus to, Long orderId) throws InvalidOrderException {
        if (!isValidTransition(from, to)) {
            throw new InvalidOrderException(
                    String.format("Illegal order state transition for order #%s: cannot transition from %s to %s",
                            orderId != null ? orderId : "new", from, to)
            );
        }
    }
}
