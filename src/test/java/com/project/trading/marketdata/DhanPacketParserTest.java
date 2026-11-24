package com.project.trading.marketdata;

import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDepth;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.dhan.DhanPacketParser;
import com.project.trading.marketdata.service.InstrumentMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DhanPacketParserTest {

    private DhanPacketParser parser;

    @BeforeEach
    public void setup() {
        parser = new DhanPacketParser(InstrumentMappingService.getInstance());
    }

    @Test
    public void testParseTickerPacket_Success() {
        // Ticker packet (Code 2, 16 bytes)
        // 1 byte code, 2 bytes len, 1 byte segment, 4 bytes secId, 4 bytes ltp, 4 bytes lttSec
        ByteBuffer buf = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 2);          // Response code TICKER
        buf.putShort((short) 16);   // Message length
        buf.put((byte) 1);          // Exchange segment (NSE_EQ)
        buf.putInt(1333);           // Security ID: RELIANCE (Stock ID 1)
        buf.putFloat(2850.50f);     // LTP
        buf.putInt(1710000000);     // LTT in seconds

        Optional<MarketQuote> quoteOpt = parser.parseQuote(buf.array());
        assertTrue(quoteOpt.isPresent(), "Ticker quote should be parsed successfully");

        MarketQuote quote = quoteOpt.get();
        assertEquals(1L, quote.stockId());
        assertEquals("RELIANCE", quote.symbol());
        assertEquals(MarketDataSource.LIVE_PROVIDER, quote.source());
        assertEquals(new BigDecimal("2850.50"), quote.ltp());
        assertEquals(new BigDecimal("2850.45"), quote.bidPrice());
        assertEquals(new BigDecimal("2850.55"), quote.askPrice());
        assertFalse(quote.isStale());
        assertEquals("VALID", quote.qualityStatus());
    }

    @Test
    public void testParseQuotePacket_Success() {
        // Quote packet (Code 4, 48+ bytes)
        ByteBuffer buf = ByteBuffer.allocate(50).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 4);          // Response code QUOTE
        buf.putShort((short) 50);   // Message length
        buf.put((byte) 1);          // Segment
        buf.putInt(11536);          // Security ID: TCS (Stock ID 2)
        buf.putFloat(3890.75f);     // LTP
        buf.putShort((short) 25);   // LTQ
        buf.putInt(1710000010);     // LTT seconds
        buf.putFloat(3885.00f);     // Avg price
        buf.putInt(1250000);        // Volume
        buf.putInt(55000);          // Total sell qty
        buf.putInt(85000);          // Total buy qty
        buf.putFloat(3850.00f);     // Open
        buf.putFloat(3840.00f);     // Close / PrevClose
        buf.putFloat(3910.00f);     // High
        buf.putFloat(3845.00f);     // Low

        Optional<MarketQuote> quoteOpt = parser.parseQuote(buf.array());
        assertTrue(quoteOpt.isPresent(), "Quote packet should be parsed successfully");

        MarketQuote quote = quoteOpt.get();
        assertEquals(2L, quote.stockId());
        assertEquals("TCS", quote.symbol());
        assertEquals(new BigDecimal("3890.75"), quote.ltp());
        assertEquals(new BigDecimal("3850.00"), quote.open());
        assertEquals(new BigDecimal("3910.00"), quote.high());
        assertEquals(new BigDecimal("3845.00"), quote.low());
        assertEquals(new BigDecimal("3840.00"), quote.prevClose());
        assertEquals(1250000L, quote.volume());
    }

    @Test
    public void testParseDepthPacket_Success() {
        // Full Depth packet (Code 8, 8 header + 8 ltp/ltt + 5*10 buy + 5*10 sell = 116 bytes)
        ByteBuffer buf = ByteBuffer.allocate(116).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 8);          // Response code FULL DEPTH
        buf.putShort((short) 116);  // Message length
        buf.put((byte) 1);          // Segment
        buf.putInt(1594);           // Security ID: INFY (Stock ID 3)
        buf.putFloat(1600.25f);     // LTP
        buf.putInt(1710000020);     // LTT seconds

        // 5 Buy bids
        for (int i = 0; i < 5; i++) {
            buf.putInt(500 + i * 50);             // Quantity
            buf.putShort((short) (10 + i));       // Orders count
            buf.putFloat(1600.20f - (i * 0.10f)); // Price
        }

        // 5 Sell asks
        for (int i = 0; i < 5; i++) {
            buf.putInt(400 + i * 40);             // Quantity
            buf.putShort((short) (8 + i));        // Orders count
            buf.putFloat(1600.30f + (i * 0.10f)); // Price
        }

        Optional<MarketDepth> depthOpt = parser.parseDepth(buf.array());
        assertTrue(depthOpt.isPresent(), "MarketDepth packet should be parsed successfully");

        MarketDepth depth = depthOpt.get();
        assertEquals(3L, depth.stockId());
        assertEquals("INFY", depth.symbol());
        assertEquals(5, depth.bids().size());
        assertEquals(5, depth.asks().size());

        assertEquals(new BigDecimal("1600.20"), depth.bids().get(0).price());
        assertEquals(500, depth.bids().get(0).quantity());
        assertEquals(new BigDecimal("1600.30"), depth.asks().get(0).price());
        assertEquals(400, depth.asks().get(0).quantity());
    }

    @Test
    public void testParseInvalidAndEdgeCases() {
        // Null data
        assertFalse(parser.parseQuote(null).isPresent());
        assertFalse(parser.parseDepth(null).isPresent());

        // Truncated data (< 8 bytes)
        assertFalse(parser.parseQuote(new byte[]{2, 16, 0}).isPresent());
        assertFalse(parser.parseDepth(new byte[]{8, 50, 0}).isPresent());

        // Unknown security ID
        ByteBuffer unknownBuf = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        unknownBuf.put((byte) 2);
        unknownBuf.putShort((short) 16);
        unknownBuf.put((byte) 1);
        unknownBuf.putInt(999999); // Unknown security ID
        unknownBuf.putFloat(100.0f);
        unknownBuf.putInt(1710000000);
        assertFalse(parser.parseQuote(unknownBuf.array()).isPresent());

        // Disconnect message (code 50)
        ByteBuffer discBuf = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        discBuf.put((byte) 50);
        discBuf.putShort((short) 16);
        discBuf.put((byte) 1);
        discBuf.putInt(1333);
        discBuf.putFloat(2850.0f);
        discBuf.putInt(1710000000);
        assertFalse(parser.parseQuote(discBuf.array()).isPresent());
    }
}
