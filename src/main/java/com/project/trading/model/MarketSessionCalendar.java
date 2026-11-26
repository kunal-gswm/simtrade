package com.project.trading.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * High-precision Indian Market Session Calendar (NSE / BSE).
 *
 * Market Specifications:
 * - Timezone: Asia/Kolkata (IST: UTC+05:30)
 * - Regular Trading Session: 09:15:00 to 15:30:00 IST
 * - Trading Days: Monday through Friday, excluding officially declared exchange holidays
 * - Session Duration: 375 minutes (6 hours 15 minutes)
 *
 * Candle Anchoring:
 * - 1m, 5m, 15m intervals align naturally with 09:15:00.
 * - 1h candles anchor strictly at :15 past the hour (09:15, 10:15, 11:15, 12:15, 13:15, 14:15, 15:15).
 * - 1d candles anchor at 09:15:00 IST for the trading date.
 */
public final class MarketSessionCalendar {

    public static final ZoneId ZONE_IST = ZoneId.of("Asia/Kolkata");
    public static final LocalTime SESSION_OPEN = LocalTime.of(9, 15);
    public static final LocalTime SESSION_CLOSE = LocalTime.of(15, 30);

    private static final Set<LocalDate> DEFAULT_HOLIDAYS = new HashSet<>();
    private static final Set<LocalDate> ACTIVE_HOLIDAYS = Collections.synchronizedSet(new HashSet<>());

    private static volatile boolean sessionEnforced = Boolean.parseBoolean(
            System.getProperty("simtrade.market.session.enforced", "false"));

    static {
        // Official NSE Market Holidays (2024 - 2026)
        // 2024
        addDefaultHoliday(LocalDate.of(2024, 1, 26));  // Republic Day
        addDefaultHoliday(LocalDate.of(2024, 3, 8));   // Mahashivratri
        addDefaultHoliday(LocalDate.of(2024, 3, 25));  // Holi
        addDefaultHoliday(LocalDate.of(2024, 3, 29));  // Good Friday
        addDefaultHoliday(LocalDate.of(2024, 4, 11));  // Id-Ul-Fitr
        addDefaultHoliday(LocalDate.of(2024, 4, 17));  // Ram Navami
        addDefaultHoliday(LocalDate.of(2024, 5, 1));   // Maharashtra Day
        addDefaultHoliday(LocalDate.of(2024, 6, 17));  // Bakri Id
        addDefaultHoliday(LocalDate.of(2024, 7, 17));  // Muharram
        addDefaultHoliday(LocalDate.of(2024, 8, 15));  // Independence Day
        addDefaultHoliday(LocalDate.of(2024, 10, 2));  // Mahatma Gandhi Jayanti
        addDefaultHoliday(LocalDate.of(2024, 11, 1));  // Diwali Laxmi Pujan
        addDefaultHoliday(LocalDate.of(2024, 11, 15)); // Gurunanak Jayanti
        addDefaultHoliday(LocalDate.of(2024, 12, 25)); // Christmas

        // 2025
        addDefaultHoliday(LocalDate.of(2025, 2, 26));  // Mahashivratri
        addDefaultHoliday(LocalDate.of(2025, 3, 14));  // Holi
        addDefaultHoliday(LocalDate.of(2025, 3, 31));  // Id-Ul-Fitr
        addDefaultHoliday(LocalDate.of(2025, 4, 10));  // Mahavir Jayanti
        addDefaultHoliday(LocalDate.of(2025, 4, 14));  // Dr. Baba Saheb Ambedkar Jayanti
        addDefaultHoliday(LocalDate.of(2025, 4, 18));  // Good Friday
        addDefaultHoliday(LocalDate.of(2025, 5, 1));   // Maharashtra Day
        addDefaultHoliday(LocalDate.of(2025, 6, 7));   // Bakri Id
        addDefaultHoliday(LocalDate.of(2025, 8, 15));  // Independence Day
        addDefaultHoliday(LocalDate.of(2025, 10, 2));  // Mahatma Gandhi Jayanti
        addDefaultHoliday(LocalDate.of(2025, 10, 21)); // Diwali Laxmi Pujan
        addDefaultHoliday(LocalDate.of(2025, 11, 5));  // Gurunanak Jayanti
        addDefaultHoliday(LocalDate.of(2025, 12, 25)); // Christmas

        // 2026
        addDefaultHoliday(LocalDate.of(2026, 1, 26));  // Republic Day
        addDefaultHoliday(LocalDate.of(2026, 2, 15));  // Mahashivratri
        addDefaultHoliday(LocalDate.of(2026, 3, 3));   // Holi
        addDefaultHoliday(LocalDate.of(2026, 3, 20));  // Id-Ul-Fitr
        addDefaultHoliday(LocalDate.of(2026, 4, 3));   // Good Friday
        addDefaultHoliday(LocalDate.of(2026, 4, 14));  // Dr. Ambedkar Jayanti
        addDefaultHoliday(LocalDate.of(2026, 5, 1));   // Maharashtra Day
        addDefaultHoliday(LocalDate.of(2026, 5, 27));  // Bakri Id
        addDefaultHoliday(LocalDate.of(2026, 6, 26));  // Muharram
        addDefaultHoliday(LocalDate.of(2026, 10, 2));  // Mahatma Gandhi Jayanti
        addDefaultHoliday(LocalDate.of(2026, 10, 20)); // Dussehra
        addDefaultHoliday(LocalDate.of(2026, 11, 8));  // Diwali Laxmi Pujan
        addDefaultHoliday(LocalDate.of(2026, 11, 24)); // Gurunanak Jayanti
        addDefaultHoliday(LocalDate.of(2026, 12, 25)); // Christmas

        resetHolidaysToDefault();
    }

    private static void addDefaultHoliday(LocalDate d) {
        DEFAULT_HOLIDAYS.add(d);
    }

    private MarketSessionCalendar() {}

    /**
     * Resets active holiday set to the default standard NSE schedule.
     */
    public static void resetHolidaysToDefault() {
        synchronized (ACTIVE_HOLIDAYS) {
            ACTIVE_HOLIDAYS.clear();
            ACTIVE_HOLIDAYS.addAll(DEFAULT_HOLIDAYS);
        }
    }

    /**
     * Adds a market holiday to the active calendar.
     */
    public static void addHoliday(LocalDate date) {
        if (date != null) {
            ACTIVE_HOLIDAYS.add(date);
        }
    }

    /**
     * Removes a date from the active holiday set.
     */
    public static void removeHoliday(LocalDate date) {
        if (date != null) {
            ACTIVE_HOLIDAYS.remove(date);
        }
    }

    /**
     * Checks if a specific date is a configured market holiday.
     */
    public static boolean isHoliday(LocalDate date) {
        return date != null && ACTIVE_HOLIDAYS.contains(date);
    }

    /**
     * Returns true if the date is a regular NSE trading day (Monday–Friday, non-holiday).
     */
    public static boolean isTradingDay(LocalDate date) {
        if (date == null) return false;
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return false;
        }
        return !isHoliday(date);
    }

    /**
     * Returns true if the given instant falls strictly within an active NSE trading session
     * (09:15:00 to 15:30:00 IST on a valid trading day).
     */
    public static boolean isSessionOpen(Instant instant) {
        if (instant == null) return false;
        ZonedDateTime ist = instant.atZone(ZONE_IST);
        LocalDate date = ist.toLocalDate();
        if (!isTradingDay(date)) {
            return false;
        }
        LocalTime time = ist.toLocalTime();
        return !time.isBefore(SESSION_OPEN) && time.isBefore(SESSION_CLOSE);
    }

    /**
     * Finds the immediately preceding trading day before the given date.
     */
    public static LocalDate getPreviousTradingDay(LocalDate date) {
        Objects.requireNonNull(date, "Date cannot be null");
        LocalDate cur = date.minusDays(1);
        while (!isTradingDay(cur)) {
            cur = cur.minusDays(1);
        }
        return cur;
    }

    /**
     * Finds the immediately following trading day after the given date.
     */
    public static LocalDate getNextTradingDay(LocalDate date) {
        Objects.requireNonNull(date, "Date cannot be null");
        LocalDate cur = date.plusDays(1);
        while (!isTradingDay(cur)) {
            cur = cur.plusDays(1);
        }
        return cur;
    }

    /**
     * Returns the most recent `count` trading days up to and including `upTo`,
     * sorted in ascending chronological order (oldest to newest).
     */
    public static List<LocalDate> getRecentTradingDays(LocalDate upTo, int count) {
        Objects.requireNonNull(upTo, "upTo date cannot be null");
        if (count <= 0) return Collections.emptyList();

        List<LocalDate> days = new ArrayList<>(count);
        LocalDate cur = upTo;
        if (!isTradingDay(cur)) {
            cur = getPreviousTradingDay(cur);
        }

        while (days.size() < count) {
            days.add(cur);
            cur = getPreviousTradingDay(cur);
        }

        Collections.reverse(days);
        return days;
    }

    /**
     * Calculates the deterministic candle start timestamp in Asia/Kolkata for any instant.
     * Aligns 1h candles to :15 past the hour (09:15 session open anchor).
     */
    public static Instant getCandleStart(Timeframe tf, Instant time) {
        Objects.requireNonNull(time, "Time cannot be null");
        Objects.requireNonNull(tf, "Timeframe cannot be null");

        ZonedDateTime ist = time.atZone(ZONE_IST);

        return switch (tf) {
            case ONE_MIN -> ist.truncatedTo(ChronoUnit.MINUTES).toInstant();
            case FIVE_MIN -> {
                int min = ist.getMinute();
                int bucketMin = (min / 5) * 5;
                yield ist.withMinute(bucketMin).truncatedTo(ChronoUnit.MINUTES).toInstant();
            }
            case FIFTEEN_MIN -> {
                int min = ist.getMinute();
                int bucketMin = (min / 15) * 15;
                yield ist.withMinute(bucketMin).truncatedTo(ChronoUnit.MINUTES).toInstant();
            }
            case ONE_HOUR -> {
                int min = ist.getMinute();
                ZonedDateTime base = (min >= 15) ? ist : ist.minusHours(1);
                yield base.withMinute(15).withSecond(0).withNano(0).toInstant();
            }
            case ONE_DAY -> ist.toLocalDate().atTime(SESSION_OPEN).atZone(ZONE_IST).toInstant();
        };
    }

    /**
     * Calculates the subsequent candle boundary timestamp.
     */
    public static Instant getNextCandleStart(Timeframe tf, Instant currentStart) {
        Objects.requireNonNull(currentStart, "Current candle start cannot be null");
        Objects.requireNonNull(tf, "Timeframe cannot be null");

        ZonedDateTime ist = currentStart.atZone(ZONE_IST);

        return switch (tf) {
            case ONE_MIN -> ist.plusMinutes(1).toInstant();
            case FIVE_MIN -> ist.plusMinutes(5).toInstant();
            case FIFTEEN_MIN -> ist.plusMinutes(15).toInstant();
            case ONE_HOUR -> {
                // If this is the closing 15-minute bar of the trading session (15:15 to 15:30),
                // the subsequent candle begins at next trading day's 09:15 open.
                if (ist.toLocalTime().equals(LocalTime.of(15, 15))) {
                    LocalDate nextDay = getNextTradingDay(ist.toLocalDate());
                    yield nextDay.atTime(SESSION_OPEN).atZone(ZONE_IST).toInstant();
                }
                yield ist.plusHours(1).toInstant();
            }
            case ONE_DAY -> {
                LocalDate nextDay = getNextTradingDay(ist.toLocalDate());
                yield nextDay.atTime(SESSION_OPEN).atZone(ZONE_IST).toInstant();
            }
        };
    }

    /**
     * Returns true if strict market session enforcement is enabled.
     * When false (default paper simulation mode), ticks outside exchange hours are processed.
     */
    public static boolean isSessionEnforced() {
        return sessionEnforced;
    }

    /**
     * Sets strict market session enforcement mode.
     */
    public static void setSessionEnforced(boolean enforced) {
        sessionEnforced = enforced;
    }
}
