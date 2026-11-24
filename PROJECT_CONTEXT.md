# SimTrade 2.0 — Comprehensive Project Context & Engineering Review

> **Context Document for AI Assistants (ChatGPT, Claude, etc.) & Engineering Onboarding**  
> **Last Updated:** November 2026  
> **Repository:** `https://github.com/kunal-gswm/simtrade`  
> **Active Branch:** `upgrade/simtrade-2.0-audit`  
> **Latest Verified Commit:** `d4693f1` (*fix: align OHLCV data with Indian market sessions*)  
> **Build Status:** 88/88 Tests Passing · Production Build Verified (`mvn clean package -Pproduction`)

---

## 1. Executive Summary

**SimTrade 2.0** is an institutional-grade, full-stack paper trading platform simulating Indian equities markets (NSE India). Built with modern Java enterprise technologies, it provides a realistic, risk-free environment for simulated trading, portfolio tracking, real-time market data streaming, and interactive financial charting.

### Core Technology Stack
* **Language & Runtime:** Java 17 (LTS)
* **Web UI Framework:** Vaadin Flow 24.3 (Reactive, Component-based Java UI with Web Components)
* **Servlet & Web Layer:** Jakarta Servlet 6.0, JSP & JSTL 3.0 (Dual-stack support)
* **Database & Persistence:** MySQL 8.0 (InnoDB engine, raw JDBC with HikariCP 5.1 connection pooling)
* **Frontend & Charting:** TradingView Lightweight Charts (v4.1.3), custom CSS design system, Vite frontend bundler
* **Build System:** Apache Maven 3.9
* **Application Server:** Apache Tomcat 10.1 (Jakarta EE 10 compatible)
* **Application Context Path:** `http://localhost:8081/paper-trading` (Vaadin UI at `/ui/*`)

---

## 2. Strict Architectural Invariants & Tenets

Any AI assistant or developer working on SimTrade must strictly adhere to these rules:

1. **Zero Real Money / Simulated Only:** Pure paper-trading simulator. No external payment gateways or broker APIs.
2. **Financial Precision:** All currency amounts, share quantities, prices, values, and P&L calculations **MUST** use `java.math.BigDecimal` with scale 2 and `RoundingMode.HALF_UP`. **Never** use `float` or `double` in calculation or persistence paths.
3. **Database as Single Source of Truth:** All UI views (Vaadin Flow and legacy JSP/Servlets) share identical Service and DAO layers backed by MySQL. No in-memory collections or HTTP session maps for portfolio or trade state.
4. **Isolated Test Database:** Unit and integration tests run strictly against `papertrade_test` via `src/test/resources/db.properties`. Tests must never touch the development database (`papertrade`).
5. **ACID Transaction Management:** Database transactions are managed explicitly in the Service layer (`c.setAutoCommit(false)`, `c.commit()`, `c.rollback()`) using try-with-resources. Order placement uses row-level locking (`SELECT ... FOR UPDATE`) on user balance and holdings to prevent race conditions.
6. **Indian Market Session Rules:** Market operates on **Asia/Kolkata (IST)** timezone:
   * Regular Trading Hours: **09:15 to 15:30 IST**, Monday through Friday.
   * Session calendar enforces official NSE market holidays (2024–2026).
   * Candle intervals anchor to 09:15 IST (e.g., 1-hour candles open at 09:15, 10:15, 11:15, 12:15, 13:15, 14:15, 15:15).
7. **Design System ("Warm Graphite + Copper + Jade"):**
   * Background Surface: `#141419` / Card Surface: `#1a1a22` / Border: `#2a2a36`
   * Accents: Copper `#d97736` (Brand/Action), Jade `#10b981` (Gains/Profits), Ruby `#ef4444` (Losses)
   * High-contrast, typography-first institutional trading layout.

---

## 3. System Evolution & Completed Milestones

### Phase 1: Financial Precision, Security & Concurrency
* Replaced primitive floating-point math with `BigDecimal` throughout `PnLCalculator`, `TradingService`, `PortfolioService`, and DAOs.
* Implemented `HikariDataSource` connection pool (`DBConnection.java`) with graceful lifecycle shutdown in `AppContextListener`.
* Added PBKDF2 with HMAC-SHA256 password hashing in `PasswordUtil.java`.
* Enforced CSRF synchronizer tokens (`CsrfFilter.java`) and strict role-based authentication (`AuthFilter.java`).
* Separated development (`papertrade`) and automated testing (`papertrade_test`) databases.

### Phase 2 & 2.1: Institutional UI & Universal Responsive Sidebar
* Redesigned the entire UI using the Warm Graphite, Copper, and Jade institutional palette (`simtrade.css`).
* Re-architected `MainLayout.java` and `AdminLayout.java` based on Vaadin `AppLayout`.
* Built universal responsive sidebar controls:
  * Desktop (width > 1024px): Collapse/expand sidebar toggle in navbar, expanding main content to 100% full-screen width.
  * Mobile/Tablet (width ≤ 1024px): Zero-offset drawer overlay with slide-in animation, hamburger toggle, and drawer collapse button `✕`.
  * Verified across 320px, 360px, 390px, 768px, 1024px, 1440px, and 1920px viewports with zero horizontal overflow.
* Integrated real-time WebSocket/UI Push updates (`@Push(PushMode.AUTOMATIC)`) via `MarketDataBroadcaster.java`.

### Phase 3: Persistent OHLCV Market Data & Interactive Charts (`commit 42a2955`)
* Added persistent `candles` table supporting multi-timeframe data (`1m`, `5m`, `15m`, `1h`, `1d`) with composite unique index `(stock_id, timeframe, candle_start)`.
* Created `CandleEngine.java` to aggregate live price simulator ticks into active candles in memory and persist them periodically.
* Integrated **TradingView Lightweight Charts** via client-side web component `trading-chart.js` and Java component `TradingChartView.java`.
* Supported dynamic timeframe switching, crosshair tooltips, live candle ticking, and volume histograms.

### Phase 3.1: Indian Market Session Alignment & Data Integrity (`commit d4693f1`)
* **`MarketSessionCalendar.java`:** Formalized Indian equities calendar in `Asia/Kolkata` with regular session (09:15–15:30 IST), weekday filtering, official NSE holidays, and weekend roll-over logic.
* **Mathematical Multi-Timeframe Reconciliation:** Built synthetic backfill in `CandleService.java` that generates 1,875 base 1-minute candles across 5 trading days, and mathematically derives 5m, 15m, 1h, and 1d bars directly from 1m base candles (100% exact high/low/volume reconciliation).
* **Cold Restart Recovery:** `CandleEngine.recoverActiveCandles()` re-loads unclosed candles from MySQL upon application boot, ensuring zero volume loss or bar corruption across restarts.
* **Data Provenance:** Tagged candles with `source` (`SYNTHETIC_BACKFILL`, `SIMULATED_STREAM`, `EXCHANGE_IMPORT`) with UI indicators displaying `[PAPER SIMULATION]` and `Asia/Kolkata (IST)` badges.
* **Database Upsert Performance:** Added transaction-wrapped batch upsert in `CandleDAO` (`c.setAutoCommit(false)` / `c.commit()`), delivering 4.3ms mean latency across 12,695 candle queries.

---

## 4. Codebase Directory Structure

```text
simtrade/
├── db/
│   └── schema.sql                 # Complete MySQL 8.0 DDL (tables, indexes, check constraints)
├── frontend/
│   ├── src/components/
│   │   └── trading-chart.js       # TradingView Lightweight Charts custom element
│   └── styles/
│       └── simtrade.css           # Institutional Warm Graphite + Copper design system
├── src/main/java/com/project/trading/
│   ├── dao/                       # Raw JDBC Data Access Objects
│   │   ├── CandleDAO.java         # OHLCV candle persistence & batch upsert
│   │   ├── HoldingDAO.java        # User portfolio holdings queries
│   │   ├── StockDAO.java          # Stock symbols, live prices, metadata
│   │   ├── TradeDAO.java          # Trade execution logs
│   │   ├── UserDAO.java           # Authentication, credentials, cash balance
│   │   └── StatsDAO.java          # Admin analytics & platform stats
│   ├── exception/                 # Typed domain exceptions (InsufficientBalance, etc.)
│   ├── filter/                    # AuthFilter, CsrfFilter, EncodingFilter
│   ├── listener/                  # AppContextListener (lifecycle, HikariCP shutdown)
│   ├── model/                     # Core Domain POJOs
│   │   ├── Candle.java            # OHLCV candle model with CandleSource enum
│   │   ├── MarketSessionCalendar.java # NSE trading hours, holidays, session math
│   │   ├── Timeframe.java         # 1m, 5m, 15m, 1h, 1d enum with session start helpers
│   │   ├── Stock.java, Trade.java, Holding.java, User.java, Portfolio.java
│   ├── service/                   # Transactional Business Services
│   │   ├── CandleEngine.java      # Real-time tick-to-candle accumulator & periodic flush
│   │   ├── CandleService.java     # Historical data retrieval & synthetic seeding
│   │   ├── TradingService.java    # ACID buy/sell execution with row-level locks
│   │   ├── PortfolioService.java  # Real-time Net Worth & P&L calculation
│   │   ├── StockService.java      # Stock catalog management
│   │   ├── UserService.java       # User registration & password management
│   │   └── PnLCalculator.java     # BigDecimal financial calculations
│   ├── simulator/                 # Background Market Simulators
│   │   ├── PriceSimulator.java    # Random-walk tick generator broadcasting to UI & CandleEngine
│   │   └── MarketDataBroadcaster.java # Vaadin Push broadcaster to UI views
│   ├── ui/                        # Vaadin Flow 24 Reactive Web Views
│   │   ├── components/
│   │   │   └── TradingChartView.java # Server-side component wrapping trading-chart.js
│   │   ├── views/
│   │   │   ├── MainLayout.java    # Universal responsive app shell & navigation drawer
│   │   │   ├── DashboardView.java # Executive KPI cards, portfolio summary, quick trade
│   │   │   ├── MarketView.java    # 100-stock grid, interactive chart, order placement modal
│   │   │   ├── PortfolioView.java # Current positions table, unrealized P&L, square-off
│   │   │   ├── HistoryView.java   # Trade log table with pagination
│   │   │   ├── ProfileView.java   # Account details & password change
│   │   │   └── LoginView.java     # Terminal-styled authentication view
│   │   └── admin/                 # Dedicated admin layout and views
│   └── util/                      # DBConnection (HikariCP), PasswordUtil, Validator
└── src/test/java/                 # 88 Automated Unit & Integration Tests (papertrade_test)
```

---

## 5. Database Schema Overview (MySQL 8.0)

| Table | Primary Key | Key Columns / Constraints | Purpose |
|---|---|---|---|
| `users` | `id` (BIGINT AUTO_INCREMENT) | `username` (UNIQUE), `password_hash`, `balance` (DECIMAL 12,2), `role` | User accounts and virtual trading cash |
| `stocks` | `id` (BIGINT AUTO_INCREMENT) | `symbol` (UNIQUE), `name`, `current_price` (DECIMAL 12,2), `category` | Equity catalog (100 Indian stocks) |
| `holdings` | `id` (BIGINT AUTO_INCREMENT) | `user_id`, `stock_id`, `quantity` (INT), `average_price` (DECIMAL 12,2), `UNIQUE(user_id, stock_id)` | Open positions per user |
| `trades` | `id` (BIGINT AUTO_INCREMENT) | `user_id`, `stock_id`, `trade_type` (`BUY`/`SELL`), `quantity`, `price`, `timestamp` | Historical executed trade records |
| `candles` | `id` (BIGINT AUTO_INCREMENT) | `stock_id`, `timeframe` (VARCHAR 5), `candle_start` (DATETIME), `open_price`, `high_price`, `low_price`, `close_price`, `volume`, `is_closed`, `source`, `UNIQUE(stock_id, timeframe, candle_start)` | Persistent multi-timeframe OHLCV market history |

**Key Check Constraints:**
* `chk_balance_non_negative`: `balance >= 0`
* `chk_holdings_quantity`: `quantity > 0`
* `chk_candles_prices`: `high_price >= open_price AND high_price >= close_price AND low_price <= open_price AND low_price <= close_price AND low_price >= 0`

---

## 6. How to Build, Test, and Run

### Running Automated Tests
```powershell
# Executes all 88 unit & integration tests against papertrade_test database
mvn clean test
```

### Packaging for Production
```powershell
# Compiles frontend assets with Vite and packages WAR file for Tomcat 10.1
mvn clean package -Pproduction
```

### Deploying & Accessing
1. Deploy `target/paper-trading.war` to Apache Tomcat 10.1 `webapps/`.
2. Ensure MySQL 8.0 is running on port 3306 with `papertrade` and `papertrade_test` created.
3. Access the application:
   * **Terminal Login / Dashboard:** `http://localhost:8081/paper-trading/ui/dashboard`
   * **Market & Charts:** `http://localhost:8081/paper-trading/ui/market`
   * **Holdings & P&L:** `http://localhost:8081/paper-trading/ui/portfolio`
   * **Admin Terminal:** `http://localhost:8081/paper-trading/ui/admin/dashboard`

---

## 7. What is Left to Add (Unimplemented Roadmap)

While SimTrade 2.0 has completed Phases 1 through 3.1, the following core trading platform capabilities are planned for upcoming phases:

### Phase 4: Advanced Order Types & Execution Engine ⭐ *(Highest Priority)*
* **Orders View (`/ui/orders`):** Dedicated navigation tab containing:
  * Open / Pending Orders (awaiting price trigger)
  * Completed / Executed Orders
  * Cancelled / Expired Orders
* **Order Types:**
  * **Limit Orders:** Buy $\le$ target price; Sell $\ge$ target price.
  * **Stop-Loss Orders (SL & SL-M):** Exit position when price breaches stop threshold.
  * **Bracket / Target Orders:** Simultaneous Stop-Loss + Take-Profit order creation.
* **Order Matching Engine:**
  * New `pending_orders` table (`user_id`, `stock_id`, `order_type`, `quantity`, `target_price`, `status`).
  * In-memory simulator matcher in `PriceSimulator.java` evaluating pending orders on every tick broadcast.
  * Margin locking: Reserving user cash for open Limit Buy orders so cash cannot be double-spent.

### Phase 5: Technical Chart Indicators & Overlays
* **Client-side indicators in `trading-chart.js`:**
  * Overlays: **SMA (20, 50, 200)**, **EMA (9, 21)**, **Bollinger Bands (20, 2σ)**.
  * Sub-pane oscillators: **RSI (14)**, **MACD (12, 26, 9)**.
* Interactive UI toggle buttons above the chart in `TradingChartView.java`.

### Phase 6: Custom Watchlists & Stock Screener
* User-defined custom watchlists (`user_watchlists`, `user_watchlist_items`).
* Market screener tabs: Top Gainers, Top Losers, Most Active by Volume, 52-Week High / Low.

### Phase 7: Portfolio Risk Analytics & Equity Curve
* Historical Net Worth equity curve chart tracking daily portfolio value over time.
* Institutional risk metrics: Sharpe Ratio, Maximum Drawdown (MDD), Portfolio Beta, Win/Loss Ratio.
* Sector diversification breakdown (pie/donut chart of holdings by sector).

### Phase 8: Price Alerts & Notification Center
* User-configured price alerts (e.g. *"Alert when INFY crosses ₹1,800"*).
* Navbar bell icon with unread notification badge and floating in-app toasts.

### Phase 9: Virtual Capital Management & Statement Export
* Virtual fund resets / deposit simulated capital controls.
* Export Trade Log and P&L statements to CSV and PDF.

---

## 8. Quick Prompting Templates for ChatGPT

When asking ChatGPT to implement or assist with the next features, copy and paste this context document followed by one of these prompts:

### Prompt to implement Phase 4 (Advanced Order Types):
> *"Based on the attached SimTrade 2.0 Project Context, implement Phase 4: Advanced Order Types (Limit and Stop-Loss orders). Design the `pending_orders` schema, write the `OrderDAO`, implement the background order-matching logic in `PriceSimulator`, and update the UI with an 'Orders' view and Limit/SL inputs in the Trade modal. Ensure strict `BigDecimal` math and transaction isolation."*

### Prompt to implement Phase 5 (Technical Chart Indicators):
> *"Based on the attached SimTrade 2.0 Project Context, implement Phase 5: Technical Chart Indicators. Update `trading-chart.js` and `TradingChartView.java` to support SMA (20, 50), EMA (9, 21), and an RSI (14) oscillator sub-pane with toggle pills in the Warm Graphite and Copper design system."*
