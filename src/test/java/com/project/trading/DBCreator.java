package com.project.trading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBCreator {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Kolkata";
        String user = "root";
        String p = "root";

        try (Connection c = DriverManager.getConnection(url, user, p);
             Statement s = c.createStatement()) {
             
            System.out.println("Creating database papertrade_test...");
            s.execute("DROP DATABASE IF EXISTS papertrade_test");
            s.execute("CREATE DATABASE papertrade_test COLLATE utf8mb4_unicode_ci");
            s.execute("USE papertrade_test");

            System.out.println("Loading schema...");
            executeScript(s, "db/schema.sql");
            
            System.out.println("Loading seed data...");
            executeScript(s, "db/seed.sql");
            
            System.out.println("Database setup complete.");
        }
    }

    private static void executeScript(Statement s, String path) throws Exception {
        java.util.List<String> lines = Files.readAllLines(Paths.get(path));
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().startsWith("--")) {
                sb.append(line).append("\n");
            }
        }
        String content = sb.toString();
        String[] queries = content.split(";");
        for (String q : queries) {
            if (!q.trim().isEmpty()) {
                s.execute(q.trim());
            }
        }
    }
}
