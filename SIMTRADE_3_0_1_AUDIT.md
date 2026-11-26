# SIMTRADE 3.0.1 PRODUCTION READINESS AUDIT REPORT

**Date**: 2026-11-26  
**Auditor**: Lead Java Architect, Senior Trading Systems Engineer & QA Lead  
**Repository**: [https://github.com/kunal-gswm/simtrade](https://github.com/kunal-gswm/simtrade)  
**Target Branch**: `main`  
**Base Commit**: `9fb426be0f712210d149d0d29b5bf34ae99fe721`  
**Audit Status**: **AUDITED, REMEDIATED & VERIFIED**

---

## 1. Executive Summary

SimTrade 3.0.1 was subjected to an independent, rigorous production readiness audit evaluating its live-market-data architecture, paper order execution engine, financial accounting integrity, concurrency resilience, security posture, and responsive web user interface.

### Key Audit Highlights:
- **Baseline Integrity**: The project compiles cleanly and passes all unit and integration test suites against MySQL (`papertrade_test`).
- **Live Feed Status**: Marked honestly as **`NOT VERIFIED`** for live network connectivity because real live DhanHQ API credentials (`MARKET_DATA_CLIENT_ID`, `MARKET_DATA_ACCESS_TOKEN`) are not configured in the host environment. The platform safely defaults to its internal random-walk simulation stream and explicitly displays `SIMULATION LIVE` / `PAPER SIMULATION`. The binary packet parser was tested and **`PASS`** verified against documented DhanHQ v2 packet structures.
- **Order Engine**: Supports all required order types (`MARKET`, `LIMIT`, `SL`, `SL-M`, `BRACKET`). Trigger conditions, fill execution, and state transitions strictly follow institutional standards.
- **Bracket OCO Mutual Exclusion**: Verified under concurrent multi-threaded stress. A bracket order **cannot** execute both exit legs; canonical lock ordering on the parent order serializes evaluation and guarantees that when one leg fills, the sibling leg is atomically cancelled without deadlock or overselling.
- **Financial Invariants**: All cash balances, reservations, holdings, fees, and P&L calculations strictly use `BigDecimal` inside ACID database transactions with row-level locking (`SELECT ... FOR UPDATE`). No negative balances or double-spending occurred under concurrent load.
- **UI Responsiveness**: Tested across Desktop (`1440x900`), Tablet (`768x1024`), Mobile (`390x844`), and Small Mobile (`360x800`). Zero horizontal overflow, functioning hamburger drawer, and clean dark financial terminal design.

---

## 2. Findings Classification Scorecard

| Category | Component / Capability | Audit Status | Key Evidence / Notes |
| :--- | :--- | :---: | :--- |
| **Feed Architecture** | Provider-Neutral Abstraction (`MarketDataProvider`) | **PASS** | Decoupled streaming architecture with `MarketQuote` and `MarketDepth` models. |
| **Feed Architecture** | Live Network Connectivity (DhanHQ WebSocket) | **NOT VERIFIED** | Valid API credentials not configured in host environment. Connection safely remains in `NOT_CONFIGURED` without faking. |
| **Feed Architecture** | Binary Packet Parsing (`DhanPacketParser`) | **PASS** | Validated against Code 2 (Ticker), Code 4 (Quote), and Code 8 (Depth) binary structures. |
| **Feed Architecture** | Instrument Mapping (`InstrumentMappingService`) | **PASS** | 30 major NSE equities mapped between internal IDs, symbols, and Dhan security tokens. |
| **Feed Architecture** | Data Provenance & Source Labeling | **PASS** | Clear distinction between `LIVE_PROVIDER`, `SIMULATED_STREAM`, and `SYNTHETIC_BACKFILL`. |
| **Order Engine** | Market Orders (`MARKET`) | **PASS** | Immediate execution against prevailing quotes with 2 bps slippage and transaction costs. |
| **Order Engine** | Limit Orders (`LIMIT`) | **PASS** | Strict price priority: Buy <= Limit, Sell >= Limit. Orders remain open until matched. |
| **Order Engine** | Stop-Loss Orders (`SL`, `SL-M`) | **PASS** | Trigger monitoring in `TRIGGER_PENDING` state; converts to Limit/Market upon trigger breach. |
| **Order Engine** | Bracket Orders (`BRACKET`) | **PASS** | Atomically spawns child Target and Stop-Loss exit legs upon entry fill. |
| **Order Engine** | Bracket OCO Mutual Exclusion | **PASS** | Verified under simultaneous multi-threaded trigger; exactly one leg fills, sibling cancels. |
| **Financial Accounting** | Cash Reservations (Buy Orders) | **PASS** | Funds reserved upfront; committed on fill, released on cancel. Double spending blocked. |
| **Financial Accounting** | Holdings Reservations (Sell Orders) | **PASS** | Shares locked upfront; committed on fill, released on cancel. Overselling blocked. |
| **Financial Accounting** | Financial Precision (`BigDecimal`) | **PASS** | Scale 2/4 rounding `HALF_UP`. Zero floating-point arithmetic in accounting. |
| **Financial Accounting** | Realized & Unrealized P&L | **PASS** | FIFO/weighted-average cost basis accounting; fees factored into net realized P&L. |
| **Concurrency & Failure** | Out-of-Order & Duplicate Ticks | **PASS** | Timestamp sequencing checks reject stale or regressive ticks. |
| **Concurrency & Failure** | Engine Restart Recovery | **PASS** | Active orders persisted in MySQL are recovered and matched against incoming quotes. |
| **Security** | Secrets & Credentials | **PASS** | No API keys or passwords in Git history or source files; externalized to environment. |
| **Security** | SQL Injection Protection | **PASS** | 100% parameterized `PreparedStatement` across all DAOs; no dynamic SQL concatenation. |
| **Security** | Authentication & Authorization | **PASS** | `AuthFilter` and `SecurityService` protect trader and admin realms. CSRF filter active. |
| **UI Verification** | Responsive Viewports (1440, 768, 390, 360) | **PASS** | No horizontal scrolling (`scrollWidth == clientWidth`); responsive charts and KPI grids. |
| **UI Verification** | Order Placement & Cancellation Flow | **PASS** | Full lifecycle verified live: Order placed, margin locked, cancelled, margin released. |
| **Real Broker Execution** | Real Order Routing | **NOT APPLICABLE** | Platform is strictly paper-trading only. Real broker order execution is prohibited by design. |

---

## 3. Live Data Verification (Phase 2)

### 3.1 Authenticated Live Feed Status
- **Status**: **`NOT VERIFIED`**
- **Reason**: The environment does not contain active DhanHQ credentials (`MARKET_DATA_CLIENT_ID` and `MARKET_DATA_ACCESS_TOKEN` are empty, `MARKET_DATA_ENABLED` is not set).
- **Safe Fallback Behavior**: In compliance with system specifications, `DhanLiveMarketDataProvider` does **not** fake a live connection. When unconfigured, it transitions to `MarketDataStatus.NOT_CONFIGURED` with status message:
  ```
  DhanHQ live feed is NOT CONFIGURED. Set MARKET_DATA_CLIENT_ID, MARKET_DATA_ACCESS_TOKEN, and MARKET_DATA_ENABLED=true in environment to activate.
  ```
  The platform operates seamlessly on its institutional `SimulatorMarketDataProvider`.

### 3.2 Binary Packet Parser Verification (`DhanPacketParserTest`)
The parser implementation was audited and verified against the official DhanHQ WebSocket v2 binary protocol:
1. **Header Parsing (8 bytes)**: Correctly reads 1-byte response code, 2-byte message length (Little Endian), 1-byte exchange segment, and 4-byte security ID (Little Endian).
2. **Ticker Packet (Code 2, 16 bytes)**: Correctly extracts 32-bit float LTP and 32-bit int timestamp, normalizing float prices into `BigDecimal` with 2 decimal places.
3. **Quote Packet (Code 4, 48+ bytes)**: Accurately parses LTP, LTQ, LTT, average traded price, volume, total buy/sell quantities, open, close, high, and low.
4. **Full Depth Packet (Code 8, 116 bytes)**: Parses 5 Bid and 5 Ask levels (quantity, order count, and price) into `MarketDepth.DepthLevel`.
5. **Edge Cases**: Gracefully rejects null arrays, truncated packets (< 8 bytes), unknown security IDs, and disconnection packets (Code 50).

### 3.3 Data Provenance & Anti-Confusion Guarantee
Every quote and candle maintains an explicit `MarketDataSource` tag:
- `LIVE_PROVIDER`: Live authenticated exchange feed.
- `SIMULATED_STREAM`: Internal Random-walk simulation stream.
- `SYNTHETIC_BACKFILL`: Historical seed/backfill candles.

In the user interface, the top navigation bar displays `SIMULATION LIVE` with an active pulsing status dot. Chart headers explicitly state `PAPER SIMULATION · Asia/Kolkata (IST)` and `● SIMULATED DATA STREAM`. Simulated data cannot be mistaken for actual exchange data.

### 3.4 Production Activation Instructions for DhanHQ
To activate live market data in production:
1. Obtain an authorized DhanHQ API subscription and generate access tokens from the Dhan developer portal.
2. Configure the following environment variables:
   ```bash
   export MARKET_DATA_PROVIDER="DHAN"
   export MARKET_DATA_CLIENT_ID="<your_client_id>"
   export MARKET_DATA_ACCESS_TOKEN="<your_access_token>"
   export MARKET_DATA_ENABLED="true"
   ```
3. Restart the application server. `MarketDataConnectionManager` will initialize `DhanLiveMarketDataProvider` and connect to `wss://api-feed.dhan.co`.

---

## 4. Order Engine Audit (Phase 3)

### 4.1 Order Types & Matching Conditions
- **MARKET**:
  - Buy fills at current `Ask` + 2 bps slippage; Sell fills at current `Bid` - 2 bps slippage.
  - Full execution in a single atomic transaction.
- **LIMIT**:
  - Buy fills when `Ask <= LimitPrice` at `min(Ask, LimitPrice)`.
  - Sell fills when `Bid >= LimitPrice` at `max(Bid, LimitPrice)`.
  - Unmatched limit orders remain `OPEN` in the database.
- **STOP-LOSS (SL & SL-M)**:
  - Created in `TRIGGER_PENDING` status.
  - Buy triggers when `LTP >= TriggerPrice`; Sell triggers when `LTP <= TriggerPrice`.
  - SL-M converts immediately to a market fill; SL converts to a limit order.
- **BRACKET (OCO)**:
  - Initial entry order placed with defined `targetPrice` and `stopLossPrice`.
  - Upon entry fill, the engine atomically spawns two child exit orders:
    1. **Target Exit** (`TARGET_EXIT`, `TARGET` type, `TRIGGER_PENDING`).
    2. **Stop-Loss Exit** (`STOP_LOSS_EXIT`, `STOP_LOSS_MARKET` type, `TRIGGER_PENDING`).

### 4.2 State Machine Transitions (`OrderStateMachine`)
Legal transitions strictly enforced:
- `PENDING` -> `OPEN`, `TRIGGER_PENDING`, `PARTIALLY_FILLED`, `FILLED`, `REJECTED`, `CANCELLED`
- `TRIGGER_PENDING` -> `OPEN`, `PARTIALLY_FILLED`, `FILLED`, `CANCELLED`, `EXPIRED`
- `OPEN` -> `PARTIALLY_FILLED`, `FILLED`, `CANCELLED`, `EXPIRED`
- `PARTIALLY_FILLED` -> `FILLED`, `CANCELLED`, `EXPIRED`
- Terminal states (`FILLED`, `CANCELLED`, `REJECTED`, `EXPIRED`) permit **zero** subsequent transitions.

---

## 5. Financial Integrity Audit (Phase 4)

### 5.1 Two-Phase Reservation System
- **Cash Reservations (`reserved_funds`)**:
  - When placing a Buy order, `requiredReservation = estimatedTurnover * (isMarket ? 1.01 : 1.00)`.
  - Available Cash = `CashBalance - TotalReservedFunds`.
  - If `AvailableCash < requiredReservation`, order is immediately rejected with `InsufficientBalanceException`.
  - Cash balance is only debited upon execution; on cancellation, the reservation is released back to available cash.
- **Holdings Reservations (`reserved_holdings`)**:
  - When placing a Sell order, `availableShares = TotalQuantity - ReservedQuantity`.
  - If `availableShares < orderQuantity`, order is immediately rejected with `InsufficientHoldingsException`.
  - Holding balance is only decremented upon execution; on cancellation, the reservation is released.

### 5.2 Core Accounting Invariants
Verified across all portfolio calculations:
$$\text{Total Equity (Net Worth)} = \text{Cash Balance} + \text{Holdings Market Value}$$
$$\text{Available Cash} = \text{Cash Balance} - \text{Reserved Funds}$$
$$\text{Available Holdings} = \text{Total Holdings Quantity} - \text{Reserved Holdings Quantity}$$

All values are computed using `BigDecimal` with `RoundingMode.HALF_UP` to paice precision.

---

## 6. Concurrency & Failure Testing (Phase 5)

A dedicated test suite (`FailureAndConcurrencyAuditTest.java`) was developed to verify system resilience:

1. **Duplicate & Out-of-Order Tick Protection (`testDuplicateAndOutOfOrderTicksRejected`)**:
   - Sent quote at timestamp $T = 100$.
   - Delivered out-of-order quote at $T = 90$. The engine detected regressive timestamp and rejected it.
   - Delivered newer quote at $T = 110$. Successfully accepted and processed.
2. **Bracket Exit Mutual Exclusion (`testConcurrentBracketExitTriggers_MutualExclusion`)**:
   - Simulated extreme market volatility where both Target and Stop-Loss conditions were met simultaneously.
   - Dispatched two concurrent threads evaluating the opposing exit legs.
   - **Result**: Exactly **one** exit order filled (`FILLED`), while the sibling order was atomically cancelled (`CANCELLED`). User holdings closed to exactly 0 shares with zero double-selling.
3. **Restart Recovery (`testRestartRecovery_PendingOrdersProcessedAfterRestart`)**:
   - Placed Limit order in `OPEN` state. Terminated `PaperExecutionEngine`.
   - Started a new engine instance. Ingested matching quote.
   - **Result**: Recovered the resting order from MySQL and completed execution cleanly.
4. **Concurrent Multi-Threaded Order Placement (`testConcurrentOrderPlacements_DoubleSpendingProtection`)**:
   - 10 threads concurrently attempted to place orders against an account with balance sufficient for only 1 order.
   - **Result**: Exactly 1 order succeeded; 9 failed with `InsufficientBalanceException`. Available cash remained non-negative (`₹6,600.00 >= 0`).

---

## 7. Security Audit (Phase 7)

- **SQL Injection**: Comprehensive audit confirmed 100% of DAO queries utilize parameterized `PreparedStatement` with bind variables (`?`). Zero dynamic SQL string concatenation found.
- **Credentials & Secrets**:
  - No plaintext passwords, API keys, or JWT tokens are stored in Git history or source files.
  - `src/main/resources/db.properties` and `src/test/resources/db.properties` are listed in `.gitignore` and excluded from repository tracking.
  - Database passwords and live feed tokens are consumed via environment variables.
- **Authentication & Realm Isolation**:
  - `AuthFilter` protects all protected servlet paths (`/app/*`, `/admin/*`, `/dashboard`, `/trade`).
  - Vaadin views enforce `SecurityService.getAuthenticatedUser()`. Unauthenticated requests redirect to `/login`.
  - Role-based access control enforces `Role.ADMIN` for administrative endpoints.
- **User Ownership Validation**:
  - `OrderService.cancelOrder(orderId, userId)` strictly validates `order.getUserId() == userId`, preventing unauthorized cancellation of peer orders.
- **CSRF Protection**:
  - `CsrfFilter` generates and verifies cryptographic session tokens for state-altering HTTP requests.

---

## 8. UI Verification (Phase 8)

Audited via headless browser across multiple viewports on running server (`http://localhost:8081/paper-trading`):

| Viewport | Device Class | Bounding Width | Scrollbar Status | Layout & Functionality Findings |
| :---: | :---: | :---: | :---: | :--- |
| **1440x900** | Desktop | 1440px | None | Fixed 256px sidebar, collapse button expands content to 1440px. Interactive multi-timeframe candlestick chart (1m, 5m, 15m, 1h, 1d). Order entry and cancellation completed smoothly. |
| **768x1024** | Tablet | 768px | None | Sidebar collapses to drawer with hamburger toggle. 6 KPI cards wrap into clean 2x3 grid. Backdrop blur on drawer open. |
| **390x844** | Mobile | 390.4px | None | Zero horizontal scrollbar (`scrollWidth == clientWidth`). Drawer opens as fixed 280px overlay. Close button and backdrop tap dismiss drawer. |
| **360x800** | Small Mobile | 360px | None | Zero horizontal scrollbar. Single-column stacked KPI cards. Clean font scaling and no text overlap. |

---

## 9. Defects Identified & Remediations Applied

### Defect 1: Sibling Exit Deadlock Under Simultaneous Execution
- **Root Cause**: When both legs of a bracket order were triggered simultaneously by concurrent threads, Thread 1 locked Target order and attempted to cancel Stop-Loss order, while Thread 2 locked Stop-Loss order and attempted to cancel Target order, causing an inverse lock acquisition order in MySQL.
- **Remediation**: Implemented **canonical lock ordering** in `PaperExecutionEngine.evaluateSingleOrder`. Before evaluating any child bracket order, the engine locks `parentOrderId` upfront (`FOR UPDATE`). This serializes execution across sibling legs, deterministically preventing deadlocks.
- **Regression Test**: Verified via `FailureAndConcurrencyAuditTest.testConcurrentBracketExitTriggers_MutualExclusion` (4/4 passed).

### Defect 2: Icon Button Hit-Testing Interference in Automated Testing
- **Root Cause**: Inner `<svg>` container within `<vaadin-button class="st-drawer-close-btn">` lacked inline `pointer-events: none`, causing strict point actionability checks to report `no_own_hit_point`.
- **Remediation**: Added `.set("pointer-events", "none")` directly to the `Span` container in `MainLayout.createSvgIcon`, ensuring child SVGs pass pointer events directly to the button host.
- **Regression Test**: Verified via automated browser subagent.

### Defect 3: Relative Stylesheet 404 Resolution
- **Root Cause**: Browser requested `/paper-trading/VAADIN/styles/simtrade.css` when resolving relative `@import` from theme stylesheet.
- **Remediation**: Added static stylesheet asset at `src/main/webapp/VAADIN/styles/simtrade.css` and cleaned theme references.
- **Regression Test**: Verified HTTP 200 on `/paper-trading/VAADIN/styles/simtrade.css`.

---

## 10. Automated Test Results & Build Verification

- **Total Unit & Integration Tests**: **118**
- **Test Failures**: **0**
- **Test Errors**: **0**
- **Skipped Tests**: **0**
- **Test Execution Time**: ~57.7 seconds
- **Production Package Build**: `mvn clean package -Pproduction` -> **BUILD SUCCESS** (`paper-trading.war` generated).

---

## 11. Final Assessment

SimTrade 3.0.1 is **READY** for institutional paper trading with simulated Indian market data. Live data abstractions, packet parsers, order matching, and risk management are fully implemented and verified. Live DhanHQ feed connectivity is marked **NOT VERIFIED** solely due to absence of production API keys and can be activated immediately upon key provisioning without code modification.
