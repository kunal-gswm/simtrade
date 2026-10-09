# SimTrade Documentation (Dev 3)

## 1. ER Diagram Description
The database consists of 4 main entities: `users`, `stocks`, `holdings`, and `trades`.

* `users` (1) --- (<) `holdings`
* `users` (1) --- (<) `trades`
* `stocks` (1) --- (<) `holdings`
* `stocks` (1) --- (<) `trades`

Relationships:
- A User has many Holdings.
- A User has many Trades.
- A Stock is part of many Holdings.
- A Stock is involved in many Trades.

## 2. Database Schema Summary
- **users**: `user_id` (PK), `username`, `password_hash`, `role`, `status`, `cash_balance`, `created_at`
- **stocks**: `stock_id` (PK), `symbol`, `company_name`, `sector`, `price`, `previous_price`, `is_active`, `created_at`, `updated_at`
- **holdings**: `holding_id` (PK), `user_id` (FK), `stock_id` (FK), `quantity`, `avg_buy_price`, `last_updated`
- **trades**: `trade_id` (PK), `user_id` (FK), `stock_id` (FK), `type` (BUY/SELL), `quantity`, `price`, `total_amount`, `realized_pnl`, `trade_time`

*See `db/schema.sql` for exact column types and constraints.*

## 3. Technology Stack
- **Backend**: Java 17+, Jakarta Servlet API (JSP/Servlets). No Spring/Hibernate.
- **Frontend**: HTML5, Vanilla CSS, JSP scriptlets / JSTL. No frontend frameworks like React/Angular.
- **Database**: PostgreSQL / MySQL (ANSI SQL compatible).
- **Concurrency**: `java.util.concurrent` (ScheduledExecutorService) for background tasks.
- **Build**: Maven.

## 4. Java Concepts Used
| Concept | Where/How it's Used |
|---------|---------------------|
| JDBC/Transactions | Used in `StockDAO` and `StockService` for atomic CRUD and price shifting. |
| Collections | `List<Stock>` used for passing data from Servlet to JSP. |
| Multithreading | `PriceSimulator` uses `ScheduledExecutorService` for concurrent price updates. |
| Design Patterns | MVC (Servlets as Controllers, JSP as Views, DAO as Models), Singleton (DBConnection). |
| Exceptions | Custom exceptions (e.g., `AppException`, `ValidationException`) used for business logic errors. |
