package com.project.trading.model;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Supported historical and real-time candle aggregation intervals.
 * All interval boundaries are calculated strictly in UTC.
 */
public enum Timeframe {
    ONE_MIN("1m", 60L, "1m"),
    FIVE_MIN("5m", 300L, "5m"),
    FIFTEEN_MIN("15m", 900L, "15m"),
    ONE_HOUR("1h", 3600L, "1h"),
    ONE_DAY("1d", 86400L, "1d");

    private final String code;
    private final long durationSeconds;
    private final String label;

    Timeframe(String code, long durationSeconds, String label) {
        this.code = code;
        this.durationSeconds = durationSeconds;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Calculates the deterministic candle start timestamp (in UTC) for any given instant.
     */
    public Instant getCandleStart(Instant time) {
        Objects.requireNonNull(time, "Time cannot be null");
        if (this == ONE_DAY) {
            return time.atZone(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS).toInstant();
        }
        long epochSec = time.getEpochSecond();
        long startSec = epochSec - (epochSec % durationSeconds);
        return Instant.ofEpochSecond(startSec);
    }

    /**
     * Calculates the subsequent candle boundary.
     */
    public Instant getNextCandleStart(Instant candleStart) {
        Objects.requireNonNull(candleStart, "Candle start cannot be null");
        return candleStart.plusSeconds(durationSeconds);
    }

    /**
     * Resolves a Timeframe from code string (e.g. "1m", "5m", "15m", "1h", "1d").
     * Defaults to ONE_MIN if input is null or unrecognized.
     */
    public static Timeframe fromCode(String code) {
        if (code == null || code.isBlank()) {
            return ONE_MIN;
        }
        String clean = code.trim().toLowerCase();
        for (Timeframe tf : values()) {
            if (tf.code.equalsIgnoreCase(clean)) {
                return tf;
            }
        }
        throw new IllegalArgumentException("Unsupported timeframe: " + code);
    }

    /**
     * Safe lookup returning default (ONE_MIN) instead of throwing.
     */
    public static Timeframe fromCodeSafe(String code) {
        try {
            return fromCode(code);
        } catch (IllegalArgumentException e) {
            return ONE_MIN;
        }
    }
}
