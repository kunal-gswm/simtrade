# Database Setup & Reset Guide

## Requirements
- MySQL 8.0.16+ (InnoDB with CHECK constraint support)
- Database collation: `utf8mb4_unicode_ci`

## Setup & Reset Command
Run the following command from the project root whenever you need a clean database reset:

```bash
mysql -u root -p < db/schema.sql
mysql -u root -p < db/seed.sql
```

## Seed Credentials
| Role | Username | Password | Notes |
|---|---|---|---|
| **Admin** | `admin` | `Admin@123` | Platform Administrator |
| **Trader (Fresh)** | `rahul` | `Demo@123` | Fresh account with ₹100,000 cash for live demo |
| **Trader (Pre-built)** | `priya` | `Demo@123` | 3 holdings, 4 trades, ₹600 realized P&L |
