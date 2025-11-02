package com.hostedftp.auth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.sql.SQLException;

public class InitServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        try {
            // Ensure table exists and seed default user
            DB.ensureSchemaAndSeed();
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}