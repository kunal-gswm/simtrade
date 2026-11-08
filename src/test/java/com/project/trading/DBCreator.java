package com.project.trading;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class DBCreator {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (InputStream in = DBCreator.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        }
        String fullUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/papertrade_test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Kolkata");
        String user = props.getProperty("db.user", "root");
        String p = props.getProperty("db.password", "root");

        // Parse baseUrl without target database name to allow DROP/CREATE DATABASE
        String baseUrl = fullUrl;
        int slashIdx = fullUrl.indexOf("3306/");
        if (slashIdx != -1) {
            int qIdx = fullUrl.indexOf("?", slashIdx);
            baseUrl = fullUrl.substring(0, slashIdx + 5) + (qIdx != -1 ? fullUrl.substring(qIdx) : "");
        }

        try (Connection c = DriverManager.getConnection(baseUrl, user, p);
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
        List<String> lines = Files.readAllLines(Paths.get(path));
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--") && 
                !trimmed.toLowerCase().startsWith("use papertrade") &&
                !trimmed.toLowerCase().startsWith("create database papertrade") &&
                !trimmed.toLowerCase().startsWith("drop database if exists papertrade")) {
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
