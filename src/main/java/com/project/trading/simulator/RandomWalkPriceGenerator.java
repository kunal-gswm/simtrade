package com.project.trading.simulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class RandomWalkPriceGenerator implements PriceGenerator {

    private static final BigDecimal FLOOR_PRICE = new BigDecimal("1.00");

    @Override
    public BigDecimal next(BigDecimal current) {
        if (current == null || current.compareTo(BigDecimal.ZERO) <= 0) {
            return FLOOR_PRICE;
        }
        // Multiply by 1 + uniform(-0.015, +0.015), round to 2 dp, floor at 1.00
        double deltaPercent = ThreadLocalRandom.current().nextDouble(-0.015, 0.015);
        BigDecimal factor = BigDecimal.valueOf(1.0 + deltaPercent);
        BigDecimal newPrice = current.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        if (newPrice.compareTo(FLOOR_PRICE) < 0) {
            return FLOOR_PRICE;
        }
        return newPrice;
    }
}
