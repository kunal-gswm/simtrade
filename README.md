<div align="center">
  <img src="https://raw.githubusercontent.com/FortAwesome/Font-Awesome/6.x/svgs/solid/chart-line.svg" width="80" height="80" alt="SimTrade Logo">
  <h1>SimTrade Platform</h1>
  <p><strong>A Professional Paper Trading Application</strong></p>
</div>

<br/>

## <img src="https://raw.githubusercontent.com/FortAwesome/Font-Awesome/6.x/svgs/solid/circle-info.svg" width="20" height="20" align="top"> Overview

SimTrade is a robust, academic-grade paper trading platform designed to simulate real-world financial markets. It allows users to manage portfolios, execute trades, and analyze market trends within a risk-free environment. Built with an enterprise-grade Java backend and a dual-interface architecture, the platform supports both standard legacy web components and a modern Vaadin-based reactive UI.

---

## <img src="https://raw.githubusercontent.com/FortAwesome/Font-Awesome/6.x/svgs/solid/layer-group.svg" width="20" height="20" align="top"> Core Architecture

The system operates on a dual-routing methodology to fulfill strict academic compliance while providing a premium user experience.

- **Backend Logic**: Pure Java `Servlets`, `JSP`, `JSTL`, and `JDBC`.
- **Modern UI**: Developed with `Vaadin Flow 24`, featuring reactive data grids, custom SVGs, and responsive design tokens.
- **Database**: Relational data persistence via `MySQL 8+` with isolated environment switching for test suites.
- **Dependency Management**: Standard `Maven` lifecycle.

---

## <img src="https://raw.githubusercontent.com/FortAwesome/Font-Awesome/6.x/svgs/solid/star.svg" width="20" height="20" align="top"> Key Features

- **Market Simulation**: A live background thread dynamically adjusts stock prices.
- **Portfolio Management**: Real-time evaluation of total equity, available cash, and holdings value.
- **Trade Execution**: Synchronized Order execution engine with robust insufficient-funds protection.
- **Role-Based Access**: Dedicated Admin Portals alongside standard user workflows.
- **Dual Compatibility**: Legacy JSPs route to `/app/*` while the Vaadin Flow UI seamlessly occupies the `/ui/*` namespace.

---

## <img src="https://raw.githubusercontent.com/FortAwesome/Font-Awesome/6.x/svgs/solid/server.svg" width="20" height="20" align="top"> Local Setup Instructions

### Prerequisites
- **Java**: JDK 17+
- **Database**: MySQL 8.0+
- **Application Server**: Apache Tomcat 10.1+
- **Build Tool**: Apache Maven 3.8+

### 1. Database Configuration
Create the necessary MySQL databases:
```sql
CREATE DATABASE papertrade;
CREATE DATABASE papertrade_test;
```
Configure your credentials in `src/main/resources/db.properties` and `src/test/resources/db.properties`. Note that these files are intentionally excluded from version control for security.

### 2. Build the Application
Package the application into a Deployable Web Archive (WAR).
```bash
mvn clean package -DskipTests
```

### 3. Deployment
Copy the generated artifact to your Tomcat server:
```bash
cp target/paper-trading.war <tomcat-directory>/webapps/
```
Start the Tomcat daemon and navigate to `http://localhost:8081/paper-trading`.

---

## <img src="https://raw.githubusercontent.com/FortAwesome/Font-Awesome/6.x/svgs/solid/vial.svg" width="20" height="20" align="top"> Testing & Quality Assurance

The system is fortified with extensive unit tests verifying concurrent execution scenarios, transaction rollbacks, and route isolation.

```bash
mvn clean test
```
The test suite securely targets the `papertrade_test` container, ensuring the developer database schema is never destructively overwritten during validation passes.

---

## <img src="https://raw.githubusercontent.com/FortAwesome/Font-Awesome/6.x/svgs/solid/shield-halved.svg" width="20" height="20" align="top"> License & Academic Compliance

This repository is maintained as an academic deliverable. Legacy servlets (`DashboardServlet`, `MarketServlet`, etc.) are explicitly retained to meet strict evaluative criteria. All graphical user refinements operate cleanly within isolated `com.project.trading.ui.views` spaces.
