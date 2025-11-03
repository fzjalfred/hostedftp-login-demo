package com.hostedftp.auth;

import java.sql.*;
import java.util.Optional;
import java.util.Properties;

import javax.servlet.ServletException;

public class DB {
    // Reads JDBC URL/credentials from config variables for portability:
    // Example DB_URL: jdbc:mysql://localhost:3306/demo?useSSL=false&allowPublicKeyRetrieval=true
    private static String url() { return getenvOr("DB_URL", DbConfig.url()); }
    private static String user() { return getenvOr("DB_USER", DbConfig.user()); }
    private static String pass() { return getenvOr("DB_PASS", DbConfig.pass()); }
    
    static {
    	// static check ensure that driver is loaded.
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver"); 
    		ensureSchemaAndSeed();
    	} catch (ClassNotFoundException e) {
    	    throw new RuntimeException("MySQL JDBC driver not found in WEB-INF/lib", e);
    	} catch (SQLException e) {
    		e.printStackTrace();
    		throw new RuntimeException("ensureSchemaAndSeed failed", e);
        }
    }

    private static String getenvOr(String k, String defv) {
        String v = System.getenv(k);
        return (v == null || v.isEmpty()) ? defv : v;
    }

    public static Connection conn() throws SQLException {
        return DriverManager.getConnection(url(), user(), pass());
    }

    // Dummy code to insert database for testing.
    public static void ensureSchemaAndSeed() throws SQLException {
        try (Connection c = conn(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "full_name VARCHAR(100)," +
                    "password_salt VARCHAR(255) NOT NULL," +
                    "password_hash VARCHAR(255) NOT NULL," +
                    "iterations INT NOT NULL," +
                    "alg VARCHAR(64) NOT NULL," +
                    "login_count INT NOT NULL DEFAULT 0," +
                    "last_login_at TIMESTAMP NULL" +
                    ")");
        }

        // Seed default user if missing: hostedftp / money
        if (!usernameExists("hostedftp")) {
            String[] hp = PasswordUtil.hashPassword("money".toCharArray());
            try (Connection c = conn();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users (username, full_name, password_salt, password_hash, iterations, alg, login_count, last_login_at) VALUES (?,?,?,?,?,?,?,?)")) {
                ps.setString(1, "hostedftp");
                ps.setString(2, "HostedFTP QA");
                ps.setString(3, hp[0]);
                ps.setString(4, hp[1]);
                ps.setInt(5, Integer.parseInt(hp[2]));
                ps.setString(6, hp[3]);
                ps.setInt(7, 0);
                ps.setTimestamp(8, null);
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
                     "SELECT id, username, full_name, password_salt, password_hash, iterations, alg, login_count FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                //Success
                User u = new User();
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
                u.fullName = rs.getString("full_name");
                u.salt = rs.getString("password_salt");
                u.hash = rs.getString("password_hash");
                u.iterations = rs.getInt("iterations");
                u.alg = rs.getString("alg");
                u.login_count = rs.getInt("login_count");
                u.last_login_at = Timestamp.from(java.time.Instant.now());
                
                try (PreparedStatement ps_update = c.prepareStatement(
              	       "UPDATE users SET login_count = ?, last_login_at = ? WHERE id = ?")) {
                	ps_update.setInt(1, u.login_count + 1);
                    ps_update.setTimestamp(2, u.last_login_at);
                    ps_update.setInt(3, u.id);
                    ps_update.executeUpdate();
                }
                
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
        public int login_count;
        public java.sql.Timestamp last_login_at; 
    }
}