package com.project.trading.broadcaster;

import com.project.trading.model.Trade;
import com.vaadin.flow.shared.Registration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe centralized event broadcaster connecting real-time market simulation
 * and order executions with connected Vaadin UI push sessions.
 */
public class MarketDataBroadcaster {

    public record PriceUpdate(Map<Long, BigDecimal> updatedPrices, long timestamp) {
        public PriceUpdate {
            updatedPrices = updatedPrices != null ? Collections.unmodifiableMap(updatedPrices) : Collections.emptyMap();
        }
    }

    public record TradeUpdate(Trade trade, long timestamp) {}

    @FunctionalInterface
    public interface Registration extends com.vaadin.flow.shared.Registration {
        @Override
        void remove();
    }

    public interface BroadcastListener {
        default void onPriceUpdate(PriceUpdate event) {}
        default void onTradeUpdate(TradeUpdate event) {}
    }

    @FunctionalInterface
    public interface PriceListener {
        void onPriceUpdate(PriceUpdate event);
    }

    @FunctionalInterface
    public interface TradeListener {
        void onTradeUpdate(Trade trade);
    }

    private static final CopyOnWriteArrayList<BroadcastListener> listeners = new CopyOnWriteArrayList<>();

    public static Registration register(BroadcastListener listener) {
        if (listener != null) {
            listeners.add(listener);
            return () -> listeners.remove(listener);
        }
        return () -> {};
    }

    public static Registration registerPriceListener(PriceListener listener) {
        if (listener == null) return () -> {};
        BroadcastListener bl = new BroadcastListener() {
            @Override
            public void onPriceUpdate(PriceUpdate event) {
                listener.onPriceUpdate(event);
            }
        };
        return register(bl);
    }

    public static Registration registerTradeListener(TradeListener listener) {
        if (listener == null) return () -> {};
        BroadcastListener bl = new BroadcastListener() {
            @Override
            public void onTradeUpdate(TradeUpdate event) {
                listener.onTradeUpdate(event.trade());
            }
        };
        return register(bl);
    }

    public static void broadcastPrices(Map<Long, BigDecimal> newPrices) {
        if (newPrices == null || newPrices.isEmpty()) return;
        PriceUpdate event = new PriceUpdate(newPrices, System.currentTimeMillis());
        for (BroadcastListener listener : listeners) {
            try {
                listener.onPriceUpdate(event);
            } catch (Throwable t) {
                System.err.println("[MarketDataBroadcaster] Price listener exception: " + t.getMessage());
            }
        }
    }

    public static void broadcastTrade(Trade trade) {
        if (trade == null) return;
        TradeUpdate event = new TradeUpdate(trade, System.currentTimeMillis());
        for (BroadcastListener listener : listeners) {
            try {
                listener.onTradeUpdate(event);
            } catch (Throwable t) {
                System.err.println("[MarketDataBroadcaster] Trade listener exception: " + t.getMessage());
            }
        }
    }

    public static int getListenerCount() {
        return listeners.size();
    }

    public static void clearListeners() {
        listeners.clear();
    }
}
