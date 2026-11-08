# SimTrade 2.0 — Engineering & Agent Architecture Guide

> **Active Development Branch**: `upgrade/simtrade-2.0-audit`  
> **Tech Stack**: Java 17 · Vaadin Flow 24.3 · Jakarta Servlet 6 · JSP / JSTL 3.0 · HikariCP 5.1 · MySQL 8.0 (InnoDB) · Tomcat 10.1 · Maven 3.9

---

## 1. System Overview & Core Tenets

SimTrade 2.0 is a Java-based institutional-grade paper-trading platform. It simulates real-world equity market mechanics (inspired by NSE India) in a completely virtual, risk-free environment.

### Strict Engineering Rules:
1. **Paper-Trading Only**: Zero real money, real broker integrations, or payment gateways.
2. **Branch Isolation**: Never commit directly to `main`. All Phase 1 work resides on `upgrade/simtrade-2.0-audit`.
3. **Financial Precision**: All calculations involving money, shares, prices, and profit/loss must use `BigDecimal` with explicit scale and `RoundingMode.HALF_UP`. Never cast financial values to `double` or `float` in calculation paths.
4. **Single Source of Truth**: All interfaces (Vaadin Flow UI and traditional Jakarta Servlet/JSP) must share the identical service and DAO layers backed by MySQL. No in-memory collections or session maps for portfolio or trade state.
5. **Database Isolation**: Unit and integration tests must run exclusively against `papertrade_test` via `src/test/resources/db.properties`. Tests must never touch the development database (`papertrade`).
6. **Connection Pooling**: Database connections are managed via a thread-safe `HikariDataSource` pool configured in `DBConnection.java`. Services own transactions (`c.setAutoCommit(false)`, `c.commit()`, `c.rollback()`). Connections must always be acquired in try-with-resources blocks.
7. **Security & Route Guarding**: Every trading, account, and administrative operation requires server-side authentication (`AuthFilter`) and CSRF protection (`CsrfFilter`).

---

## 2. Package Architecture

```text
com.project.trading/
├── dao/               # High-performance JDBC Data Access Objects
│   ├── HoldingDAO.java
│   ├── StockDAO.java
│   ├── TradeDAO.java
│   ├── UserDAO.java
│   └── StatsDAO.java
├── exception/         # Typed domain and business rule exceptions
│   ├── AppException.java
│   ├── DataAccessException.java
│   ├── InsufficientBalanceException.java
│   ├── InsufficientSharesException.java
│   ├── StockNotFoundException.java
│   └── ValidationException.java
├── filter/            # HTTP Request Filters
│   ├── AuthFilter.java       # Role-based route authorization (/app/*, /admin/*, /dashboard, /trade)
│   ├── CsrfFilter.java       # Cryptographic synchronizer CSRF protection for POST requests
│   └── EncodingFilter.java   # UTF-8 request/response character encoding
├── listener/          # Servlet Context & Lifecycle Listeners
│   └── AppContextListener.java # HikariCP pool graceful shutdown
├── model/             # Domain POJOs and Value Objects
│   ├── AuthUser.java
│   ├── Holding.java
│   ├── Portfolio.java
│   ├── Role.java
│   ├── Stock.java
│   ├── Trade.java
│   ├── TradeType.java
│   └── User.java
├── service/           # Shared Business Logic & Transactional Engines
│   ├── AdminService.java
│   ├── PnLCalculator.java     # BigDecimal financial math engine
│   ├── PortfolioService.java  # Holdings aggregation & net worth reconciliation
│   ├── StockService.java
│   ├── TradingService.java    # ACID order execution with row-level locks
│   ├── UserService.java
│   └── order/
│       ├── BuyOrderExecutor.java
│       ├── SellOrderExecutor.java
│       └── OrderExecutor.java
├── servlet/           # Traditional Jakarta Servlet Controllers
│   ├── BaseServlet.java
│   ├── BuyServlet.java
│   ├── DashboardServlet.java
│   ├── HistoryServlet.java
│   ├── MarketServlet.java
│   ├── PortfolioServlet.java
│   ├── ProfileServlet.java
│   ├── SellServlet.java
│   ├── StockDetailServlet.java
│   └── admin/
├── simulator/         # Background Market Simulation
│   └── MarketPriceSimulator.java
├── ui/                # Vaadin Flow 24.3 Reactive Views
│   ├── admin/
│   ├── security/
│   └── views/
│       ├── DashboardView.java
│       ├── MarketView.java
│       ├── PortfolioView.java
│       └── LoginView.java
├── util/              # Shared Utilities
│   ├── DBConnection.java     # HikariCP connection pool provider
│   ├── PasswordUtil.java     # PBKDF2 with HMAC-SHA256 password hashing
│   ├── SessionUtil.java      # Session management & session fixation protection
│   └── Validator.java        # Input sanitization and bounds checking
└── web/               # Legacy Web Controller Adapters
    └── DashboardServlet.java # Harmonized legacy route handler (/dashboard, /trade)
```

---

## 3. Financial Invariants

All services and views must enforce and preserve the following mathematical invariants:

1. **Total Equity Invariant**:
   $$\text{Total Equity (Net Worth)} = \text{Available Cash} + \text{Total Current Holdings Value}$$
2. **P&L Reconciliation Invariant**:
   $$\text{Overall P&L} = \text{Realized P&L} + \text{Total Unrealized P&L}$$
3. **Capital Return Invariant**:
   $$\text{Overall P&L} = \text{Total Equity} - \text{Initial Starting Capital}$$
4. **Weighted Average Cost Basis**:
   When purchasing additional shares of an existing holding:
   $$\text{New Avg Price} = \frac{(\text{Current Qty} \times \text{Current Avg Price}) + (\text{New Qty} \times \text{Execution Price})}{\text{Current Qty} + \text{New Qty}}$$
5. **Realized Profit/Loss on Sell**:
   When selling shares:
   $$\text{Realized P&L} = \text{Sold Qty} \times (\text{Execution Price} - \text{Avg Buy Price})$$
   The average buy price of remaining shares remains strictly unchanged.

---

## 4. Test Execution & Database Isolation

Tests must never execute against development or production databases.

- Configuration file: `src/test/resources/db.properties`
- Test database: `papertrade_test`
- Automated test suite:
  ```bash
  mvn clean test
  ```
- Full production packaging verification:
  ```bash
  mvn clean package
  ```
