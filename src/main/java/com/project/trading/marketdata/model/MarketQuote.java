package com.project.trading.marketdata.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * Normalized market quote representing the latest tick, top-of-book, and OHLC data.
 * All monetary values use high-precision BigDecimal.
 */
public record MarketQuote(
        long stockId,
        String symbol,
        String exchange,
        String providerSecurityId,
        MarketDataSource source,
        Instant exchangeTimestamp,
        Instant receivedTimestamp,
        BigDecimal ltp,
        BigDecimal prevClose,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        long volume,
        BigDecimal bidPrice,
        int bidQty,
        BigDecimal askPrice,
        int askQty,
        boolean isStale,
        String qualityStatus
) {
    public MarketQuote {
        if (ltp != null) ltp = ltp.setScale(2, RoundingMode.HALF_UP);
        if (prevClose != null) prevClose = prevClose.setScale(2, RoundingMode.HALF_UP);
        if (open != null) open = open.setScale(2, RoundingMode.HALF_UP);
        if (high != null) high = high.setScale(2, RoundingMode.HALF_UP);
        if (low != null) low = low.setScale(2, RoundingMode.HALF_UP);
        if (bidPrice != null) bidPrice = bidPrice.setScale(2, RoundingMode.HALF_UP);
        if (askPrice != null) askPrice = askPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getChange() {
        if (ltp == null || prevClose == null || prevClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return ltp.subtract(prevClose).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPercentChange() {
        if (ltp == null || prevClose == null || prevClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return ltp.subtract(prevClose)
                .divide(prevClose, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public MarketQuote withStale(boolean stale) {
        return new MarketQuote(
                stockId, symbol, exchange, providerSecurityId, source,
                exchangeTimestamp, receivedTimestamp, ltp, prevClose,
                open, high, low, volume, bidPrice, bidQty, askPrice, askQty,
                stale, stale ? "STALE" : "VALID"
        );
    }
}
