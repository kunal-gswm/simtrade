package com.project.trading.dao;

import com.project.trading.model.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class UserDAO {
    public User findByIdForUpdate(Connection c, long id) throws SQLException {
        return null;
    }
    public void adjustCash(Connection c, long id, BigDecimal delta) throws SQLException {
    }
}
