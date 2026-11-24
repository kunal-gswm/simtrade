package com.project.trading.marketdata.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to map internal SimTrade stock IDs to exchange symbols, segments,
 * and provider-specific security tokens (e.g. DhanHQ security IDs).
 */
public class InstrumentMappingService {

    public record InstrumentMapping(
            long stockId,
            String symbol,
            String exchange,
            String segment,
            String providerSecurityId
    ) {}

    private final Map<Long, InstrumentMapping> idToMapping = new ConcurrentHashMap<>();
    private final Map<String, InstrumentMapping> symbolToMapping = new ConcurrentHashMap<>();
    private final Map<String, InstrumentMapping> providerIdToMapping = new ConcurrentHashMap<>();

    private static final InstrumentMappingService INSTANCE = new InstrumentMappingService();

    public static InstrumentMappingService getInstance() {
        return INSTANCE;
    }

    public InstrumentMappingService() {
        // Initialize standard Indian equity instrument mappings (NSE Cash / DhanHQ security tokens)
        register(1L, "RELIANCE", "NSE", "NSE_EQ", "1333");
        register(2L, "TCS", "NSE", "NSE_EQ", "11536");
        register(3L, "INFY", "NSE", "NSE_EQ", "1594");
        register(4L, "HDFCBANK", "NSE", "NSE_EQ", "1330");
        register(5L, "ICICIBANK", "NSE", "NSE_EQ", "4963");
        register(6L, "SBIN", "NSE", "NSE_EQ", "3045");
        register(7L, "ITC", "NSE", "NSE_EQ", "1660");
        register(8L, "LT", "NSE", "NSE_EQ", "11483");
        register(9L, "BHARTIARTL", "NSE", "NSE_EQ", "10604");
        register(10L, "HINDUNILVR", "NSE", "NSE_EQ", "1394");
        register(11L, "WIPRO", "NSE", "NSE_EQ", "3787");
        register(12L, "TATAMOTORS", "NSE", "NSE_EQ", "3456");
        register(13L, "MARUTI", "NSE", "NSE_EQ", "10999");
        register(14L, "ASIANPAINT", "NSE", "NSE_EQ", "236");
        register(15L, "SUNPHARMA", "NSE", "NSE_EQ", "3351");
        register(16L, "HCLTECH", "NSE", "NSE_EQ", "7229");
        register(17L, "TECHM", "NSE", "NSE_EQ", "13538");
        register(18L, "LTIM", "NSE", "NSE_EQ", "17818");
        register(19L, "BAJFINANCE", "NSE", "NSE_EQ", "317");
        register(20L, "BAJAJFINSV", "NSE", "NSE_EQ", "16675");
        register(21L, "KOTAKBANK", "NSE", "NSE_EQ", "1922");
        register(22L, "AXISBANK", "NSE", "NSE_EQ", "5900");
        register(23L, "INDUSINDBK", "NSE", "NSE_EQ", "5258");
        register(24L, "ULTRACEMCO", "NSE", "NSE_EQ", "11532");
        register(25L, "GRASIM", "NSE", "NSE_EQ", "1232");
        register(26L, "TITAN", "NSE", "NSE_EQ", "3506");
        register(27L, "NESTLEIND", "NSE", "NSE_EQ", "17963");
        register(28L, "BRITANNIA", "NSE", "NSE_EQ", "547");
        register(29L, "TATASTEEL", "NSE", "NSE_EQ", "3499");
        register(30L, "HINDALCO", "NSE", "NSE_EQ", "1363");
    }

    public synchronized void register(long stockId, String symbol, String exchange, String segment, String providerSecurityId) {
        if (stockId <= 0 || symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid instrument registration parameters");
        }
        String cleanSymbol = symbol.trim().toUpperCase();
        String cleanExchange = exchange != null ? exchange.trim().toUpperCase() : "NSE";
        String cleanSegment = segment != null ? segment.trim().toUpperCase() : "NSE_EQ";
        String cleanProviderId = providerSecurityId != null ? providerSecurityId.trim() : String.valueOf(stockId);

        InstrumentMapping mapping = new InstrumentMapping(stockId, cleanSymbol, cleanExchange, cleanSegment, cleanProviderId);
        idToMapping.put(stockId, mapping);
        symbolToMapping.put(cleanSymbol, mapping);
        providerIdToMapping.put(cleanProviderId, mapping);
    }

    public Optional<InstrumentMapping> getByStockId(long stockId) {
        return Optional.ofNullable(idToMapping.get(stockId));
    }

    public Optional<InstrumentMapping> getBySymbol(String symbol) {
        if (symbol == null) return Optional.empty();
        return Optional.ofNullable(symbolToMapping.get(symbol.trim().toUpperCase()));
    }

    public Optional<InstrumentMapping> getByProviderSecurityId(String providerSecurityId) {
        if (providerSecurityId == null) return Optional.empty();
        return Optional.ofNullable(providerIdToMapping.get(providerSecurityId.trim()));
    }

    public boolean isValidInstrument(long stockId) {
        return idToMapping.containsKey(stockId);
    }

    public Map<Long, InstrumentMapping> getAllMappings() {
        return Collections.unmodifiableMap(idToMapping);
    }
}
