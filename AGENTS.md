# Dev 2 (Auth, Users & Admin) — 3-Day Development & Agent Guide

> **Branch**: `dev2/auth-admin`  
> **Repository**: [kunal-gswm/simtrade/tree/dev2/auth-admin](https://github.com/kunal-gswm/simtrade/tree/dev2/auth-admin)  
> **Role**: Dev 2 — Authentication, User Management, Security Filters, and Admin Oversight  
> **Tech Stack**: Java 17 · Jakarta Servlet 6 · JSP / JSTL 3.0 · JDBC · MySQL 8 (InnoDB) · Tomcat 10.1 · Maven

---

## 1. Dev 2 Owned Files (Strict Ownership)

Nobody else edits these files. All changes in these files are Dev 2's responsibility:

```text
simtrade/
├── src/main/java/com/project/trading/
│   ├── util/
│   │   └── PasswordUtil.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   └── StatsDAO.java
│   ├── service/
│   │   ├── UserService.java
│   │   └── AdminService.java
│   ├── filter/
│   │   ├── EncodingFilter.java
│   │   └── AuthFilter.java
│   └── servlet/
│       ├── LoginServlet.java
│       ├── RegisterServlet.java
│       ├── LogoutServlet.java
│       ├── ProfileServlet.java
│       ├── AdminDashboardServlet.java
│       ├── AdminUsersServlet.java
│       └── AdminTradesServlet.java
├── src/main/webapp/WEB-INF/jsp/
│   ├── login.jsp
│   ├── register.jsp
│   ├── profile.jsp
│   └── admin/
│       ├── dashboard.jsp
│       ├── users.jsp
│       └── trades.jsp
└── src/test/java/com/project/trading/
    └── PasswordUtilTest.java
```

---

## 2. Dev 2 3-Day Roadmap & Deliverables

### 📅 Day 1: Authentication & User Foundation

#### Environment & Database Setup
- [x] Checkout and track remote branch: `git checkout -b dev2/auth-admin origin/dev2/auth-admin` (or `git switch dev2/auth-admin`).
- [x] Verify local MySQL database is initialized from `db/schema.sql` and `db/seed.sql`.

#### Task `T17` — Password Security (`PasswordUtil.java`)
- [x] Implement PBKDF2 with HMAC-SHA256 (`PBKDF2WithHmacSHA256`).
- [x] Configuration: **65,536 iterations**, **256-bit key length**, **16-byte salt** via `SecureRandom`.
- [x] Format: `Base64(salt):Base64(hash)`.
- [x] Constant-time comparison using `MessageDigest.isEqual(...)` to prevent timing attacks.
- [x] Verify against seeded user hashes in `PasswordUtilTest.java` (`admin`, `rahul`, `priya`).

#### Task `T16` — User Data Access (`UserDAO.java`)
- [x] **Transaction & Locking Methods (Critical for Trading Engine)**:
  - `findByIdForUpdate(Connection c, long id)`: `SELECT ... FOR UPDATE` (row-level lock for trading engine).
  - `adjustCash(Connection c, long id, BigDecimal delta)`: Atomic update `UPDATE users SET cash_balance = cash_balance + ? WHERE id = ?`.
- [x] **Standard CRUD & Lookup Methods**:
  - `findById(Connection c, long id)`
  - `findByUsername(Connection c, String username)`
  - `existsByUsername(Connection c, String username)`
  - `existsByEmail(Connection c, String email)`
  - `insert(Connection c, User user)` with `Statement.RETURN_GENERATED_KEYS`
  - `updateProfile(Connection c, long id, String fullName, String email)`
  - `updatePasswordHash(Connection c, long id, String hash)`
  - `updateStatus(Connection c, long id, UserStatus status)`
  - `findAll(Connection c)`

#### Tasks `T18` & `T19` — User Business Logic & Security Filters
- [x] **`UserService.java`**:
  - `register(...)`: Validate fields (username regex `^[A-Za-z0-9_]{3,30}$`, valid email, password rules $\ge 8$ chars with 1 letter & 1 digit), check uniqueness, hash password, auto-credit ₹100,000.00 cash, catch duplicate key exceptions.
  - `login(...)`: Verify password hash, check if user `status == BLOCKED`, return identical error message for nonexistent username and invalid password (prevents username enumeration).
  - `getById(...)`, `updateProfile(...)`, `changePassword(...)`, `listAll(...)`, `setBlocked(...)`.
- [x] **`EncodingFilter.java`**: Sets UTF-8 encoding on every request/response (`/*`).
- [x] **`AuthFilter.java`**:
  - Unauthenticated access to `/app/*` or `/admin/*` $\rightarrow$ redirect to `/login`.
  - Non-admin access to `/admin/*` $\rightarrow$ HTTP 403 Forbidden (`error.jsp`).
  - Admin access to `/app/*` $\rightarrow$ redirect to `/admin/dashboard`.

#### Task `T20` — Authentication Controllers & Views
- [x] **`LoginServlet.java`** + **`login.jsp`**: Post-Redirect-Get pattern, regenerate session ID on login (`SessionUtil.login`), redirect by role.
- [x] **`RegisterServlet.java`** + **`register.jsp`**: Validation failure keeps input values (except passwords), flash message on success $\rightarrow$ `/login`.
- [x] **`LogoutServlet.java`**: Invalidate session $\rightarrow$ redirect `/login`.

---

### 📅 Day 2: User Profile & Admin Oversight

#### Task `T21` — Profile & Password Management
- [x] **`ProfileServlet.java`**: `/app/profile` (GET to view, POST for `action=profile` or `action=password`).
- [x] **`profile.jsp`**: Responsive card layout displaying username (readonly), cash balance, editable full name/email, and password change form (requires old password check).

#### Task `T22` — Admin Dashboard & Statistics
- [x] **`StatsDAO.java`**:
  - `PlatformStats load(Connection c)`: Aggregates `COUNT(*)` from `users`, active count from `stocks`, total trades & `SUM(total_amount)` from `trades`, and `topStocksByTrades` (top 5 by trade count via `GROUP BY`).
- [x] **`AdminService.java`**: Manages connection and DAO delegation for admin queries.
- [x] **`AdminDashboardServlet.java`** + **`admin/dashboard.jsp`**: Displays metric stat cards and top 5 stocks table.

#### Task `T23` — Admin User Management
- [x] **`AdminUsersServlet.java`**: `/admin/users` (GET to list, POST to block/unblock).
- [x] **Guards**: Admins cannot block their own account, and admin accounts cannot be blocked.
- [x] **`admin/users.jsp`**: User table with status badges and block/unblock buttons with confirmation prompts.

#### Task `T24` — Global Trade Audit Ledger
- [x] **`AdminTradesServlet.java`**: `/admin/trades` calling `adminService.getRecentTrades(200)`.
- [x] **`admin/trades.jsp`**: Audited table of the latest 200 platform trades with timestamps, usernames, stock symbols, trade types, amounts, and realized P&L.

---

### 📅 Day 3: Testing, Documentation & Final Demo

#### Task `T25` — Test Plan Execution
Run the following test matrix from Section 23 of the PRD:

| Test ID | Test Scenario | Input / Action | Expected Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **REG-01** | Valid Registration | `sneha`, `sneha@x.com`, `Sneha R`, `Test@1234` | Redirect `/login`, flash success, DB row has ₹100,000 cash, hashed pw | ⏳ |
| **REG-02** | Duplicate Username | `rahul` | Error: *"Username already taken"* | ⏳ |
| **REG-03** | Duplicate Email | `rahul@papertrade.local` | Error: *"Email already registered"* | ⏳ |
| **REG-04** | Weak Password | `abc` | Error: Password must be $\ge 8$ chars with 1 letter & 1 digit | ⏳ |
| **REG-05** | Form Memory | Blank fields or invalid email | Form preserved with entered values (password cleared) | ⏳ |
| **REG-06** | XSS in Name | `<script>alert(1)</script>` as full name | Escaped and rendered as literal text | ⏳ |
| **LOG-01** | Valid User Login | `rahul` / `Demo@123` | Redirect `/app/dashboard` | ⏳ |
| **LOG-02** | Valid Admin Login | `admin` / `Admin@123` | Redirect `/admin/dashboard` | ⏳ |
| **LOG-03** | Wrong Password | `rahul` / `wrong` | Error: *"Invalid username or password"* | ⏳ |
| **LOG-04** | Unknown User | `nobody` / `secret` | Identical message to LOG-03 (prevents enumeration) | ⏳ |
| **LOG-05** | Blocked User Login | Block `rahul`, then `rahul` logs in | Error: *"Account blocked"*, no session created | ⏳ |
| **LOG-06** | Back Button After Logout | Logout $\rightarrow$ browser back $\rightarrow$ click link | Redirected to `/login` | ⏳ |
| **AUTH-01** | Anonymous User Access | Directly visit `/app/portfolio` | Redirect to `/login` with session flash message | ⏳ |
| **AUTH-02** | Anonymous Admin Access | Directly visit `/admin/users` | Redirect to `/login` | ⏳ |
| **AUTH-03** | User Enters Admin Area | Logged in as `rahul` $\rightarrow$ visit `/admin/dashboard` | HTTP 403 Forbidden (`error.jsp`) | ⏳ |
| **AUTH-04** | Admin Enters User Area | Logged in as `admin` $\rightarrow$ visit `/app/market` | Redirected to `/admin/dashboard` | ⏳ |
| **AUTH-06** | Self / Admin Block Guard | Admin attempts to block `admin` | Block action rejected with error message | ⏳ |
| **AUTH-07** | Live Blocked User Trade | Block `rahul` during active session $\rightarrow$ buy | Order rejected: *"Account cannot trade"* | ⏳ |

#### Feature Freeze & Merge
- Merge `dev2/auth-admin` into `main`.
- Freeze all new feature additions; focus on polish and documentation.

#### Documentation Sections Assigned to Dev 2
1. **Problem Statement & Objectives** (Shortened from PRD Sec 2 & 3).
2. **User Roles & Authorization Matrix** (Guest, USER, ADMIN rules).
3. **Functional & Non-Functional Requirements** (Auth, Users, Admin tables).
4. **Use Case Diagram**:
   - Actors: Guest (Register, Login), User (Profile, Logout), Admin (Block/Unblock, View Trades, View Stats).
5. **Class Diagram**:
   - Auth/User layer: `UserDAO`, `StatsDAO`, `UserService`, `AdminService`, `AuthFilter`, `EncodingFilter`, `PasswordUtil`.
6. **Sequence Diagrams**:
   - (a) User Login & Session Fixation Protection Flow.
   - (b) Admin User Block Flow.

---

## 3. Dev 2 Demo Speaking Points

| Feature | Screen | Dev 2 Talking Points | Graded Concepts |
| :--- | :--- | :--- | :--- |
| **Admin Login** | `/login` $\rightarrow$ `/admin/dashboard` | *"Logging in as `admin` automatically redirects to the admin realm via role-based authentication and session attributes."* | `AuthFilter`, Session Management |
| **User Registration** | `/register` $\rightarrow$ `/login` | *"When `sneha` registers, server-side validation checks regex rules, PBKDF2 hashes the password with 65,536 iterations, and ₹100,000 cash is initialized."* | PBKDF2, Input Validation |
| **Role Authorization** | `/app/dashboard` $\rightarrow$ `/admin/users` | *"If regular user `rahul` tries to navigate to `/admin/users`, `AuthFilter` intercepts and responds with HTTP 403 Forbidden."* | Role Isolation, Servlet Filters |
| **Admin Controls** | `/admin/users` & `/admin/trades` | *"The admin dashboard aggregates real-time metrics, allows blocking rogue accounts, and provides an audited trade ledger across all users."* | SQL Aggregates, Admin Controls |

---

## 4. Git Commands for Dev 2

```powershell
# 1. Switch to your dev2 branch
git checkout -b dev2/auth-admin origin/dev2/auth-admin

# 2. Stage your files
git add src/main/java/com/project/trading/dao/UserDAO.java
git add src/main/java/com/project/trading/dao/StatsDAO.java
git add src/main/java/com/project/trading/service/UserService.java
git add src/main/java/com/project/trading/service/AdminService.java
git add src/main/java/com/project/trading/filter/AuthFilter.java
git add src/main/java/com/project/trading/filter/EncodingFilter.java
git add src/main/java/com/project/trading/servlet/
git add src/main/webapp/WEB-INF/jsp/
git add src/test/java/com/project/trading/PasswordUtilTest.java
git add AGENTS.md

# 3. Commit your work
git commit -m "Complete Dev 2 Day 1 and Day 2 Auth, User, and Admin modules"

# 4. Push to remote dev2 branch
git push origin dev2/auth-admin
```
