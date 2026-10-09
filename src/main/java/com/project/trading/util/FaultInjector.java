package com.project.trading.util;

public class FaultInjector {
    public static volatile boolean failMidTrade = false;

    public static void checkpoint(String where) {
        if (failMidTrade) {
            throw new IllegalStateException("Injected failure");
        }
    }
}
