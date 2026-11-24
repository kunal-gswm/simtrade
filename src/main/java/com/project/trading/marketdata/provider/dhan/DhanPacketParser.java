package com.project.trading.marketdata.provider.dhan;

import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDepth;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.service.InstrumentMappingService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parser for DhanHQ Official WebSocket Market Feed v2 binary packet structures.
 *
 * Packet Header (8 bytes):
 *   - Response Code (1 byte): 2=Ticker, 4=Quote, 8=Full Depth, 50=Disconnection
 *   - Message Length (2 bytes short, Little Endian)
 *   - Exchange Segment (1 byte): 1=NSE_EQ, etc.
 *   - Security ID (4 bytes int, Little Endian)
 *
 * Precision Notice:
 *   DhanHQ binary feed transmits monetary prices as IEEE-754 32-bit single-precision floats.
 *   This parser normalizes floats into financial BigDecimal scaled to 2 decimal places (paise)
 *   using RoundingMode.HALF_UP.
 */
public class DhanPacketParser {

    public static final byte CODE_TICKER = 2;
    public static final byte CODE_QUOTE = 4;
    public static final byte CODE_FULL_DEPTH = 8;
    public static final byte CODE_DISCONNECT = 50;

    private final InstrumentMappingService mappingService;

    public DhanPacketParser() {
        this(InstrumentMappingService.getInstance());
    }

    public DhanPacketParser(InstrumentMappingService mappingService) {
        this.mappingService = mappingService;
    }

    /**
     * Parses a binary DhanHQ market packet into a normalized MarketQuote if valid.
     */
    public Optional<MarketQuote> parseQuote(byte[] data) {
        if (data == null || data.length < 8) {
            return Optional.empty();
        }

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        byte responseCode = buffer.get();
        short messageLength = buffer.getShort();
        byte exchangeSegment = buffer.get();
        int securityIdInt = buffer.getInt();
        String securityId = String.valueOf(securityIdInt);

        var mappingOpt = mappingService.getByProviderSecurityId(securityId);
        if (mappingOpt.isEmpty()) {
            return Optional.empty();
        }
        var mapping = mappingOpt.get();
        Instant receivedTime = Instant.now();

        if (responseCode == CODE_TICKER && data.length >= 16) {
            float ltpFloat = buffer.getFloat();
            int lttSec = buffer.getInt();
            BigDecimal ltp = normalizePrice(ltpFloat);
            Instant exchangeTime = lttSec > 0 ? Instant.ofEpochSecond(lttSec) : receivedTime;

            // Approximate bid/ask around LTP for ticker packets lacking L2 depth
            BigDecimal spread = new BigDecimal("0.05");
            BigDecimal bid = ltp.subtract(spread);
            BigDecimal ask = ltp.add(spread);

            MarketQuote quote = new MarketQuote(
                    mapping.stockId(),
                    mapping.symbol(),
                    mapping.exchange(),
                    securityId,
                    MarketDataSource.LIVE_PROVIDER,
                    exchangeTime,
                    receivedTime,
                    ltp,
                    ltp, // prevClose unknown in raw ticker, default to ltp
                    ltp, ltp, ltp,
                    0L,
                    bid, 100, ask, 100,
                    false,
                    "VALID"
            );
            return Optional.of(quote);

        } else if (responseCode == CODE_QUOTE && data.length >= 48) {
            float ltpFloat = buffer.getFloat();
            short ltq = buffer.getShort();
            int lttSec = buffer.getInt();
            float avgPriceFloat = buffer.getFloat();
            int volumeInt = buffer.getInt();
            int totalSellQty = buffer.getInt();
            int totalBuyQty = buffer.getInt();
            float openFloat = buffer.getFloat();
            float closeFloat = buffer.getFloat();
            float highFloat = buffer.getFloat();
            float lowFloat = buffer.getFloat();

            BigDecimal ltp = normalizePrice(ltpFloat);
            BigDecimal prevClose = normalizePrice(closeFloat);
            BigDecimal open = normalizePrice(openFloat);
            BigDecimal high = normalizePrice(highFloat);
            BigDecimal low = normalizePrice(lowFloat);
            long volume = Integer.toUnsignedLong(volumeInt);
            Instant exchangeTime = lttSec > 0 ? Instant.ofEpochSecond(lttSec) : receivedTime;

            BigDecimal spread = new BigDecimal("0.05");
            BigDecimal bid = ltp.subtract(spread);
            BigDecimal ask = ltp.add(spread);

            MarketQuote quote = new MarketQuote(
                    mapping.stockId(),
                    mapping.symbol(),
                    mapping.exchange(),
                    securityId,
                    MarketDataSource.LIVE_PROVIDER,
                    exchangeTime,
                    receivedTime,
                    ltp,
                    prevClose,
                    open, high, low,
                    volume,
                    bid, Math.max(1, totalBuyQty > 0 ? totalBuyQty / 100 : 50),
                    ask, Math.max(1, totalSellQty > 0 ? totalSellQty / 100 : 50),
                    false,
                    "VALID"
            );
            return Optional.of(quote);
        }

        return Optional.empty();
    }

    /**
     * Parses Full Depth packet (Code 8) into normalized MarketDepth with 5 Bid & Ask levels.
     */
    public Optional<MarketDepth> parseDepth(byte[] data) {
        if (data == null || data.length < 8) {
            return Optional.empty();
        }
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        byte responseCode = buffer.get();
        if (responseCode != CODE_FULL_DEPTH) {
            return Optional.empty();
        }
        short messageLength = buffer.getShort();
        byte exchangeSegment = buffer.get();
        int securityIdInt = buffer.getInt();
        String securityId = String.valueOf(securityIdInt);

        var mappingOpt = mappingService.getByProviderSecurityId(securityId);
        if (mappingOpt.isEmpty() || buffer.remaining() < 8) {
            return Optional.empty();
        }
        var mapping = mappingOpt.get();

        float ltpFloat = buffer.getFloat();
        int lttSec = buffer.getInt();

        List<MarketDepth.DepthLevel> bids = new ArrayList<>();
        List<MarketDepth.DepthLevel> asks = new ArrayList<>();

        // Parse 5 Buy depth packets
        for (int i = 0; i < 5 && buffer.remaining() >= 10; i++) {
            int qty = buffer.getInt();
            short orders = buffer.getShort();
            float price = buffer.getFloat();
            if (qty > 0 && price > 0.0f) {
                bids.add(new MarketDepth.DepthLevel(normalizePrice(price), qty, orders));
            }
        }

        // Parse 5 Sell depth packets
        for (int i = 0; i < 5 && buffer.remaining() >= 10; i++) {
            int qty = buffer.getInt();
            short orders = buffer.getShort();
            float price = buffer.getFloat();
            if (qty > 0 && price > 0.0f) {
                asks.add(new MarketDepth.DepthLevel(normalizePrice(price), qty, orders));
            }
        }

        return Optional.of(new MarketDepth(mapping.stockId(), mapping.symbol(), bids, asks, lttSec * 1000L));
    }

    private BigDecimal normalizePrice(float rawPrice) {
        if (Float.isNaN(rawPrice) || Float.isInfinite(rawPrice) || rawPrice <= 0.0f) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        // Round to 2 decimal places representing Indian Rupee paise
        return BigDecimal.valueOf((double) rawPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
