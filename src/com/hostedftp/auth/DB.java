package com.hostedftp.auth;

import java.sql.*;
import java.util.Optional;

public class DB {
    // Reads JDBC URL/credentials from environment variables for portability:
    // DB_URL, DB_USER, DB_PASS
    // Example DB_URL: jdbc:mysql://localhost:3306/demo?useSSL=false&allowPublicKeyRetrieval=true
    private static String url() { return getenvOr("DB_URL", "jdbc:mysql://localhost:3306/demo?useSSL=false&allowPublicKeyRetrieval=true"); }
    private static String user() { return getenvOr("DB_USER", "root"); }
    private static String pass() { return getenvOr("DB_PASS", "dfvgo@123!"); }
    
    static {
    	// static check ensure that driver is loaded.
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver"); 
    	} catch (ClassNotFoundException e) {
    	    throw new RuntimeException("MySQL JDBC driver not found in WEB-INF/lib", e);
    	}
    }

    private static String getenvOr(String k, String defv) {
        String v = System.getenv(k);
        return (v == null || v.isEmpty()) ? defv : v;
    }

    public static Connection conn() throws SQLException {
        return DriverManager.getConnection(url(), user(), pass());
    }

    public static void ensureSchemaAndSeed() throws SQLException {
        try (Connection c = conn(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "full_name VARCHAR(100)," +
                    "password_salt VARCHAR(255) NOT NULL," +
                    "password_hash VARCHAR(255) NOT NULL," +
                    "iterations INT NOT NULL," +
                    "alg VARCHAR(64) NOT NULL" +
                    ")");
        }

        // Seed default user if missing: hostedftp / money
        if (!usernameExists("hostedftp")) {
            String[] hp = PasswordUtil.hashPassword("money".toCharArray());
            try (Connection c = conn();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users (username, full_name, password_salt, password_hash, iterations, alg) VALUES (?,?,?,?,?,?)")) {
                ps.setString(1, "hostedftp");
                ps.setString(2, "HostedFTP QA");
                ps.setString(3, hp[0]);
                ps.setString(4, hp[1]);
                ps.setInt(5, Integer.parseInt(hp[2]));
                ps.setString(6, hp[3]);
                ps.executeUpdate();
            }
        }
    }

    public static boolean usernameExists(String username) throws SQLException {
        try (Connection c = conn();
            PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public static Optional<User> findUser(String username) throws SQLException {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, username, full_name, password_salt, password_hash, iterations, alg FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                User u = new User();
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
                u.fullName = rs.getString("full_name");
                u.salt = rs.getString("password_salt");
                u.hash = rs.getString("password_hash");
                u.iterations = rs.getInt("iterations");
                u.alg = rs.getString("alg");
                return Optional.of(u);
            }
        }
    }

    public static class User {
        public int id;
        public String username;
        public String fullName;
        public String salt;
        public String hash;
        public int iterations;
        public String alg;
    }
}