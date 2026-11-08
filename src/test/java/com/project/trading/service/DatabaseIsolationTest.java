package com.project.trading.service;

import com.project.trading.util.DBConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseIsolationTest {

    @Test
    public void testActiveDatabaseIsIsolatedTestDatabase() throws Exception {
        try (Connection c = DBConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DATABASE()")) {
            assertTrue(rs.next(), "Database query must return current catalog");
            String activeDb = rs.getString(1);
            assertEquals("papertrade_test", activeDb, 
                    "Tests must execute exclusively against papertrade_test. Development database (papertrade) must never be touched!");
        }
    }
}
