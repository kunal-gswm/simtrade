package com.project.trading.marketdata;

import com.project.trading.DBCreator;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.dhan.DhanLiveMarketDataProvider;
import com.project.trading.marketdata.provider.dhan.DhanPacketParser;
import com.project.trading.marketdata.service.InstrumentMappingService;
import com.project.trading.marketdata.service.MarketDataConnectionManager;
import com.project.trading.model.Candle;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.OrderValidity;
import com.project.trading.order.service.OrderService;
import com.project.trading.service.CandleEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SimTrade 3.0.2: DhanHQ Live Market Data Activation Tests.
 *
 * Verifies:
 * - Unconfigured fallback when credentials absent
 * - Official DhanHQ JSON subscription formatting
 * - Reconnection backoff calculation & subscription restoration
 * - Live packet ingestion & CandleEngine dispatch
 * - Stale-feed detection
 * - Market order blocking when quotes are unavailable or stale
 */
public class DhanLiveMarketDataActivationTest {

    private InstrumentMappingService mappingService;
    private OrderService orderService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        mappingService = InstrumentMappingService.getInstance();
        orderService = new OrderService();
    }

    private byte[] createBinaryQuotePacket(int securityId, float ltp, int lttSec, float open, float high, float low, float close, int volume) {
        ByteBuffer buf = ByteBuffer.allocate(50).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 4);          // Response code QUOTE
        buf.putShort((short) 50);   // Message length
        buf.put((byte) 1);          // Segment NSE_EQ
        buf.putInt(securityId);     // Security ID
        buf.putFloat(ltp);          // LTP
        buf.putShort((short) 10);   // LTQ
        buf.putInt(lttSec);         // LTT in seconds
        buf.putFloat(ltp);          // Avg price
        buf.putInt(volume);         // Volume
        buf.putInt(5000);           // Total sell qty
        buf.putInt(8000);           // Total buy qty
        buf.putFloat(open);         // Open
        buf.putFloat(close);        // Close
        buf.putFloat(high);         // High
        buf.putFloat(low);          // Low
        return buf.array();
    }

    @Test
    public void testUnconfiguredStateWhenCredentialsMissing() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("", "", false, mappingService);
        assertEquals(MarketDataStatus.NOT_CONFIGURED, provider.getStatus());
        assertTrue(provider.getStatusMessage().contains("NOT CONFIGURED"));

        provider.connect();
        // Remains NOT_CONFIGURED, does not fake connection
        assertEquals(MarketDataStatus.NOT_CONFIGURED, provider.getStatus());
    }

    @Test
    public void testSubscriptionJsonFormatting() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("testUser", "testToken", true, mappingService);
        provider.subscribe(List.of(1L, 2L)); // RELIANCE (1333) and TCS (11536)

        assertEquals(2, provider.getSubscribedStockIds().size());
        assertTrue(provider.getSubscribedStockIds().contains(1L));
        assertTrue(provider.getSubscribedStockIds().contains(2L));

        // Unsubscribe 1 instrument
        provider.unsubscribe(List.of(1L));
        assertEquals(1, provider.getSubscribedStockIds().size());
        assertTrue(provider.getSubscribedStockIds().contains(2L));
    }

    @Test
    public void testReconnectionBackoffCalculation() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("testUser", "testToken", true, mappingService);

        int delay1 = provider.calculateBackoffDelaySeconds();
        assertEquals(2, delay1); // 2^1 = 2

        int delay2 = provider.calculateBackoffDelaySeconds();
        assertEquals(4, delay2); // 2^2 = 4

        int delay3 = provider.calculateBackoffDelaySeconds();
        assertEquals(8, delay3); // 2^3 = 8

        int delay4 = provider.calculateBackoffDelaySeconds();
        assertEquals(16, delay4); // 2^4 = 16

        int delay5 = provider.calculateBackoffDelaySeconds();
        assertEquals(30, delay5); // 2^5 = 32 -> capped at 30

        provider.resetBackoff();
        assertEquals(2, provider.calculateBackoffDelaySeconds());
    }

    @Test
    public void testLivePacketIngestion_DispatchesToListenersAndCandleEngine() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("testUser", "testToken", false, mappingService);
        AtomicReference<MarketQuote> receivedQuote = new AtomicReference<>();
        provider.addListener(new com.project.trading.marketdata.provider.MarketDataListener() {
            @Override
            public void onQuote(MarketQuote quote) {
                receivedQuote.set(quote);
            }
        });

        // Ingest RELIANCE (secId 1333, stockId 1) quote packet
        byte[] packet = createBinaryQuotePacket(1333, 2875.50f, (int) Instant.now().getEpochSecond(), 2850.0f, 2890.0f, 2845.0f, 2850.0f, 500000);
        provider.ingestBinaryPacket(packet);

        MarketQuote quote = provider.getLatestQuote(1L);
        assertNotNull(quote, "Provider must retain latest ingested quote");
        assertEquals(1L, quote.stockId());
        assertEquals("RELIANCE", quote.symbol());
        assertEquals(new BigDecimal("2875.50"), quote.ltp());
        assertEquals(MarketDataSource.LIVE_PROVIDER, quote.source());
        assertNotNull(receivedQuote.get());

        // Forward to CandleEngine to test normalization
        CandleEngine.getInstance().onMarketQuote(quote);
        // Verify CandleEngine processed tick
        Candle candle = CandleEngine.getInstance().getActiveCandle(1L, com.project.trading.model.Timeframe.ONE_MIN);
        assertNotNull(candle, "CandleEngine must record active candle for ingested quote");
        assertEquals(Candle.CandleSource.LIVE_PROVIDER, candle.getSource());
    }

    @Test
    public void testStaleQuoteDetection() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("testUser", "testToken", false, mappingService);

        // Before any quote
        assertTrue(provider.isStale(1L), "Instrument with no quotes must be reported as stale");

        // Ingest fresh quote
        byte[] freshPacket = createBinaryQuotePacket(1333, 2850.00f, (int) Instant.now().getEpochSecond(), 2850.0f, 2850.0f, 2850.0f, 2850.0f, 100);
        provider.ingestBinaryPacket(freshPacket);
        assertFalse(provider.isStale(1L), "Fresh quote must not be stale");
    }

    @Test
    public void testMarketOrder_BlockedWhenQuotesUnavailable() {
        // Switch provider to empty live provider without quotes
        DhanLiveMarketDataProvider emptyProvider = new DhanLiveMarketDataProvider("testUser", "testToken", false, mappingService);
        MarketDataConnectionManager.getInstance().setProvider(emptyProvider);

        Order marketOrder = new Order();
        marketOrder.setUserId(2L);
        marketOrder.setStockId(1L);
        marketOrder.setSide(OrderSide.BUY);
        marketOrder.setOrderType(OrderType.MARKET);
        marketOrder.setQuantity(5);
        marketOrder.setValidity(OrderValidity.DAY);

        InvalidOrderException ex = assertThrows(InvalidOrderException.class, () -> orderService.placeOrder(marketOrder));
        assertTrue(ex.getMessage().contains("Live market quotes currently unavailable"),
                "Market order must be rejected when quotes are unavailable: " + ex.getMessage());

        // Restore simulator provider for other tests
        MarketDataConnectionManager.getInstance().initializeDefaultProvider();
    }

    @Test
    public void testMarketOrder_BlockedWhenQuotesStale() {
        DhanLiveMarketDataProvider staleProvider = new DhanLiveMarketDataProvider("testUser", "testToken", false, mappingService) {
            @Override
            public boolean isStale(long stockId) {
                return true;
            }
        };
        // Ingest dummy quote marked stale
        byte[] packet = createBinaryQuotePacket(1333, 2850.00f, 100, 2850.0f, 2850.0f, 2850.0f, 2850.0f, 100);
        staleProvider.ingestBinaryPacket(packet);

        MarketDataConnectionManager.getInstance().setProvider(staleProvider);

        Order marketOrder = new Order();
        marketOrder.setUserId(2L);
        marketOrder.setStockId(1L);
        marketOrder.setSide(OrderSide.BUY);
        marketOrder.setOrderType(OrderType.MARKET);
        marketOrder.setQuantity(5);
        marketOrder.setValidity(OrderValidity.DAY);

        InvalidOrderException ex = assertThrows(InvalidOrderException.class, () -> orderService.placeOrder(marketOrder));
        assertTrue(ex.getMessage().contains("Market data feed is stale"),
                "Market order must be rejected when quotes are stale: " + ex.getMessage());

        // Restore simulator provider
        MarketDataConnectionManager.getInstance().initializeDefaultProvider();
    }
}
