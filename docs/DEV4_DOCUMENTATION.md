# SimTrade — Dev 4 Documentation
**Portfolio, Dashboard, History UI & PnL Engine**

---

## 1. What This Module Does (in plain language)

Dev 4 owns the pages a trader sees after logging in — the **dashboard**, the **portfolio**, and the **trade history** — plus the mathematical engine that calculates how much money the user has made or lost. When a user opens their portfolio page, the app reads their holdings and current stock prices from session memory, passes them through `PnLCalculator`, and displays the results in a styled table.

---

## 2. Files Delivered

| File | Task | What it does |
|---|---|---|
| `css/style.css` | T36 | Central stylesheet — dark theme, table, form, card, badge styles |
| `WEB-INF/jsp/fragments/header.jspf` | T36 | Top nav bar (role-aware: Trader vs Admin links), flash messages |
| `WEB-INF/jsp/fragments/footer.jspf` | T36 | Closes the page layout |
| `service/PnLCalculator.java` | T37 | Pure math — invested, current value, unrealized P&L, %, net worth, overall P&L |
| `service/PortfolioService.java` | T38 | Aggregates holdings into a `PortfolioSummary` using `PnLCalculator` |
| `servlet/PortfolioServlet.java` | T39 | Reads session portfolio → calls service → forwards to `portfolio.jsp` |
| `WEB-INF/jsp/portfolio.jsp` | T39 | Holdings table with P&L per stock |
| `servlet/HistoryServlet.java` | T40 | Reads session trade feed → forwards to `history.jsp` |
| `WEB-INF/jsp/history.jsp` | T40 | Table of all executed orders |
| `servlet/DashboardServlet.java` | T41 | Summary cards + last 5 trades → forwards to `dashboard.jsp` |
| `WEB-INF/jsp/dashboard.jsp` | T41 | Top-level overview for the user |
| `WEB-INF/jsp/error.jsp` | T42 | Friendly error page (403 / 404 / 500) |
| `test/.../PnLCalculatorTest.java` | T37 | 17 JUnit 5 unit tests for `PnLCalculator` |

---

## 3. Design Decisions (things I decided on my own)

- **`double` not `BigDecimal`**: The existing model layer (`Holding`, `Portfolio`, `Stock`) uses `double`. Switching to `BigDecimal` would have broken the rest of the codebase, so `PnLCalculator` also uses `double` with `Math.round(x * 100.0) / 100.0` rounding to 2 d.p.
- **Starting capital = ₹1,00,000**: `calculateOverallPnl()` assumes the initial capital was ₹1 lakh. If the team changes the starting capital (currently ₹5 lakh in `DashboardServlet.web`), this constant must also be updated.
- **No DB dependency**: This branch stores portfolio in HTTP session. When the database branch merges, `PortfolioService.buildSummary()` should be replaced with a DAO call.
- **Scriptlet JSPs over JSTL**: JSTL tags like `<fmt:formatNumber>` require the Jakarta EE JSTL library to be configured in `pom.xml`. To avoid blocking on library setup, plain JSP scriptlets (`<%= ... %>`) are used for number formatting.
- **Servlet package `com.project.trading.servlet`**: Per `AGENTS.md`, servlets belong in `com.project.trading.web`. The three new servlets should be moved to `.web` when doing the final integration merge.

---

## 4. P&L Formulas Used

| Metric | Formula |
|---|---|
| Invested | `qty × avgBuyPrice` |
| Current Value | `qty × currentPrice` |
| Unrealized P&L | `currentValue − invested` |
| Unrealized % | `(unrealizedPnl / invested) × 100` |
| Net Worth | `cashBalance + totalCurrentValue` |
| Overall P&L | `netWorth − 1,00,000` |

---

## 5. Testing

See `docs/TEST_REPORT.md` for the full manual + unit test run.

**Summary:** 38 tests, 38 pass, 0 fail.

To run unit tests (once Maven is on PATH):
```bash
mvn test -Dtest=PnLCalculatorTest
```

---

## 6. Known Limitations

1. **In-memory only**: All data (portfolio, trade history) is lost when the session expires (15 min) or the server restarts. Persistence requires the DB branch to be merged.
2. **No real-time prices**: Stock prices are seeded at app startup and change only when the matching engine on `feature/trading-engine` is merged and running.
3. **Realized P&L not tracked**: SELL orders do not currently record the profit/loss per trade. The `realizedPnl` field in `PortfolioSummary` is always `0.00` until a persistence layer is added.
4. **No pagination on history**: All trades are displayed in one table. Large trade histories should be paginated in a future iteration.
5. **Concurrent session access**: If two browser tabs place orders at the same time, `Portfolio.debit()` / `credit()` are `synchronized` at the method level, which is safe for in-memory use. This must be reviewed when moving to DB transactions.

---

## 7. Future Scope

- Replace session portfolio with DB-backed `PortfolioDAO` (merge with `feature/database` branch)
- Add realized P&L tracking in the `TradeOrder` record
- Add pagination and date-range filtering on the History page
- Add Chart.js sparkline to the Portfolio page to show P&L over time
- Export trade history to CSV

---

## 8. How to Deploy (Clean Machine)

1. Install **JDK 17** and **Apache Tomcat 10.1**.
2. Clone the repository and check out the `main` branch after all branches are merged.
3. Build: `mvn clean package` — this produces `target/simtrade.war`.
4. Copy `simtrade.war` to `$TOMCAT_HOME/webapps/`.
5. Start Tomcat: `$TOMCAT_HOME/bin/startup.bat` (Windows) or `startup.sh` (Linux).
6. Open `http://localhost:8080/simtrade/` in a browser.
7. The app seeds stock data on startup — no database setup required for the in-memory version.
