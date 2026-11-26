package com.project.trading.marketdata.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Normalized 5-level market depth (order book bid/ask levels).
 */
public record MarketDepth(
        long stockId,
        String symbol,
        List<DepthLevel> bids,
        List<DepthLevel> asks,
        long exchangeTimestamp
) {
    public record DepthLevel(BigDecimal price, int quantity, int orders) {}

    public MarketDepth {
        bids = bids != null ? Collections.unmodifiableList(bids) : Collections.emptyList();
        asks = asks != null ? Collections.unmodifiableList(asks) : Collections.emptyList();
    }
}
