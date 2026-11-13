package com.project.trading.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TimeframeTest {

    @Test
    public void testSupportedTimeframes() {
        assertEquals("1m", Timeframe.ONE_MIN.getCode());
        assertEquals(60L, Timeframe.ONE_MIN.getDurationSeconds());

        assertEquals("5m", Timeframe.FIVE_MIN.getCode());
        assertEquals(300L, Timeframe.FIVE_MIN.getDurationSeconds());

        assertEquals("15m", Timeframe.FIFTEEN_MIN.getCode());
        assertEquals(900L, Timeframe.FIFTEEN_MIN.getDurationSeconds());

        assertEquals("1h", Timeframe.ONE_HOUR.getCode());
        assertEquals(3600L, Timeframe.ONE_HOUR.getDurationSeconds());

        assertEquals("1d", Timeframe.ONE_DAY.getCode());
        assertEquals(86400L, Timeframe.ONE_DAY.getDurationSeconds());
    }

    @Test
    public void testFromCode() {
        assertEquals(Timeframe.ONE_MIN, Timeframe.fromCode("1m"));
        assertEquals(Timeframe.FIVE_MIN, Timeframe.fromCode("5M"));
        assertEquals(Timeframe.FIFTEEN_MIN, Timeframe.fromCode("15m"));
        assertEquals(Timeframe.ONE_HOUR, Timeframe.fromCode("1h"));
        assertEquals(Timeframe.ONE_DAY, Timeframe.fromCode("1D"));

        assertEquals(Timeframe.ONE_MIN, Timeframe.fromCodeSafe("unknown"));
        assertEquals(Timeframe.ONE_MIN, Timeframe.fromCodeSafe(null));
        assertThrows(IllegalArgumentException.class, () -> Timeframe.fromCode("invalid"));
    }

    @Test
    public void testCandleStartBoundariesInUTC() {
        // 2026-11-13T10:15:42Z (epoch 1794564942)
        Instant instant = Instant.parse("2026-11-13T10:15:42Z");

        // 1m boundary -> 10:15:00Z
        Instant start1m = Timeframe.ONE_MIN.getCandleStart(instant);
        assertEquals(Instant.parse("2026-11-13T10:15:00Z"), start1m);

        // 5m boundary -> 10:15:00Z
        Instant start5m = Timeframe.FIVE_MIN.getCandleStart(instant);
        assertEquals(Instant.parse("2026-11-13T10:15:00Z"), start5m);

        // 15m boundary -> 10:15:00Z
        Instant start15m = Timeframe.FIFTEEN_MIN.getCandleStart(instant);
        assertEquals(Instant.parse("2026-11-13T10:15:00Z"), start15m);

        // 1h boundary -> 10:00:00Z
        Instant start1h = Timeframe.ONE_HOUR.getCandleStart(instant);
        assertEquals(Instant.parse("2026-11-13T10:00:00Z"), start1h);

        // 1d boundary -> 00:00:00Z
        Instant start1d = Timeframe.ONE_DAY.getCandleStart(instant);
        assertEquals(Instant.parse("2026-11-13T00:00:00Z"), start1d);
    }
}
