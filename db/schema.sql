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

CREATE TABLE candles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    stock_id BIGINT NOT NULL,
    timeframe VARCHAR(10) NOT NULL,
    open_price DECIMAL(12,2) NOT NULL,
    high_price DECIMAL(12,2) NOT NULL,
    low_price DECIMAL(12,2) NOT NULL,
    close_price DECIMAL(12,2) NOT NULL,
    volume BIGINT NOT NULL DEFAULT 0,
    open_time TIMESTAMP NOT NULL,
    is_closed TINYINT(1) NOT NULL DEFAULT 0,
    source VARCHAR(30) NOT NULL DEFAULT 'SIMULATED_STREAM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_candles_stock_tf_time (stock_id, timeframe, open_time),
    KEY idx_candles_lookup (stock_id, timeframe, open_time ASC),
    KEY idx_candles_stock_tf_closed (stock_id, timeframe, is_closed),
    CONSTRAINT fk_candles_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE,
    CONSTRAINT chk_candles_prices CHECK (open_price > 0 AND high_price >= low_price AND high_price >= open_price AND high_price >= close_price AND low_price <= open_price AND low_price <= close_price),
    CONSTRAINT chk_candles_volume CHECK (volume >= 0)
) ENGINE=InnoDB;

-- ============================================================
-- SimTrade 3.0: Full Paper Order Domain, Executions & Reservations
-- ============================================================

CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    side ENUM('BUY','SELL') NOT NULL,
    order_type ENUM('MARKET','LIMIT','STOP_LOSS_MARKET','STOP_LOSS_LIMIT','TARGET','BRACKET') NOT NULL,
    quantity INT NOT NULL,
    filled_quantity INT NOT NULL DEFAULT 0,
    remaining_quantity INT NOT NULL,
    limit_price DECIMAL(12,2) NULL,
    trigger_price DECIMAL(12,2) NULL,
    target_price DECIMAL(12,2) NULL,
    stop_loss_price DECIMAL(12,2) NULL,
    average_fill_price DECIMAL(12,2) NULL,
    status ENUM('PENDING','TRIGGER_PENDING','OPEN','PARTIALLY_FILLED','FILLED','CANCELLED','REJECTED','EXPIRED') NOT NULL DEFAULT 'PENDING',
    validity ENUM('DAY','GTC','IOC') NOT NULL DEFAULT 'DAY',
    parent_order_id BIGINT NULL,
    bracket_role ENUM('ENTRY','TARGET_EXIT','STOP_LOSS_EXIT') NULL,
    idempotency_key VARCHAR(64) NULL,
    rejection_reason VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expiry_time TIMESTAMP NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_orders_idempotency (idempotency_key),
    KEY idx_orders_user_status (user_id, status),
    KEY idx_orders_stock_status (stock_id, status),
    KEY idx_orders_parent (parent_order_id),
    KEY idx_orders_status (status),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_parent FOREIGN KEY (parent_order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT chk_orders_qty CHECK (quantity > 0),
    CONSTRAINT chk_orders_filled_qty CHECK (filled_quantity >= 0 AND filled_quantity <= quantity),
    CONSTRAINT chk_orders_rem_qty CHECK (remaining_quantity >= 0 AND remaining_quantity <= quantity)
) ENGINE=InnoDB;

CREATE TABLE order_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    from_status VARCHAR(30) NULL,
    to_status VARCHAR(30) NOT NULL,
    reason VARCHAR(255) NULL,
    event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_order_events_order (order_id, event_time),
    CONSTRAINT fk_order_events_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE fills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    fill_quantity INT NOT NULL,
    fill_price DECIMAL(12,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    slippage DECIMAL(12,4) NOT NULL DEFAULT 0.0000,
    simulated_brokerage DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    simulated_taxes DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    fill_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_fills_order (order_id),
    KEY idx_fills_user (user_id, fill_time),
    KEY idx_fills_stock (stock_id),
    CONSTRAINT fk_fills_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fills_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fills_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT,
    CONSTRAINT chk_fills_qty CHECK (fill_quantity > 0),
    CONSTRAINT chk_fills_price CHECK (fill_price > 0)
) ENGINE=InnoDB;

CREATE TABLE reserved_funds (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reserved_amount DECIMAL(15,2) NOT NULL,
    status ENUM('RESERVED','COMMITTED','RELEASED') NOT NULL DEFAULT 'RESERVED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_res_funds_order (order_id),
    KEY idx_res_funds_user_status (user_id, status),
    CONSTRAINT fk_res_funds_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_res_funds_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_res_funds_amt CHECK (reserved_amount >= 0)
) ENGINE=InnoDB;

CREATE TABLE reserved_holdings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    reserved_quantity INT NOT NULL,
    status ENUM('RESERVED','COMMITTED','RELEASED') NOT NULL DEFAULT 'RESERVED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_res_holdings_order (order_id),
    KEY idx_res_holdings_user_stock (user_id, stock_id, status),
    CONSTRAINT fk_res_holdings_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_res_holdings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_res_holdings_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT,
    CONSTRAINT chk_res_holdings_qty CHECK (reserved_quantity > 0)
) ENGINE=InnoDB;


