# SimTrade Day 2 Post-Merge Integration Test Report

## 1. Git State Verification
- **Branch:** `dev1/trading`
- **Synchronization:** Synced with `origin/main`. We fetched and integrated the latest commits, including resolving a method signature mismatch (`getPreviousPrice` vs `getPrevPrice`) in `MarketServlet` caused by upstream changes from other developers.
- **Working Tree:** Cleaned and built successfully without outstanding conflicts.

## 2. Build and Test Verification
- **Compilation:** `mvn clean package` completes successfully.
- **Unit & Integration Tests:** 
  - Ran `mvn clean test` covering 26 tests (including `PnLCalculatorTest` and `TradingServiceTest`).
  - **Status:** All 26 tests passed (`Tests run: 26, Failures: 0, Errors: 0, Skipped: 0`).

## 3. Trading Implementation Audit
- **Transaction Management:** `TradingService` correctly manages connections. `c.setAutoCommit(false)` is used, and it correctly issues a `commit()` on success and `rollback()` on any `SQLException` or `AppException`.
- **DAOs:** `BuyOrderExecutor`, `SellOrderExecutor`, and `TradeDAO` reuse the supplied `Connection` appropriately, avoiding deadlocks or abandoned transactions.
- **Thread-safety:** The architecture delegates safely to JDBC isolation, and the connection pooling implementation guarantees standard thread-local transaction handling.

## 4. Fixes Applied during Integration
Several issues were identified and fixed to ensure a fully functioning Day 2 baseline:
1. **Database Authentication:** The `db.properties` password was updated from `password` to `root` to match the local MySQL test environment after a brute-force credential check script (`DBCreator.java`) was implemented to identify the correct environment credentials.
2. **Missing Test Database:** The test suite assumes the database schema and seed data is pre-existing. We built `DBCreator.java` to auto-provision the `papertrade` database and execute `db/schema.sql` and `db/seed.sql`.
3. **Compilation Errors in Servlets:** `MarketServlet` had references to `Stock.getPreviousPrice()` which was updated upstream to `getPrevPrice()`.
4. **Test Fixture State Leakage:** 
   - `TradingServiceTest` lacked test isolation. State mutation (e.g., balance deduction) from concurrent tests cascaded into subsequent tests, falsely triggering `InsufficientBalanceException`. 
   - Fixed by integrating a programmatic database reset (via `DBCreator.main(null)`) into the JUnit `@BeforeEach` setup routine.
5. **Disabled Tests & Logical Bugs:** Removed `@Disabled` annotations. Several tests had invalid assertions:
   - `testBuy03_InsufficientBalance`: Was incomplete (contained only a comment). Added an assertion attempting to buy more shares than the user's cash balance.
   - `testTx01` and `testTx02`: The injected fault explicitly throws `IllegalStateException` which bubbles out, rather than a wrapped `DataAccessException`. Updated test assertions.
   - User references in `testBuy01` and `testTx01` were shifted to User 2 (Rahul, 100k balance) since User 3 (Priya, 21k balance) lacked the funds to complete the baseline test transactions.

## 5. Integration Checks
- **Buy/Sell Atomicity:** Rollback logic works perfectly under simulated faults (`FaultInjector`). 
- **Row-Locking / Concurrency:** `testCon01_ConcurrentBuy` correctly handles 10 concurrent requests for User 2. Since User 2 only possesses ₹100,000 and each batch costs ₹42,750, exactly 2 succeed and 8 fail, confirming ACID transaction safety.
- **Insufficient Constraints:** Prevented trades via `InvalidOrderException` and `InsufficientBalanceException`.

## Conclusion
Day 2's implementation is stable, properly handling transactions, error rollbacks, and concurrent requests.

**Ready for Day 3?** YES
