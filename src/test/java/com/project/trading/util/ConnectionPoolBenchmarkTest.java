package com.project.trading.util;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionPoolBenchmarkTest {

    @Test
    void benchmarkHikariVsDriverManager() throws Exception {
        int iterations = 30;

        // 1. Warm up Hikari pool
        try (Connection c = DBConnection.getConnection()) {
            assertNotNull(c);
        }

        // 2. Measure HikariCP
        long startHikari = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try (Connection c = DBConnection.getConnection();
                 Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery("SELECT 1")) {
                assertTrue(rs.next());
            }
        }
        long hikariDurationMs = (System.nanoTime() - startHikari) / 1_000_000;

        // 3. Measure raw DriverManager
        String url = "jdbc:mysql://localhost:3306/papertrade_test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Kolkata";
        String user = "root";
        String pass = "root";

        long startDriver = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try (Connection c = DriverManager.getConnection(url, user, pass);
                 Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery("SELECT 1")) {
                assertTrue(rs.next());
            }
        }
        long driverDurationMs = (System.nanoTime() - startDriver) / 1_000_000;

        System.out.println("=== Connection Acquisition & Query Benchmark (" + iterations + " iterations) ===");
        System.out.println("HikariCP duration:      " + hikariDurationMs + " ms (" + (hikariDurationMs / (double) iterations) + " ms/op)");
        System.out.println("DriverManager duration: " + driverDurationMs + " ms (" + (driverDurationMs / (double) iterations) + " ms/op)");
        System.out.println("Speedup factor:         " + String.format("%.2f", (double) driverDurationMs / Math.max(1, hikariDurationMs)) + "x");

        assertTrue(hikariDurationMs < driverDurationMs, "HikariCP should be strictly faster than raw DriverManager");
    }
}
