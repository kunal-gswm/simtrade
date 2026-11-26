package com.project.trading.ui.components;

import com.project.trading.model.Candle;
import com.project.trading.model.Stock;
import com.project.trading.model.Timeframe;
import com.project.trading.service.CandleService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

import java.util.List;

/**
 * Vaadin Flow wrapper for the TradingView Lightweight Charts custom element.
 * Connects server-side candle streams with high-performance 60fps client rendering.
 */
@Tag("trading-chart")
@JsModule("./src/components/trading-chart.js")
@NpmPackage(value = "lightweight-charts", version = "4.1.3")
public class TradingChartView extends Component {

    private final CandleService candleService;
    private Stock currentStock;
    private Timeframe currentTimeframe = Timeframe.ONE_MIN;

    public TradingChartView() {
        this(new CandleService());
    }

    public TradingChartView(CandleService candleService) {
        this.candleService = candleService;

        getElement().setProperty("currentTimeframe", currentTimeframe.getCode());

        // Listen for client-side timeframe pill clicks
        getElement().addEventListener("timeframe-changed", event -> {
            String tfCode = event.getEventData().getString("event.detail.timeframe");
            if (tfCode != null && !tfCode.isBlank()) {
                setTimeframe(Timeframe.fromCodeSafe(tfCode));
            }
        }).addEventData("event.detail.timeframe");
    }

    public void setStock(Stock stock) {
        this.currentStock = stock;
        if (stock == null) {
            getElement().setProperty("symbol", "");
            getElement().setProperty("companyName", "");
            getElement().callJsFunction("setData", "[]");
            return;
        }

        getElement().setProperty("symbol", stock.getSymbol());
        getElement().setProperty("companyName", stock.getCompanyName() != null ? stock.getCompanyName() : "");
        loadChartData();
    }

    public Stock getStock() {
        return currentStock;
    }

    public void setTimeframe(Timeframe timeframe) {
        if (timeframe == null) return;
        this.currentTimeframe = timeframe;
        getElement().setProperty("currentTimeframe", timeframe.getCode());
        loadChartData();
    }

    public Timeframe getTimeframe() {
        return currentTimeframe;
    }

    public void setHeight(int heightPx) {
        getElement().setProperty("height", heightPx);
    }

    public void setCompact(boolean compact) {
        getElement().setProperty("isCompact", compact);
    }

    public void loadChartData() {
        if (currentStock == null) return;

        try {
            List<Candle> candles = candleService.getCandles(currentStock.getId(), currentTimeframe.getCode(), 120);
            String json = serializeCandles(candles);
            getElement().callJsFunction("setData", json);
        } catch (Exception e) {
            System.err.println("[TradingChartView] Error loading chart data: " + e.getMessage());
        }
    }

    /**
     * Pushes a streaming candle tick to the chart.
     */
    public void onCandleUpdate(Candle candle) {
        if (candle == null || currentStock == null) return;
        if (!candle.getStockId().equals(currentStock.getId())) return;
        if (candle.getTimeframe() != currentTimeframe) return;

        String json = serializeSingleCandle(candle);
        getElement().callJsFunction("updateCandle", json);
    }

    private String serializeCandles(List<Candle> candles) {
        if (candles == null || candles.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < candles.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(serializeSingleCandle(candles.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String serializeSingleCandle(Candle c) {
        return "{" +
                "\"time\":" + c.getEpochSeconds() + "," +
                "\"open\":" + (c.getOpenPrice() != null ? c.getOpenPrice().toPlainString() : "0") + "," +
                "\"high\":" + (c.getHighPrice() != null ? c.getHighPrice().toPlainString() : "0") + "," +
                "\"low\":" + (c.getLowPrice() != null ? c.getLowPrice().toPlainString() : "0") + "," +
                "\"close\":" + (c.getClosePrice() != null ? c.getClosePrice().toPlainString() : "0") + "," +
                "\"volume\":" + c.getVolume() + "," +
                "\"source\":\"" + (c.getSource() != null ? c.getSource().name() : "SIMULATED_STREAM") + "\"" +
                "}";
    }
}
