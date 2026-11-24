# SimTrade 3.0 — Technical Implementation Report
**Institutional Live Market Data & Paper Execution Engine Upgrade**

* **Repository:** `https://github.com/kunal-gswm/simtrade`
* **Branch:** `upgrade/simtrade-2.0-audit`
* **Context Path:** `http://localhost:8081/paper-trading`
* **Status:** Verified, Tested, and Production Packaged

---

## 1. Executive Summary

SimTrade 3.0 transforms the platform into an institutional-grade **live-market-data paper trading system**. It decouples market data ingestion from simulated matching and order execution:

* **Market Data Ingestion:** Authorized provider abstraction consuming live exchange data (implemented via DhanHQ WebSocket Feed v2 binary packet protocol) with seamless, explicit fallback to an internal realistic random walk generator when credentials are absent (`NOT_CONFIGURED` status).
* **Paper Execution Only:** Absolute separation from live brokerages. Simulated orders are matched in an internal transaction-safe execution engine (`PaperExecutionEngine`) with row-level pessimistic locking (`SELECT FOR UPDATE`), realistic bid/ask spreads, slippage modeling (2 bps), and statutory brokerage/tax deductions.
* **Capital Protection & Reservation Model:** Transaction-level reservations prevent double-spending (`InsufficientBalanceException`) and overselling (`InsufficientHoldingsException`). Total cash and net equity remain reconciled while available trading capital accurately reflects open commitments.
* **Full Order Domain:** Support for 6 order types (`MARKET`, `LIMIT`, `STOP_LOSS_MARKET`, `STOP_LOSS_LIMIT`, `TARGET`, `BRACKET`), both sides (`BUY`, `SELL`), and validities (`DAY`, `GTC`, `IOC`), featuring atomic One-Cancels-the-Other (OCO) bracket exits.
* **Responsive UI & Design System:** Full compliance with the Warm Graphite, Copper, and Jade design system across Desktop (1440px), Tablet (768px), and Mobile (390px/360px) viewports with zero horizontal overflow or clipping.

---

## 2. Architecture & Data Flow

```
+-----------------------------------------------------------------------------------+
|                           MARKET DATA INGESTION ENGINE                            |
|                                                                                   |
|  +-----------------------------+         +-------------------------------------+  |
|  | DhanLiveMarketDataProvider  |         |     SimulatorMarketDataProvider     |  |
|  | (DhanHQ WebSocket Feed v2)  |         |   (Calibrated Random Walk + Spread) |  |
|  +--------------+--------------+         +------------------+------------------+  |
|                 | (Binary Packet Decode)                    |                     |
|                 +-------------------> [ Normalized MarketQuote ] <----------------+
+-----------------------------------------------|-----------------------------------+
                                                |
                                                v
               +------------------------------------------------+
               |          MarketDataConnectionManager           |
               |       (Global Quote & Depth Event Bus)         |
               +-------+--------------------+-------------------+
                       |                    |
        +--------------+                    +---------------+
        v                                                   v
+-------------------------------+         +----------------------------------+
|         CandleEngine          |         |       PaperExecutionEngine       |
| (1m, 5m, 15m, 1h, 1d candles) |         | - Evaluates Active Orders        |
| - Anchored at 09:15 IST       |         | - Row Locks (SELECT FOR UPDATE)  |
| - MarketSessionCalendar       |         | - Slippage (2 bps) & Fees Deduct |
+---------------+---------------+         | - Bracket Child Activation & OCO |
                |                         +-----------------+----------------+
                v                                           |
+-------------------------------+                           v
|   MarketDataBroadcaster       |         +----------------------------------+
| (Vaadin Web Push to UI views) |         |      Transactional Database      |
+-------------------------------+         | - orders, order_events, fills    |
                                          | - reserved_funds & holdings      |
                                          +----------------------------------+
```

---

## 3. Market Data Architecture & Provider Abstraction

### 3.1 Domain Models
* **`MarketQuote`:** Immutable Java record capturing `stockId`, `symbol`, `exchange`, `providerSecurityId`, `source` (`LIVE_PROVIDER` or `SIMULATED_STREAM`), `exchangeTimestamp`, `receivedTimestamp`, `ltp`, `prevClose`, `open`, `high`, `low`, `volume`, `bidPrice`, `bidQty`, `askPrice`, `askQty`, `isStale`, and `qualityStatus`. All monetary prices are normalized into `BigDecimal` with 2 decimal places (`paise`) using `RoundingMode.HALF_UP`.
* **`MarketDepth`:** L2 top-of-book order book with up to 5 levels of bids and asks.
* **`MarketDataStatus`:** `CONNECTED`, `CONNECTING`, `DISCONNECTED`, `RECONNECTING`, `STALE`, `ERROR`, `NOT_CONFIGURED`.

### 3.2 DhanHQ WebSocket Feed v2 (`DhanPacketParser` & `DhanLiveMarketDataProvider`)
* **Binary Protocol Decoding:** DhanHQ transmits binary little-endian packets:
  * **Header (8 bytes):** `responseCode` (1 byte), `messageLength` (2 bytes short), `exchangeSegment` (1 byte), `securityId` (4 bytes int).
  * **Ticker Packet (Code 2, 16 bytes):** Extracts LTP (32-bit float) and LTT timestamp; generates top-of-book spread estimate.
  * **Quote Packet (Code 4, 50 bytes):** Extracts LTP, LTQ, LTT, average price, day volume, total buy/sell quantities, open, close, high, low.
  * **Depth Packet (Code 8, 184 bytes):** Extracts 5-level buy and sell order books.
* **Security & Configuration:**
  * Provider requires `MARKET_DATA_CLIENT_ID` and `MARKET_DATA_ACCESS_TOKEN`.
  * If credentials are unset or blank, status transitions to `NOT_CONFIGURED` without masking or throwing runtime exceptions.
  * Secrets are never logged, committed to source control, or sent to client browsers.

### 3.3 Simulator Provider Fallback
* When live provider is disabled or not configured, the platform activates `SimulatorMarketDataProvider`, computing high-frequency ticks with realistic bid/ask spreads anchored to historical closing prices.

---

## 4. Full Paper Order Domain & Execution Engine

### 4.1 Order Types & State Machine
1. **`MARKET`:** Immediate fill at current best ask (for BUY) or best bid (for SELL) with simulated slippage.
2. **`LIMIT`:** Fills when market ask $\le$ limit price (BUY) or market bid $\ge$ limit price (SELL).
3. **`STOP_LOSS_MARKET (SL-M)`:** Initiates in `TRIGGER_PENDING`. When market price breaches `triggerPrice`, converts to market order and fills.
4. **`STOP_LOSS_LIMIT (SL)`:** Initiates in `TRIGGER_PENDING`. When market price breaches `triggerPrice`, transitions to `OPEN` limit order.
5. **`TARGET`:** Take-profit order executing when price reaches profit target.
6. **`BRACKET`:** Multi-leg risk-managed order. Entry fill automatically spawns:
   * Leg 1: `TARGET_EXIT` (Target take-profit)
   * Leg 2: `STOP_LOSS_EXIT` (Stop-loss market exit)
   * Atomic OCO (One-Cancels-the-Other): When either exit fills, the sibling exit is immediately and atomically cancelled.

### 4.2 State Machine Transition Graph
```
           +-----------+
           |  PENDING  +--------------------+--------------------+
           +-----+-----+                    |                    |
                 |                          v                    v
                 |                   +-------------+      +------------+
                 +------------------>|  REJECTED   |      | CANCELLED  |
                 |                   +-------------+      +------------+
                 v                                               ^
        +-----------------+                                      |
        | TRIGGER_PENDING +--------------------------------------+
        +--------+--------+                                      |
                 | (Trigger Met)                                 |
                 v                                               |
             +---+---+                                           |
             | OPEN  +-------------------------------------------+
             +---+---+                                           |
                 |                                               |
                 v (Fill)                                        |
       +------------------+                                      |
       | PARTIALLY_FILLED +--------------------------------------+
       +---------+--------+
                 | (Completed)
                 v
            +----+----+
            | FILLED  |  (Terminal State)
            +---------+
```

### 4.3 Realistic Cost & Execution Model (`SimulationCostModel`)
* **Slippage Model:** 2 basis points ($0.02\%$) applied unfavorably to simulated market executions:
  $$\text{Buy Execution Price} = \text{Ask} \times 1.0002$$
  $$\text{Sell Execution Price} = \text{Bid} \times 0.9998$$
* **Brokerage Model:** $\min(₹20.00, 0.03\% \times \text{Turnover})$ per executed leg.
* **Statutory Taxes & Charges:** $0.00345\%$ combined estimate covering STT, exchange turnover fees, and GST.

---

## 5. Capital Protection & Reservation Accounting

To guarantee zero double-spending and zero overselling across concurrent operations:

### 5.1 Accounting Invariants
$$\text{Net Worth (Total Equity)} = \text{Cash Balance} + \sum (\text{Holding Quantity} \times \text{Current Market Price})$$
$$\text{Available Buying Power} = \text{Cash Balance} - \sum \text{Active Reserved Funds}$$
$$\text{Available Quantity to Sell} = \text{Held Quantity} - \sum \text{Active Reserved Holdings}$$

### 5.2 Reservation Lifecycle
1. **Order Placement:**
   * BUY: Validates `availableCash >= estimatedTurnover * 1.01` (1% buffer for market volatility). Inserts row into `reserved_funds` with status `RESERVED`.
   * SELL: Validates `availableQuantity >= orderQuantity`. Inserts row into `reserved_holdings` with status `RESERVED`.
2. **Order Execution:**
   * Settles actual transaction cash and shares.
   * Updates reservation record status to `COMMITTED`.
3. **Order Cancellation / Expiry:**
   * Updates reservation record status to `RELEASED`.
   * Immediately restores available buying power and available share inventory.

---

## 6. Vaadin User Interface Enhancements

### 6.1 Orders View (`/ui/orders`)
* High-density professional trading terminal table showing active and past orders.
* **Top Metric Cards:** `Open & Pending Orders`, `Executed Fills`, `Locked Margin (Reserved Funds)`, and `Total Order Volume`.
* **Filter Tabs:** `All Orders`, `Open & Pending`, `Executed`, `Cancelled / Rejected`.
* **Action Drawer:** Click `Details` on any order to inspect execution fills, slippage breakdown, simulated brokerage, and complete state machine audit event history.

### 6.2 Market View (`/ui/market`)
* **Real-time Depth & Prices:** Grid includes live `Bid / Ask` spread and `Last Traded Price`.
* **Dynamic Order Entry Modal:**
  * Supports `Market`, `Limit`, `SL-M`, `SL`, `Target`, and `Bracket`.
  * Dynamic field visibility based on chosen order type (Trigger Price, Limit Price, Target Price, Stop Loss Price).
  * Real-time `Estimated Order Value` and `Required Margin` calculation updating as user edits quantity and price.
  * Buy and Sell mode toggle with color-coded buttons and warning callouts.

### 6.3 Portfolio View (`/ui/portfolio`)
* **Holdings Table:** Clear distinction between `Avail / Total Shares` preventing accidental overselling.
* **Cash Card:** Displays `Available Buying Power` alongside total settled cash balance and `Locked Margin` callout.
* **Capital Allocation Bar:** Visual proportion of cash reserve vs equity allocation.

### 6.4 Main Layout & Navigation
* **Navbar Feed Pill:** Dynamic status badge indicating connection state (`● SIMULATION LIVE`, `● LIVE: DHAN`, or `● FEED NOT CONFIGURED`).
* **Sidebar Link:** New `Orders` navigation link with custom icon.

---

## 7. Verification & Quality Assurance

### 7.1 Automated Test Suite
* **114 Total Automated Tests:** 100% passing rate, 0 failures, 0 errors, 0 skipped.
* **Strict Database Isolation:** Verified via `DatabaseIsolationTest`. Tests run strictly against `papertrade_test` and never touch development data.
* **Key Test Coverage:**
  * `DhanPacketParserTest`: Binary packet decoding for Ticker (code 2), Quote (code 4), and Depth (code 8); LE float-to-BigDecimal normalization.
  * `OrderStateMachineTest`: All legal and illegal state transitions, same-state idempotency, terminal state immutability.
  * `OrderServiceTest`: Limit buy fund reservation, double-spending prevention, limit sell holding reservation, overselling prevention, risk limit enforcement ($₹50\text{L}$ cap), atomic cancelation.
  * `PaperExecutionEngineTest`: Limit order price matching, SL-M trigger breach execution, bracket order execution with atomic OCO child cancellation, slippage and fee deduction.
  * `PortfolioAccountingTest`: Equity and cash reservation invariants.

### 7.2 Production Build
* Maven production packaging executed cleanly:
  ```bash
  mvn clean package -Pproduction
  ```
  Result: `BUILD SUCCESS`, generating deployment artifact `target/paper-trading.war`.

### 7.3 Multi-Viewport Browser Verification
Verified on Apache Tomcat 10.1.60 (`http://localhost:8081/paper-trading`):
* **Desktop (1440x900):** Clean layout, sidebar expanded, charts and grids fully accessible without clipping.
* **Mobile (390x844 & 360x800):** Responsive sidebar collapse, responsive card wrap, horizontal scrolling on high-density financial tables, zero overflow.

---

## 8. Git Commit Log

* **Branch:** `upgrade/simtrade-2.0-audit`
* **Commit:** `feat: implement SimTrade 3.0 live market data and paper execution`
* **Changes Committed:**
  * Complete market data abstraction & DhanHQ WebSocket provider
  * Complete paper order domain, state machine, DAOs, and execution engine
  * Transaction-safe fund and holding reservation services
  * Upgraded modern Vaadin views (`OrdersView`, `MarketView`, `PortfolioView`, `DashboardView`, `MainLayout`)
  * Full automated test suite (114 tests) and production documentation
