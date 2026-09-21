# SimTrade — Concurrent Stock Trading Platform

A simulated stock trading platform built in core Java, exposed through a Servlet-based web layer, and persisted through JDBC. Built as a Java Programming project (Topic #26) for the 2029 Passout batch, SCSE, Galgotias University (GUVI-HCL).

## Overview

Two roles exist: **Trader** and **Admin**. Traders buy and sell simulated stocks, hold a portfolio, and see live (simulated) market price updates. Admins manage user accounts, monitor trading activity, and oversee system settings.

This topic was chosen specifically because it creates a genuine need for concurrency handling — multiple users can legitimately try to trade the same stock at the same instant, and a shared price feed has to update independently of any single user's request. The project treats that as its technical core, not a decorative add-on.

| | Base Requirement (official topic) | This Implementation (added deliberately) |
|---|---|---|
| Trader | Buy/sell stocks, portfolio tracking, market updates, trade history and alerts | + Live simulated price feed on a background thread |
| Admin | User management, financial data security, system settings, trade monitoring and reports | + Concurrent trade execution protected with synchronization |
| — | — | + Order book and matching engine (market + limit orders) |
| — | — | + Every trade wrapped in a JDBC transaction with rollback on failure |

**Out of scope, explicitly:** no real market data feeds or brokerage APIs, no real payments, no public deployment, no production-grade auth beyond session-based login and role checks.

## Tech Stack

- **Language:** Java 17
- **Web layer:** Jakarta Servlets (no Spring)
- **Persistence:** JDBC, MySQL
- **Build tool:** Maven
- **Server:** Apache Tomcat 10+

## Architecture

```
                     WEB CLIENT (Browser)
                           |
                           v
                  +------------------+
                  |     Servlets     |   login, trade requests,
                  +------------------+   dashboard data, admin actions
                           |
                           v
                  +------------------+
                  |  Service Layer   |   business logic, validation
                  +------------------+
                           |
         +------------------+-------------------+
         |                  |                    |
         v                  v                    v
  +-------------+   +---------------+   +------------------+
  | Order Engine|   |  Portfolio    |   |   Market Feed    |
  | (matching,  |   |  Service      |   |  (price          |
  |  order book)|   |  (holdings,   |   |   simulation     |
  |             |   |   balance)    |   |   thread)        |
  +-------------+   +---------------+   +------------------+
         |                  |                    |
         +------------------+--------------------+
                           |
                           v
                  +------------------+
                  |  JDBC / DAO Layer|
                  +------------------+
                           |
                           v
                     +-----------+
                     | DATABASE  |
                     +-----------+
```

Two different resources are protected by two different mechanisms: in-memory shared state (order book, portfolio) is guarded with Java-level `synchronized`/`ReentrantLock`, and database rows are guarded by the JDBC transaction boundary. The thread holding the in-memory lock for a given stock performs the full transaction for that trade, start to commit, before releasing the lock — so the database never sees overlapping writes to the same stock's rows.

## Project Structure

```
simtrade/
├── pom.xml
├── .gitignore
├── README.md
└── src/main/
    ├── java/com/project/trading/
    │   ├── model/        User, Trader, Admin, Stock, Order (abstract),
    │   │                 MarketOrder, LimitOrder, Trade, Portfolio, Holding
    │   ├── engine/        OrderBook, MatchingEngine, PriceFeedSimulator
    │   ├── dao/           UserDAO, StockDAO, OrderDAO, TradeDAO, PortfolioDAO
    │   ├── service/       AuthService, TradingService, PortfolioService
    │   ├── web/           LoginServlet, RegisterServlet, DashboardServlet,
    │   │                 TradeServlet, AdminServlet
    │   ├── exception/     InsufficientFundsException, InvalidOrderException,
    │   │                 StockNotFoundException
    │   └── util/          DBConnectionUtil, PasswordUtil
    ├── resources/
    │   └── db.properties.example
    └── webapp/WEB-INF/
        └── web.xml
```

## Prerequisites

- JDK 17 or higher
- Maven 3.8+
- MySQL 8+
- Apache Tomcat 10+ (Jakarta EE 10 compatible)

## Setup & Installation

**1. Clone the repository**
```bash
git clone https://github.com/kunal-gswm/simtrade.git
cd simtrade
```

**2. Create the database**
```sql
CREATE DATABASE simtrade;
```
Run the schema script (see `src/main/resources/schema.sql` once added) to create the `users`, `stocks`, `orders`, `trades`, `portfolios`, and `holdings` tables.

**3. Configure your local database credentials**

Copy the example config and edit it with your own local MySQL credentials — this file is git-ignored and should never be committed:
```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```
```properties
db.url=jdbc:mysql://localhost:3306/simtrade
db.user=CHANGE_ME
db.password=CHANGE_ME
```

**4. Build the project**
```bash
mvn clean package
```

**5. Deploy**

Copy the generated `.war` file from `target/` into Tomcat's `webapps/` directory, start Tomcat, and access the app at `http://localhost:8080/simtrade`.

## Database Schema (summary)

| Table | Key Columns |
|---|---|
| `users` | id, name, email, password_hash, role, created_at |
| `stocks` | symbol, name, current_price, last_updated |
| `orders` | id, user_id, stock_symbol, order_type, side, quantity, limit_price, status, created_at |
| `trades` | id, buy_order_id, sell_order_id, stock_symbol, quantity, price, executed_at |
| `portfolios` | user_id, cash_balance |
| `holdings` | user_id, stock_symbol, quantity, avg_price |

Passwords are hashed before storage — never stored in plaintext. All queries use `PreparedStatement`; no string-concatenated SQL anywhere in the codebase.

## Testing

The concurrency test is the centerpiece of this project's technical demonstration, not an afterthought:

- **Race condition test:** 20 threads, released simultaneously via `CountDownLatch`, attempt trades against shared portfolio state. Run once unsynchronized (demonstrates the bug) and once with synchronization/locking (demonstrates the fix).
- **Transaction rollback test:** a trade is forced to fail mid-execution; the database state is verified unchanged afterward.
- **Functional checklist:** registration, login/logout, buy with sufficient/insufficient funds, sell without holdings, admin access control — see `/docs` for the full checklist.

## Branching & Workflow

| Branch | Owner | Scope |
|---|---|---|
| `main` | — | Protected. No direct pushes — all changes via reviewed pull request. |
| `feature/trading-engine` | Core engine, concurrency, integration | Order/MatchingEngine/PriceFeedSimulator, synchronization strategy |
| `feature/database` | Database & persistence | Schema, DAO layer, JDBC transactions |
| `feature/servlets` | Web backend | Auth, sessions, role-based access, request routing |
| `feature/dashboard` | UI & QA | Trader/admin dashboards, functional testing |

All changes go through a pull request into `main` with at least one review approval before merging. Direct pushes to `main` are blocked by a repository rule.

## Team

| Name | Role | GitHub |
|---|---|---|
| _(you)_ | Team Lead — Core Engine, Concurrency & Integration | `@kunal-gswm` |
| _(name)_ | Database & Persistence | `@ashubhandari22` |
| _(name)_ | Web Backend / Servlets | `@SINGH0883` |
| _(name)_ | Dashboard, Admin UI & QA | `@VaishnaviGupta-09` |

## Project Status

| Milestone | Deadline | Status |
|---|---|---|
| Review 1 — auth, DB layer, price feed, thread-safe trading, JDBC transactions | 10 Oct 2026 | In progress |
| Review 2 — order book, matching engine, reports, polish | 15 Nov 2026 | Not started |

## License

This is an academic project built for a university course evaluation. Not intended for production, commercial use, or handling of real financial data.
