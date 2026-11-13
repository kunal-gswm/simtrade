package com.project.trading.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/**
 * Historical and streaming financial candle representation (OHLCV).
 * Financial precision is preserved with BigDecimal (scale 2).
 */
public class Candle {

    public enum CandleSource {
        SYNTHETIC_BACKFILL,
        SIMULATED_STREAM,
        EXCHANGE_IMPORT;

        public static CandleSource fromStringSafe(String val) {
            if (val == null || val.isBlank()) return SIMULATED_STREAM;
            try {
                return CandleSource.valueOf(val.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return SIMULATED_STREAM;
            }
        }
    }

    private Long id;
    private Long stockId;
    private Timeframe timeframe;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private long volume;
    private Instant openTime;
    private boolean isClosed;
    private CandleSource source = CandleSource.SIMULATED_STREAM;
    private Instant createdAt;
    private Instant updatedAt;

    public Candle() {
    }

    public Candle(Long stockId, Timeframe timeframe, BigDecimal openPrice, BigDecimal highPrice,
                  BigDecimal lowPrice, BigDecimal closePrice, long volume, Instant openTime, boolean isClosed) {
        this(stockId, timeframe, openPrice, highPrice, lowPrice, closePrice, volume, openTime, isClosed, CandleSource.SIMULATED_STREAM);
    }

    public Candle(Long stockId, Timeframe timeframe, BigDecimal openPrice, BigDecimal highPrice,
                  BigDecimal lowPrice, BigDecimal closePrice, long volume, Instant openTime, boolean isClosed,
                  CandleSource source) {
        this.stockId = stockId;
        this.timeframe = timeframe;
        this.openPrice = sanitizePrice(openPrice);
        this.highPrice = sanitizePrice(highPrice);
        this.lowPrice = sanitizePrice(lowPrice);
        this.closePrice = sanitizePrice(closePrice);
        this.volume = Math.max(0, volume);
        this.openTime = Objects.requireNonNull(openTime, "openTime must not be null");
        this.isClosed = isClosed;
        this.source = source != null ? source : CandleSource.SIMULATED_STREAM;
    }

    public Candle(Long id, Long stockId, Timeframe timeframe, BigDecimal openPrice, BigDecimal highPrice,
                  BigDecimal lowPrice, BigDecimal closePrice, long volume, Instant openTime, boolean isClosed,
                  Instant createdAt, Instant updatedAt) {
        this(id, stockId, timeframe, openPrice, highPrice, lowPrice, closePrice, volume, openTime, isClosed,
             CandleSource.SIMULATED_STREAM, createdAt, updatedAt);
    }

    public Candle(Long id, Long stockId, Timeframe timeframe, BigDecimal openPrice, BigDecimal highPrice,
                  BigDecimal lowPrice, BigDecimal closePrice, long volume, Instant openTime, boolean isClosed,
                  CandleSource source, Instant createdAt, Instant updatedAt) {
        this(stockId, timeframe, openPrice, highPrice, lowPrice, closePrice, volume, openTime, isClosed, source);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a new forming candle from an initial price tick.
     */
    public static Candle createInitial(Long stockId, Timeframe timeframe, BigDecimal initialPrice,
                                       long initialVolume, Instant openTime) {
        return createInitial(stockId, timeframe, initialPrice, initialVolume, openTime, CandleSource.SIMULATED_STREAM);
    }

    public static Candle createInitial(Long stockId, Timeframe timeframe, BigDecimal initialPrice,
                                       long initialVolume, Instant openTime, CandleSource source) {
        BigDecimal p = sanitizePrice(initialPrice);
        return new Candle(stockId, timeframe, p, p, p, p, initialVolume, openTime, false, source);
    }

    /**
     * Atomically mutates this active candle in-memory with a new price tick and volume.
     */
    public synchronized void updateWithTick(BigDecimal price, long tickVolume) {
        if (price == null) return;
        BigDecimal p = sanitizePrice(price);
        this.closePrice = p;
        if (this.highPrice == null || p.compareTo(this.highPrice) > 0) {
            this.highPrice = p;
        }
        if (this.lowPrice == null || p.compareTo(this.lowPrice) < 0) {
            this.lowPrice = p;
        }
        if (tickVolume > 0) {
            this.volume += tickVolume;
        }
        this.updatedAt = Instant.now();
    }

    private static BigDecimal sanitizePrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price must not be null");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive: " + price);
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Timeframe getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(Timeframe timeframe) {
        this.timeframe = timeframe;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = sanitizePrice(openPrice);
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = sanitizePrice(highPrice);
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = sanitizePrice(lowPrice);
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = sanitizePrice(closePrice);
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = Math.max(0, volume);
    }

    public Instant getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Instant openTime) {
        this.openTime = Objects.requireNonNull(openTime, "openTime must not be null");
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getEpochSeconds() {
        return openTime != null ? openTime.getEpochSecond() : 0L;
    }

    public CandleSource getSource() {
        return source;
    }

    public void setSource(CandleSource source) {
        this.source = source != null ? source : CandleSource.SIMULATED_STREAM;
    }

    public boolean isBullish() {
        return closePrice != null && openPrice != null && closePrice.compareTo(openPrice) >= 0;
    }

    public Candle copy() {
        Candle c = new Candle(stockId, timeframe, openPrice, highPrice, lowPrice, closePrice, volume, openTime, isClosed, source);
        c.setId(this.id);
        c.setCreatedAt(this.createdAt);
        c.setUpdatedAt(this.updatedAt);
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candle candle = (Candle) o;
        return Objects.equals(stockId, candle.stockId) &&
               timeframe == candle.timeframe &&
               Objects.equals(openTime, candle.openTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId, timeframe, openTime);
    }

    @Override
    public String toString() {
        return "Candle{" +
                "id=" + id +
                ", stockId=" + stockId +
                ", tf=" + (timeframe != null ? timeframe.getCode() : null) +
                ", O=" + openPrice +
                ", H=" + highPrice +
                ", L=" + lowPrice +
                ", C=" + closePrice +
                ", V=" + volume +
                ", time=" + openTime +
                ", closed=" + isClosed +
                ", source=" + source +
                '}';
    }
}
