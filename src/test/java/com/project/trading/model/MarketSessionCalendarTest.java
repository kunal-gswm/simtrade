package com.project.trading.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MarketSessionCalendarTest {

    @BeforeEach
    public void setUp() {
        MarketSessionCalendar.resetHolidaysToDefault();
        MarketSessionCalendar.setSessionEnforced(false);
    }

    @AfterEach
    public void tearDown() {
        MarketSessionCalendar.resetHolidaysToDefault();
        MarketSessionCalendar.setSessionEnforced(false);
    }

    @Test
    public void testMarketTimezoneIsAsiaKolkata() {
        assertEquals("Asia/Kolkata", MarketSessionCalendar.ZONE_IST.getId());
    }

    @Test
    public void testTradingDaysAndWeekendExclusion() {
        // Monday through Friday
        LocalDate monday = LocalDate.of(2026, 11, 9);
        LocalDate friday = LocalDate.of(2026, 11, 13);
        LocalDate saturday = LocalDate.of(2026, 11, 14);
        LocalDate sunday = LocalDate.of(2026, 11, 15);

        assertEquals(DayOfWeek.MONDAY, monday.getDayOfWeek());
        assertEquals(DayOfWeek.FRIDAY, friday.getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY, saturday.getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY, sunday.getDayOfWeek());

        assertTrue(MarketSessionCalendar.isTradingDay(monday), "Monday must be a trading day");
        assertTrue(MarketSessionCalendar.isTradingDay(friday), "Friday must be a trading day");
        assertFalse(MarketSessionCalendar.isTradingDay(saturday), "Saturday must NEVER be a trading day");
        assertFalse(MarketSessionCalendar.isTradingDay(sunday), "Sunday must NEVER be a trading day");
    }

    @Test
    public void testNSEHolidaysAreRecognized() {
        // Republic Day 2026-01-26 (Monday)
        LocalDate republicDay = LocalDate.of(2026, 1, 26);
        assertEquals(DayOfWeek.MONDAY, republicDay.getDayOfWeek());
        assertTrue(MarketSessionCalendar.isHoliday(republicDay), "Republic Day must be recognized as exchange holiday");
        assertFalse(MarketSessionCalendar.isTradingDay(republicDay), "Exchange holiday must not be a trading day");

        // Mahatma Gandhi Jayanti 2026-10-02 (Friday)
        LocalDate gandhiJayanti = LocalDate.of(2026, 10, 2);
        assertTrue(MarketSessionCalendar.isHoliday(gandhiJayanti));
        assertFalse(MarketSessionCalendar.isTradingDay(gandhiJayanti));

        // Christmas 2026-12-25 (Friday)
        LocalDate christmas = LocalDate.of(2026, 12, 25);
        assertTrue(MarketSessionCalendar.isHoliday(christmas));
        assertFalse(MarketSessionCalendar.isTradingDay(christmas));
    }

    @Test
    public void testSessionHoursOpenClose() {
        LocalDate tradingDay = LocalDate.of(2026, 11, 11); // Wednesday

        // 09:14:59 IST -> Before market open
        Instant preOpen = tradingDay.atTime(9, 14, 59).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        assertFalse(MarketSessionCalendar.isSessionOpen(preOpen));

        // 09:15:00 IST -> Session open
        Instant openTime = tradingDay.atTime(9, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        assertTrue(MarketSessionCalendar.isSessionOpen(openTime));

        // 12:30:00 IST -> Mid session
        Instant midSession = tradingDay.atTime(12, 30, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        assertTrue(MarketSessionCalendar.isSessionOpen(midSession));

        // 15:29:59 IST -> Last second of session
        Instant lastSecond = tradingDay.atTime(15, 29, 59).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        assertTrue(MarketSessionCalendar.isSessionOpen(lastSecond));

        // 15:30:00 IST -> Market closed
        Instant closeTime = tradingDay.atTime(15, 30, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        assertFalse(MarketSessionCalendar.isSessionOpen(closeTime));

        // Weekend (Sunday 11:00 AM)
        LocalDate sunday = LocalDate.of(2026, 11, 15);
        Instant sundayTime = sunday.atTime(11, 0, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        assertFalse(MarketSessionCalendar.isSessionOpen(sundayTime));
    }

    @Test
    public void testHourlyCandleAnchorsAtFifteenMinutesPastHour() {
        LocalDate day = LocalDate.of(2026, 11, 12); // Thursday

        // 09:15:00 IST tick -> starts at 09:15:00
        Instant t1 = day.atTime(9, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        Instant c1 = MarketSessionCalendar.getCandleStart(Timeframe.ONE_HOUR, t1);
        assertEquals(day.atTime(9, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant(), c1);

        // 09:45:00 IST tick -> starts at 09:15:00
        Instant t2 = day.atTime(9, 45, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        Instant c2 = MarketSessionCalendar.getCandleStart(Timeframe.ONE_HOUR, t2);
        assertEquals(day.atTime(9, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant(), c2);

        // 10:14:59 IST tick -> still within 09:15 candle!
        Instant t3 = day.atTime(10, 14, 59).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        Instant c3 = MarketSessionCalendar.getCandleStart(Timeframe.ONE_HOUR, t3);
        assertEquals(day.atTime(9, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant(), c3);

        // 10:15:00 IST tick -> starts 10:15:00 candle
        Instant t4 = day.atTime(10, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        Instant c4 = MarketSessionCalendar.getCandleStart(Timeframe.ONE_HOUR, t4);
        assertEquals(day.atTime(10, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant(), c4);

        // 15:15:00 IST tick -> closing hour/session bar starting at 15:15:00
        Instant t5 = day.atTime(15, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        Instant c5 = MarketSessionCalendar.getCandleStart(Timeframe.ONE_HOUR, t5);
        assertEquals(day.atTime(15, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant(), c5);
    }

    @Test
    public void testNextCandleBoundaryRollover() {
        LocalDate friday = LocalDate.of(2026, 11, 13);
        LocalDate monday = LocalDate.of(2026, 11, 16);

        // 1h candle at 15:15 Friday rolls over weekend to Monday 09:15
        Instant fri1515 = friday.atTime(15, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        Instant nextHourly = MarketSessionCalendar.getNextCandleStart(Timeframe.ONE_HOUR, fri1515);
        assertEquals(monday.atTime(9, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant(), nextHourly,
                "Last hourly bar on Friday must roll to Monday 09:15 open");

        // 1d candle for Friday rolls to Monday 09:15
        Instant friDaily = friday.atTime(9, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
        Instant nextDaily = MarketSessionCalendar.getNextCandleStart(Timeframe.ONE_DAY, friDaily);
        assertEquals(monday.atTime(9, 15, 0).atZone(MarketSessionCalendar.ZONE_IST).toInstant(), nextDaily,
                "Daily candle on Friday must roll to Monday session open");
    }

    @Test
    public void testRecentTradingDaysSequence() {
        LocalDate wednesday = LocalDate.of(2026, 11, 11);
        List<LocalDate> days = MarketSessionCalendar.getRecentTradingDays(wednesday, 5);

        assertEquals(5, days.size());
        assertEquals(LocalDate.of(2026, 11, 5), days.get(0));
        assertEquals(LocalDate.of(2026, 11, 6), days.get(1)); // Friday
        // 2026-11-07 (Sat) and 2026-11-08 (Sun / Diwali) skipped!
        assertEquals(LocalDate.of(2026, 11, 9), days.get(2)); // Monday
        assertEquals(LocalDate.of(2026, 11, 10), days.get(3));
        assertEquals(LocalDate.of(2026, 11, 11), days.get(4));
    }
}
