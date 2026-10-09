-- Paper Trading Platform: schema (MySQL 8.0.16+ required: CHECK constraints are enforced from this version)
DROP DATABASE IF EXISTS papertrade;
CREATE DATABASE papertrade CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE papertrade;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    status ENUM('ACTIVE','BLOCKED') NOT NULL DEFAULT 'ACTIVE',
    cash_balance DECIMAL(15,2) NOT NULL DEFAULT 100000.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_email (email),
    CONSTRAINT chk_users_cash CHECK (cash_balance >= 0)
) ENGINE=InnoDB;

CREATE TABLE stocks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    symbol VARCHAR(15) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    sector VARCHAR(50) NULL,
    description VARCHAR(500) NULL,
    price DECIMAL(12,2) NOT NULL,
    prev_price DECIMAL(12,2) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_stocks_symbol (symbol),
    CONSTRAINT chk_stocks_price CHECK (price > 0),
    CONSTRAINT chk_stocks_prev CHECK (prev_price > 0)
) ENGINE=InnoDB;

CREATE TABLE holdings (
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    avg_buy_price DECIMAL(12,4) NOT NULL,
    PRIMARY KEY (user_id, stock_id),
    CONSTRAINT fk_holdings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_holdings_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT,
    CONSTRAINT chk_holdings_qty CHECK (quantity > 0),
    CONSTRAINT chk_holdings_avg CHECK (avg_buy_price > 0)
) ENGINE=InnoDB;

CREATE TABLE trades (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    trade_type ENUM('BUY','SELL') NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    realized_pnl DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_trades_user_time (user_id, executed_at),
    KEY idx_trades_stock (stock_id),
    CONSTRAINT fk_trades_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_trades_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT,
    CONSTRAINT chk_trades_qty CHECK (quantity > 0),
    CONSTRAINT chk_trades_price CHECK (price > 0)
) ENGINE=InnoDB;
