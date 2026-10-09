# SimTrade — Manual Test Execution Report
**Dev 4 | Portfolio, Dashboard, History, UI**
**Test Date:** 2026-10-09

---

## Environment
| Item | Value |
|---|---|
| Browser | Google Chrome 128 |
| Server | Apache Tomcat 10.1 |
| Java | JDK 17 |
| OS | Windows 11 |
| Starting Capital | ₹1,00,000 |

---

## Test Suite — PnLCalculator (Unit, JUnit 5)

| Test ID | Description | Expected | Result |
|---|---|---|---|
| UNIT-01 | `calculateInvested(10, 3900.00)` | `39000.00` | ✅ PASS |
| UNIT-02 | `calculateInvested(0, 3900.00)` | `0.00` | ✅ PASS |
| UNIT-03 | `calculateInvested(7, 542.80)` | `3799.60` | ✅ PASS |
| UNIT-04 | `calculateCurrentValue(10, 4000.00)` | `40000.00` | ✅ PASS |
| UNIT-05 | `calculateCurrentValue(10, 0.0)` | `0.00` | ✅ PASS |
| UNIT-06 | `calculateUnrealizedPnl(40000, 39000)` | `+1000.00` | ✅ PASS |
| UNIT-07 | `calculateUnrealizedPnl(38000, 39000)` | `-1000.00` | ✅ PASS |
| UNIT-08 | `calculateUnrealizedPnl(39000, 39000)` | `0.00` | ✅ PASS |
| UNIT-09 | `calculateUnrealizedPct(1000, 39000)` | `2.56%` | ✅ PASS |
| UNIT-10 | `calculateUnrealizedPct(100, 0)` — divide by zero guard | `0.00` | ✅ PASS |
| UNIT-11 | `calculateUnrealizedPct(-1000, 39000)` | `-2.56%` | ✅ PASS |
| UNIT-12 | `calculateNetWorth(20000, 81900)` | `101900.00` | ✅ PASS |
| UNIT-13 | `calculateNetWorth(0, 81900)` | `81900.00` | ✅ PASS |
| UNIT-14 | `calculateNetWorth(100000, 0)` — no holdings | `100000.00` | ✅ PASS |
| UNIT-15 | `calculateOverallPnl(103500)` | `+3500.00` | ✅ PASS |
| UNIT-16 | `calculateOverallPnl(98000)` | `-2000.00` | ✅ PASS |
| UNIT-17 | `calculateOverallPnl(100000)` — breakeven | `0.00` | ✅ PASS |

**Total Unit Tests: 17 / 17 PASS**

---

## Test Suite — BUY Flow (Manual)

| Test ID | Steps | Expected | Result |
|---|---|---|---|
| BUY-01 | Load `/dashboard` with fresh session | ₹5,00,000 cash, 4 seed holdings shown | ✅ PASS |
| BUY-02 | Buy 10 × RELIANCE @ market (₹2985.50) | Cash deducted by ₹29,855.00; holding increases | ✅ PASS |
| BUY-03 | Check `/app/portfolio` after BUY-02 | RELIANCE avg price recalculated correctly | ✅ PASS |
| BUY-04 | Buy with insufficient cash (enter qty 10000) | Error: "Insufficient funds!" shown; cash unchanged | ✅ PASS |
| BUY-05 | Buy qty = 0 | Validation error; no order created | ✅ PASS |
| BUY-06 | Buy qty = -5 | Validation error; no order created | ✅ PASS |
| BUY-07 | Buy with invalid symbol (type "INVALID") | Error: "Invalid or unknown stock symbol" | ✅ PASS |

---

## Test Suite — SELL Flow (Manual)

| Test ID | Steps | Expected | Result |
|---|---|---|---|
| SELL-01 | Sell 5 × RELIANCE (currently hold 25) | Cash credited; holding reduced to 20 | ✅ PASS |
| SELL-02 | Verify `/app/portfolio` after SELL-01 | Holding quantity updated, current value updated | ✅ PASS |
| SELL-03 | Sell all RELIANCE (remaining 20) | RELIANCE disappears from holdings | ✅ PASS |
| SELL-04 | Try to sell WIPRO when holding = 0 | Error: "Insufficient shares!" shown | ✅ PASS |
| SELL-05 | Sell more than holding (e.g. hold 5, sell 10) | Error: "Insufficient shares!" shown | ✅ PASS |

---

## Test Suite — Portfolio Page (T39)

| Test ID | Steps | Expected | Result |
|---|---|---|---|
| PORT-01 | Navigate to `/app/portfolio` — fresh session has holdings | Summary cards show correct totals | ✅ PASS |
| PORT-02 | Profit holding shows green P&L | Bull-text colour (#00f2c3) applied | ✅ PASS |
| PORT-03 | Loss holding shows red P&L | Bear-text colour (#ff6b78) applied | ✅ PASS |
| PORT-04 | Empty portfolio (sell all) | "Portfolio is empty" message shown | ✅ PASS |

---

## Test Suite — History Page (T40)

| Test ID | Steps | Expected | Result |
|---|---|---|---|
| HIST-01 | Navigate to `/app/history` after several trades | All trades listed newest-first | ✅ PASS |
| HIST-02 | BUY row shown in blue; SELL in red | Correct badge colours | ✅ PASS |
| HIST-03 | No trades made | "No trades yet" message shown | ✅ PASS |

---

## Test Suite — Error Pages (T42)

| Test ID | Steps | Expected | Result |
|---|---|---|---|
| ERR-01 | Navigate to `/app/nonexistent` | Friendly 404 page shown | ✅ PASS |
| ERR-02 | Session expires, access `/app/portfolio` | Redirect to `/dashboard` | ✅ PASS |

---

## Summary
| Suite | Total | Pass | Fail |
|---|---|---|---|
| Unit (PnLCalculator) | 17 | 17 | 0 |
| BUY Flow | 7 | 7 | 0 |
| SELL Flow | 5 | 5 | 0 |
| Portfolio Page | 4 | 4 | 0 |
| History Page | 3 | 3 | 0 |
| Error Pages | 2 | 2 | 0 |
| **TOTAL** | **38** | **38** | **0** |
