package com.hostedftp.auth;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Missing username or password.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try {
            Optional<DB.User> ou = DB.findUser(username);
            if (ou.isEmpty()) {
                req.setAttribute("error", "Invalid credentials.");
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                return;
            }
            DB.User u = ou.get();
            boolean ok = PasswordUtil.verifyPassword(password.toCharArray(), u.salt, u.hash, u.iterations, u.alg);
            if (!ok) {
                req.setAttribute("error", "Invalid credentials.");
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("username", u.username);
            session.setAttribute("full_name", u.fullName);
            resp.sendRedirect(req.getContextPath() + "/welcome.jsp");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}