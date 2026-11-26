package com.project.trading.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Lightweight, safe schema migration utility to guarantee all SimTrade 3.0
 * tables and constraints exist on application startup.
 */
public class DatabaseMigrator {

    public static void migrate() {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement()) {

            if (!tableExists(c, "orders")) {
                s.execute(
                    "CREATE TABLE orders (" +
                    "    id BIGINT NOT NULL AUTO_INCREMENT," +
                    "    user_id BIGINT NOT NULL," +
                    "    stock_id BIGINT NOT NULL," +
                    "    side ENUM('BUY','SELL') NOT NULL," +
                    "    order_type ENUM('MARKET','LIMIT','STOP_LOSS_MARKET','STOP_LOSS_LIMIT','TARGET','BRACKET') NOT NULL," +
                    "    quantity INT NOT NULL," +
                    "    filled_quantity INT NOT NULL DEFAULT 0," +
                    "    remaining_quantity INT NOT NULL," +
                    "    limit_price DECIMAL(12,2) NULL," +
                    "    trigger_price DECIMAL(12,2) NULL," +
                    "    target_price DECIMAL(12,2) NULL," +
                    "    stop_loss_price DECIMAL(12,2) NULL," +
                    "    average_fill_price DECIMAL(12,2) NULL," +
                    "    status ENUM('PENDING','TRIGGER_PENDING','OPEN','PARTIALLY_FILLED','FILLED','CANCELLED','REJECTED','EXPIRED') NOT NULL DEFAULT 'PENDING'," +
                    "    validity ENUM('DAY','GTC','IOC') NOT NULL DEFAULT 'DAY'," +
                    "    parent_order_id BIGINT NULL," +
                    "    bracket_role ENUM('ENTRY','TARGET_EXIT','STOP_LOSS_EXIT') NULL," +
                    "    idempotency_key VARCHAR(64) NULL," +
                    "    rejection_reason VARCHAR(255) NULL," +
                    "    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "    expiry_time TIMESTAMP NULL," +
                    "    PRIMARY KEY (id)," +
                    "    UNIQUE KEY uq_orders_idempotency (idempotency_key)," +
                    "    KEY idx_orders_user_status (user_id, status)," +
                    "    KEY idx_orders_stock_status (stock_id, status)," +
                    "    KEY idx_orders_parent (parent_order_id)," +
                    "    KEY idx_orders_status (status)," +
                    "    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT," +
                    "    CONSTRAINT fk_orders_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT," +
                    "    CONSTRAINT fk_orders_parent FOREIGN KEY (parent_order_id) REFERENCES orders(id) ON DELETE CASCADE," +
                    "    CONSTRAINT chk_orders_qty CHECK (quantity > 0)," +
                    "    CONSTRAINT chk_orders_filled_qty CHECK (filled_quantity >= 0 AND filled_quantity <= quantity)," +
                    "    CONSTRAINT chk_orders_rem_qty CHECK (remaining_quantity >= 0 AND remaining_quantity <= quantity)" +
                    ") ENGINE=InnoDB"
                );
            }

            if (!tableExists(c, "order_events")) {
                s.execute(
                    "CREATE TABLE order_events (" +
                    "    id BIGINT NOT NULL AUTO_INCREMENT," +
                    "    order_id BIGINT NOT NULL," +
                    "    from_status VARCHAR(30) NULL," +
                    "    to_status VARCHAR(30) NOT NULL," +
                    "    reason VARCHAR(255) NULL," +
                    "    event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "    PRIMARY KEY (id)," +
                    "    KEY idx_order_events_order (order_id, event_time)," +
                    "    CONSTRAINT fk_order_events_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB"
                );
            }

            if (!tableExists(c, "fills")) {
                s.execute(
                    "CREATE TABLE fills (" +
                    "    id BIGINT NOT NULL AUTO_INCREMENT," +
                    "    order_id BIGINT NOT NULL," +
                    "    user_id BIGINT NOT NULL," +
                    "    stock_id BIGINT NOT NULL," +
                    "    fill_quantity INT NOT NULL," +
                    "    fill_price DECIMAL(12,2) NOT NULL," +
                    "    total_amount DECIMAL(15,2) NOT NULL," +
                    "    slippage DECIMAL(12,4) NOT NULL DEFAULT 0.0000," +
                    "    simulated_brokerage DECIMAL(10,2) NOT NULL DEFAULT 0.00," +
                    "    simulated_taxes DECIMAL(10,2) NOT NULL DEFAULT 0.00," +
                    "    fill_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "    PRIMARY KEY (id)," +
                    "    KEY idx_fills_order (order_id)," +
                    "    KEY idx_fills_user (user_id, fill_time)," +
                    "    KEY idx_fills_stock (stock_id)," +
                    "    CONSTRAINT fk_fills_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE RESTRICT," +
                    "    CONSTRAINT fk_fills_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT," +
                    "    CONSTRAINT fk_fills_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT," +
                    "    CONSTRAINT chk_fills_qty CHECK (fill_quantity > 0)," +
                    "    CONSTRAINT chk_fills_price CHECK (fill_price > 0)" +
                    ") ENGINE=InnoDB"
                );
            }

            if (!tableExists(c, "reserved_funds")) {
                s.execute(
                    "CREATE TABLE reserved_funds (" +
                    "    id BIGINT NOT NULL AUTO_INCREMENT," +
                    "    order_id BIGINT NOT NULL," +
                    "    user_id BIGINT NOT NULL," +
                    "    reserved_amount DECIMAL(15,2) NOT NULL," +
                    "    status ENUM('RESERVED','COMMITTED','RELEASED') NOT NULL DEFAULT 'RESERVED'," +
                    "    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "    PRIMARY KEY (id)," +
                    "    UNIQUE KEY uq_res_funds_order (order_id)," +
                    "    KEY idx_res_funds_user_status (user_id, status)," +
                    "    CONSTRAINT fk_res_funds_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE," +
                    "    CONSTRAINT fk_res_funds_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT," +
                    "    CONSTRAINT chk_res_funds_amt CHECK (reserved_amount >= 0)" +
                    ") ENGINE=InnoDB"
                );
            }

            if (!tableExists(c, "reserved_holdings")) {
                s.execute(
                    "CREATE TABLE reserved_holdings (" +
                    "    id BIGINT NOT NULL AUTO_INCREMENT," +
                    "    order_id BIGINT NOT NULL," +
                    "    user_id BIGINT NOT NULL," +
                    "    stock_id BIGINT NOT NULL," +
                    "    reserved_quantity INT NOT NULL," +
                    "    status ENUM('RESERVED','COMMITTED','RELEASED') NOT NULL DEFAULT 'RESERVED'," +
                    "    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "    PRIMARY KEY (id)," +
                    "    UNIQUE KEY uq_res_holdings_order (order_id)," +
                    "    KEY idx_res_holdings_user_stock (user_id, stock_id, status)," +
                    "    CONSTRAINT fk_res_holdings_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE," +
                    "    CONSTRAINT fk_res_holdings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT," +
                    "    CONSTRAINT fk_res_holdings_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT," +
                    "    CONSTRAINT chk_res_holdings_qty CHECK (reserved_quantity > 0)" +
                    ") ENGINE=InnoDB"
                );
            }

        } catch (SQLException e) {
            System.err.println("[DatabaseMigrator] Migration warning: " + e.getMessage());
        }
    }

    private static boolean tableExists(Connection c, String tableName) throws SQLException {
        try (ResultSet rs = c.getMetaData().getTables(c.getCatalog(), null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }
}
