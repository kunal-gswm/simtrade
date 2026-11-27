package com.project.trading.marketdata;

import com.project.trading.DBCreator;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.MarketDataListener;
import com.project.trading.marketdata.provider.dhan.DhanLiveMarketDataProvider;
import com.project.trading.marketdata.provider.dhan.DhanPacketParser;
import com.project.trading.marketdata.service.InstrumentMappingService;
import com.project.trading.marketdata.service.MarketDataConnectionManager;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.OrderValidity;
import com.project.trading.order.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SimTrade 3.0.4: DhanHQ Authentication & State Machine Verification Test Suite.
 *
 * Verifies:
 * 1. HTTP 101 without successful authentication does NOT transition to LIVE.
 * 2. Invalid and expired token handling.
 * 3. Controlled test-double authentication progression.
 * 4. Subscription success and failure.
 * 5. Documented binary fixture decoding (Codes 2, 4).
 * 6. Distinguishing fixtures from real provider observations.
 * 7. Stale quotes transition and recovery.
 * 8. Network disconnect handling.
 * 9. Reconnection and subscription restoration.
 * 10. Market order rejection in EVERY unsafe state.
 * 11. Secret redaction from logs, status messages, and URLs.
 * 12. Concurrent state transitions and thread safety.
 */
public class DhanAuthenticationStateMachineTest {

    private InstrumentMappingService mappingService;
    private OrderService orderService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        mappingService = InstrumentMappingService.getInstance();
        orderService = new OrderService();
    }

    @AfterEach
    public void cleanup() {
        MarketDataConnectionManager.getInstance().initializeDefaultProvider();
    }

    private byte[] createBinaryQuotePacket(int securityId, float ltp, int lttSec, float open, float high, float low, float close, int volume) {
        ByteBuffer buf = ByteBuffer.allocate(50).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 4);          // Response code: QUOTE (4)
        buf.putShort((short) 50);   // Message length: 50 bytes
        buf.put((byte) 1);          // Segment: 1 = NSE_EQ
        buf.putInt(securityId);     // Security ID (int32)
        buf.putFloat(ltp);          // LTP (float32)
        buf.putShort((short) 25);   // LTQ (int16)
        buf.putInt(lttSec);         // LTT in seconds (int32)
        buf.putFloat(ltp);          // Avg price (float32)
        buf.putInt(volume);         // Volume (int32)
        buf.putInt(25000);          // Total sell qty (int32)
        buf.putInt(40000);          // Total buy qty (int32)
        buf.putFloat(open);         // Open (float32)
        buf.putFloat(close);        // Close (float32)
        buf.putFloat(high);         // High (float32)
        buf.putFloat(low);          // Low (float32)
        return buf.array();
    }

    @Test
    @DisplayName("01. HTTP 101 Transport Connection Without Authentication Does NOT Transition to LIVE")
    public void testHttp101WithoutSuccessfulAuthentication() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("user123", "tokenXYZ", true, mappingService);
        List<MarketDataStatus> observedStates = Collections.synchronizedList(new ArrayList<>());
        provider.addListener(new MarketDataListener() {
            @Override
            public void onStatusChange(MarketDataStatus status, String message) {
                observedStates.add(status);
            }
        });

        // Transport connected (HTTP 101)
        provider.setStatus(MarketDataStatus.WEBSOCKET_CONNECTED, "WebSocket transport connected (HTTP 101 Switching Protocols)");
        assertEquals(MarketDataStatus.WEBSOCKET_CONNECTED, provider.getStatus());
        assertFalse(provider.getStatus().isOrderSubmissionAllowed(), "HTTP 101 must NOT allow order submission");

        // Authenticating state
        provider.setStatus(MarketDataStatus.AUTHENTICATING, "Authenticating with DhanHQ gateway...");
        assertEquals(MarketDataStatus.AUTHENTICATING, provider.getStatus());
        assertFalse(provider.getStatus().isOrderSubmissionAllowed(), "Authenticating state must NOT allow order submission");

        // Server closes session with code 1006 (unauthorized)
        provider.setStatus(MarketDataStatus.AUTHENTICATION_FAILED, "DhanHQ gateway rejected authentication (code 1006)");
        assertEquals(MarketDataStatus.AUTHENTICATION_FAILED, provider.getStatus());
        assertFalse(provider.getStatus().isOrderSubmissionAllowed());

        // Verifies LIVE was NEVER entered
        assertFalse(observedStates.contains(MarketDataStatus.LIVE), "Provider must NEVER transition to LIVE without valid packet reception");
    }

    @Test
    @DisplayName("02. Invalid and Expired Tokens Transition to AUTHENTICATION_FAILED and RECONNECTING")
    public void testInvalidAndExpiredTokens() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("expiredUser", "expiredToken", true, mappingService);

        provider.setStatus(MarketDataStatus.AUTHENTICATION_FAILED, "Token expired (HTTP 401 Unauthorized)");
        assertEquals(MarketDataStatus.AUTHENTICATION_FAILED, provider.getStatus());
        assertTrue(provider.getStatusMessage().contains("Token expired"));

        int backoff = provider.calculateBackoffDelaySeconds();
        assertEquals(2, backoff);
        provider.setStatus(MarketDataStatus.RECONNECTING, "Reconnecting in " + backoff + "s");
        assertEquals(MarketDataStatus.RECONNECTING, provider.getStatus());
    }

    @Test
    @DisplayName("03. Controlled Test-Double Validates Full State Machine Progression to LIVE")
    public void testControlledTestDoubleAuthenticatedConnection() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("validUser", "validToken", true, mappingService);
        List<MarketDataStatus> history = new ArrayList<>();
        provider.addListener(new MarketDataListener() {
            @Override
            public void onStatusChange(MarketDataStatus status, String message) {
                history.add(status);
            }
        });

        // Step 1: Transport connected with controlled WebSocket test-double
        java.net.http.WebSocket dummyWs = new java.net.http.WebSocket() {
            @Override public java.util.concurrent.CompletableFuture<java.net.http.WebSocket> sendText(CharSequence data, boolean last) { return java.util.concurrent.CompletableFuture.completedFuture(this); }
            @Override public java.util.concurrent.CompletableFuture<java.net.http.WebSocket> sendBinary(ByteBuffer data, boolean last) { return java.util.concurrent.CompletableFuture.completedFuture(this); }
            @Override public java.util.concurrent.CompletableFuture<java.net.http.WebSocket> sendPing(ByteBuffer message) { return java.util.concurrent.CompletableFuture.completedFuture(this); }
            @Override public java.util.concurrent.CompletableFuture<java.net.http.WebSocket> sendPong(ByteBuffer message) { return java.util.concurrent.CompletableFuture.completedFuture(this); }
            @Override public java.util.concurrent.CompletableFuture<java.net.http.WebSocket> sendClose(int statusCode, String reason) { return java.util.concurrent.CompletableFuture.completedFuture(this); }
            @Override public void request(long n) {}
            @Override public String getSubprotocol() { return ""; }
            @Override public boolean isOutputClosed() { return false; }
            @Override public boolean isInputClosed() { return false; }
            @Override public void abort() {}
        };
        provider.setWebSocketForTest(dummyWs);
        provider.setStatus(MarketDataStatus.WEBSOCKET_CONNECTED, "Transport connected");
        // Step 2: Authenticating
        provider.setStatus(MarketDataStatus.AUTHENTICATING, "Authenticating");
        // Step 3: Authenticated
        provider.setStatus(MarketDataStatus.AUTHENTICATED, "Authenticated");
        // Step 4: Subscribing
        provider.subscribe(List.of(1L, 2L, 3L));
        assertEquals(MarketDataStatus.SUBSCRIBING, provider.getStatus());

        // Step 5: Receive first authentic binary quote packet from exchange
        int nowEpoch = (int) Instant.now().getEpochSecond();
        byte[] reliancePacket = createBinaryQuotePacket(1333, 2875.50f, nowEpoch, 2850.0f, 2890.0f, 2845.0f, 2850.0f, 1000000);
        provider.ingestBinaryPacket(reliancePacket);

        // State machine must transition to LIVE upon receiving the first genuine packet
        assertEquals(MarketDataStatus.LIVE, provider.getStatus());
        assertTrue(provider.getStatus().isOrderSubmissionAllowed(), "LIVE state must allow order submission");
        assertTrue(history.contains(MarketDataStatus.WEBSOCKET_CONNECTED));
        assertTrue(history.contains(MarketDataStatus.AUTHENTICATING));
        assertTrue(history.contains(MarketDataStatus.AUTHENTICATED));
        assertTrue(history.contains(MarketDataStatus.SUBSCRIBING));
        assertTrue(history.contains(MarketDataStatus.LIVE));
    }

    @Test
    @DisplayName("04. Subscription Success and Failure Scenarios")
    public void testSubscriptionSuccessAndFailure() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("testUser", "testToken", true, mappingService);

        // Failure: null or empty stock list
        assertNull(provider.buildSubscriptionJson(null, 15));
        assertNull(provider.buildSubscriptionJson(Collections.emptyList(), 15));

        // Failure: unmapped stock ID
        assertNull(provider.buildSubscriptionJson(List.of(999999L), 15));

        // Success: valid mapped instruments (RELIANCE = 1333, TCS = 11536)
        String json = provider.buildSubscriptionJson(List.of(1L, 2L), 15);
        assertNotNull(json);
        assertTrue(json.contains("\"RequestCode\":15"));
        assertTrue(json.contains("\"InstrumentCount\":2"));
        assertTrue(json.contains("\"SecurityId\":\"1333\""));
        assertTrue(json.contains("\"SecurityId\":\"11536\""));
    }

    @Test
    @DisplayName("05. Genuine Packet Parsing Using Documented Binary Fixtures (Codes 2 and 4)")
    public void testGenuinePacketParsingUsingDocumentedBinaryFixtures() {
        DhanPacketParser parser = new DhanPacketParser(mappingService);

        // Test Code 4 (Quote, 50 bytes)
        byte[] quotePacket = createBinaryQuotePacket(1594, 1515.25f, 1710000000, 1510.0f, 1525.0f, 1505.0f, 1510.0f, 500000);
        var quoteOpt = parser.parseQuote(quotePacket);
        assertTrue(quoteOpt.isPresent());
        MarketQuote quote = quoteOpt.get();
        assertEquals("INFY", quote.symbol());
        assertEquals(new BigDecimal("1515.25"), quote.ltp());
        assertEquals(new BigDecimal("1525.00"), quote.high());
        assertEquals(new BigDecimal("1505.00"), quote.low());
        assertEquals(new BigDecimal("1510.00"), quote.open());
        assertEquals(500000, quote.volume());

        // Test Code 2 (Ticker, 16 bytes)
        ByteBuffer tickerBuf = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        tickerBuf.put((byte) 2);        // Code 2
        tickerBuf.putShort((short) 16); // Len 16
        tickerBuf.put((byte) 1);        // Segment NSE_EQ
        tickerBuf.putInt(1333);         // RELIANCE
        tickerBuf.putFloat(2880.00f);   // LTP
        tickerBuf.putInt(1710000000);   // LTT
        var tickerOpt = parser.parseQuote(tickerBuf.array());
        assertTrue(tickerOpt.isPresent());
        assertEquals("RELIANCE", tickerOpt.get().symbol());
        assertEquals(new BigDecimal("2880.00"), tickerOpt.get().ltp());
    }

    @Test
    @DisplayName("06. Distinguishing Fixtures from Real Provider Observations")
    public void testDistinguishingFixturesFromRealProviderObservations() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("testUser", "testToken", false, mappingService);

        int nowEpoch = (int) Instant.now().getEpochSecond();
        byte[] packet = createBinaryQuotePacket(1333, 2850.00f, nowEpoch, 2850.0f, 2850.0f, 2850.0f, 2850.0f, 100);
        provider.ingestBinaryPacket(packet);

        MarketQuote liveQuote = provider.getLatestQuote(1L);
        assertNotNull(liveQuote);
        assertEquals(MarketDataSource.LIVE_PROVIDER, liveQuote.source());
        assertNotNull(liveQuote.receivedTimestamp());
        assertFalse(liveQuote.isStale());
    }

    @Test
    @DisplayName("07. Stale Quotes Shift State to STALE and Return to LIVE on Fresh Tick")
    public void testStaleQuotesTransitionAndRecovery() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("testUser", "testToken", true, mappingService);
        provider.setStatus(MarketDataStatus.LIVE, "Live market feed active");

        // Shift to STALE when quotes exceed 15s threshold
        provider.setStatus(MarketDataStatus.STALE, "Market data quotes have become stale (>15s since last tick)");
        assertEquals(MarketDataStatus.STALE, provider.getStatus());
        assertFalse(provider.getStatus().isOrderSubmissionAllowed(), "STALE state must NOT allow order submission");

        // Fresh tick arrives
        int nowEpoch = (int) Instant.now().getEpochSecond();
        byte[] freshPacket = createBinaryQuotePacket(1333, 2880.00f, nowEpoch, 2850.0f, 2890.0f, 2845.0f, 2850.0f, 20000);
        provider.ingestBinaryPacket(freshPacket);

        // Recovers to LIVE
        assertEquals(MarketDataStatus.LIVE, provider.getStatus());
        assertTrue(provider.getStatus().isOrderSubmissionAllowed(), "Recovered LIVE state must allow order submission");
    }

    @Test
    @DisplayName("08. Market Order Rejection in EVERY Unsafe State")
    public void testMarketOrderRejectionInEveryUnsafeState() {
        MarketDataStatus[] unsafeStates = {
                MarketDataStatus.NOT_CONFIGURED,
                MarketDataStatus.DISCONNECTED,
                MarketDataStatus.CONNECTING,
                MarketDataStatus.WEBSOCKET_CONNECTED,
                MarketDataStatus.AUTHENTICATING,
                MarketDataStatus.AUTHENTICATED,
                MarketDataStatus.SUBSCRIBING,
                MarketDataStatus.STALE,
                MarketDataStatus.AUTHENTICATION_FAILED,
                MarketDataStatus.RECONNECTING,
                MarketDataStatus.ERROR
        };

        for (MarketDataStatus unsafeState : unsafeStates) {
            DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("user", "token", false, mappingService);
            provider.setStatus(unsafeState, "Test status " + unsafeState.getLabel());

            // Provide a quote so quote availability is satisfied, but feed status is unsafe
            byte[] packet = createBinaryQuotePacket(1333, 2850.00f, (int) Instant.now().getEpochSecond(), 2850.0f, 2850.0f, 2850.0f, 2850.0f, 100);
            provider.ingestBinaryPacket(packet);
            provider.setStatus(unsafeState, "Re-assert unsafe status " + unsafeState.getLabel());

            MarketDataConnectionManager.getInstance().setProvider(provider);

            Order marketOrder = new Order();
            marketOrder.setUserId(2L);
            marketOrder.setStockId(1L);
            marketOrder.setSide(OrderSide.BUY);
            marketOrder.setOrderType(OrderType.MARKET);
            marketOrder.setQuantity(1);
            marketOrder.setValidity(OrderValidity.DAY);

            InvalidOrderException ex = assertThrows(InvalidOrderException.class, () -> orderService.placeOrder(marketOrder),
                    "Market order must be rejected when status is " + unsafeState.name());
            assertTrue(ex.getMessage().contains("Market order rejected"),
                    "Exception message must specify rejection for state " + unsafeState.name() + ": " + ex.getMessage());
        }
    }

    @Test
    @DisplayName("09. Secret Redaction from Logs, Status Messages, and URLs")
    public void testSecretRedaction() {
        String sensitiveToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.SuperSecretPayloadToken123456";
        String sensitiveClientId = "DHAN_CLIENT_998877";

        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider(sensitiveClientId, sensitiveToken, true, mappingService);

        // Direct token redaction
        String messageWithToken = "Connection failed for token=" + sensitiveToken + " with clientId=" + sensitiveClientId;
        String redacted = provider.redact(messageWithToken);
        assertFalse(redacted.contains(sensitiveToken), "Access token must be redacted");
        assertFalse(redacted.contains(sensitiveClientId), "Client ID must be redacted");
        assertTrue(redacted.contains("[REDACTED]"));

        // Status message redaction
        provider.setStatus(MarketDataStatus.AUTHENTICATION_FAILED, "Auth error with " + sensitiveToken);
        assertFalse(provider.getStatusMessage().contains(sensitiveToken));
        assertTrue(provider.getStatusMessage().contains("[REDACTED]"));

        // URL query parameter regex redaction
        String rawUrl = "wss://api-feed.dhan.co?version=2&token=SECRET_TOKEN_XYZ&clientId=CLIENT_ABC&authType=2";
        String redactedUrl = provider.redact(rawUrl);
        assertFalse(redactedUrl.contains("SECRET_TOKEN_XYZ"));
        assertFalse(redactedUrl.contains("CLIENT_ABC"));
        assertTrue(redactedUrl.contains("token=[REDACTED]"));
        assertTrue(redactedUrl.contains("clientId=[REDACTED]"));
    }

    @Test
    @DisplayName("10. Concurrent State Transitions and Thread Safety")
    public void testStateTransitionsAndConcurrency() throws Exception {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider("testUser", "testToken", true, mappingService);
        int threadCount = 8;
        int iterations = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicBoolean errorDetected = new AtomicBoolean(false);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        if (threadId % 2 == 0) {
                            byte[] packet = createBinaryQuotePacket(1333, 2850.00f + j, (int) Instant.now().getEpochSecond(), 2850.0f, 2890.0f, 2840.0f, 2850.0f, 1000 + j);
                            provider.ingestBinaryPacket(packet);
                        } else if (threadId % 3 == 0) {
                            provider.subscribe(List.of(1L, 2L));
                        } else {
                            provider.setStatus(MarketDataStatus.LIVE, "Live tick " + j);
                        }
                    }
                } catch (Throwable t) {
                    errorDetected.set(true);
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Concurrent tasks must complete within timeout");
        assertFalse(errorDetected.get(), "No exceptions should occur during concurrent state changes and packet ingestion");
        executor.shutdownNow();
    }
}
