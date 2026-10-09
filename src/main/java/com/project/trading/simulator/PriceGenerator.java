package com.project.trading.simulator;

import java.math.BigDecimal;

public interface PriceGenerator {
    BigDecimal next(BigDecimal current);
}
