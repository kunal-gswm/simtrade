package com.project.trading.order.model;

import java.time.Instant;

/**
 * Immutable audit trail event tracking order lifecycle state transitions.
 */
public record OrderEvent(
        Long id,
        long orderId,
        String fromStatus,
        String toStatus,
        String reason,
        Instant eventTime
) {
    public OrderEvent(long orderId, String fromStatus, String toStatus, String reason) {
        this(null, orderId, fromStatus, toStatus, reason, Instant.now());
    }
}
