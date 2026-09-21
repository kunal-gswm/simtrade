# AGENTS.md — Instructions for AI Coding Assistants on This Project

If you are an AI assistant (Claude, ChatGPT, Copilot, Cursor, or any other tool) helping someone on this
repository, read this file fully before writing any code. The person you're helping may not have a strong
Java background. Your job is not just to produce working code — it's to make sure the person understands
what you built, because they will be individually questioned on this code during evaluation.

## Project Context

SimTrade is a simulated stock trading platform (Java, Servlets, JDBC, MySQL) built for a university project.
Two roles: Trader and Admin. The technical core of this project is demonstrating real concurrency handling
(multiple users trading the same stock at once) and real database transaction management (a trade updates
four things — buyer cash, seller cash, holdings, trade record — atomically). See `README.md` in this repo
for the full architecture, schema, and team task breakdown before assuming anything about the design.

## Before You Write Any Code

- **Ask what the person actually wants**, in their own words, before generating a full class or feature.
  Do not assume intent from a short or vague request like "make the login page."
- **Ask which module they own** if it isn't obvious from context — check the branch table below. Do not
  write code outside their module's scope without telling them you're doing so and why.
- **If a task depends on something another module owns** (e.g. a servlet needs a DAO method that doesn't
  exist yet), stop and tell the person to check with whoever owns that branch — do not invent or stub out
  someone else's module for them.
- If the request is ambiguous or could reasonably mean two different things, ask which one before writing
  code. A clarifying question costs ten seconds; a wrong guess costs an hour of rework.

## After You Write Code — Do Not Skip This

- **Explain what you wrote in plain, non-technical language first** — two or three sentences, no jargon —
  before or alongside showing the code itself.
- **Ask the person to repeat back, in their own words, what the code does.** If they can't, explain it again
  more simply. Do not move on to the next piece of functionality until they can give a one-sentence answer
  to "what does this do and why."
- **Flag anything you decided on their behalf** — a default value, a validation rule, an error-handling
  choice — so they know it was a decision someone should sign off on, not just an established fact.

## Hard Rules for This Codebase — Do Not Deviate, Even If Asked

- Every SQL query **must** use `PreparedStatement`. Never build SQL with string concatenation, even for a
  "quick test" or a "temporary" version. This is graded and it is a real security issue.
- **Never store passwords in plaintext.** Hash before saving, every time, no exceptions for test data.
- **Never commit real database credentials.** Local config goes in `db.properties` (already git-ignored);
  only `db.properties.example` with placeholder values belongs in git. If you generate a config file with
  real-looking credentials, warn the person explicitly not to commit it.
- Package structure is fixed. Put new classes in the correct existing package — do not invent new top-level
  packages without the person confirming it with the team lead first:
  ```
  com.project.trading.model      – data classes (Order, Stock, Trade, Portfolio, Holding...)
  com.project.trading.engine     – OrderBook, MatchingEngine, PriceFeedSimulator
  com.project.trading.dao        – database access classes
  com.project.trading.service    – business logic layer
  com.project.trading.web        – Servlets
  com.project.trading.exception  – custom exceptions
  com.project.trading.util       – shared utilities (DB connections, password hashing)
  ```
- Custom exceptions (`InsufficientFundsException`, `InvalidOrderException`, `StockNotFoundException`) are
  **checked** exceptions extending `Exception`, not `RuntimeException`. Stay consistent with this — don't
  silently switch styles partway through.
- **If your code touches shared state** — portfolio balance, holdings, or the order book — say so explicitly
  to the person: "this touches data that multiple users could modify at the same time — want me to explain
  why that matters here?" Do not silently add or skip synchronization without flagging it either way.

## Branch Ownership — Stay in Your Lane

| Branch | Owns |
|---|---|
| `feature/trading-engine` | `model/`, `engine/` — Order hierarchy, OrderBook, MatchingEngine, PriceFeedSimulator |
| `feature/database` | `dao/`, database schema |
| `feature/servlets` | `web/`, authentication, sessions, access control |
| `feature/dashboard` | JSP pages, static UI, admin views |

Only generate files inside the current person's module. If in doubt which branch you're being asked to help
with, ask.

## Before Opening a Pull Request

- Confirm the person can state, in one sentence, what their code does. If they can't, the task isn't done —
  keep explaining, don't move to the PR.
- Remind them commit messages should describe what changed (`Add UserDAO.save() with PreparedStatement`),
  not vague messages like `updates` or `fixes`.
- Remind them this repository requires a pull request into `main` — **never** suggest or attempt a direct
  push to `main`; it is protected and will be rejected regardless.

## If You're Unsure About Anything in This File

Ask the person, or tell them to check with the team lead. Do not guess and proceed as if the guess were
confirmed fact — an unstated assumption in this codebase is how a working demo turns into a broken one the
night before a deadline.


