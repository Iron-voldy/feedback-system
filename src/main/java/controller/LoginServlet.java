package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import dao.UserDAO;
import utils.PasswordUtil;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        boolean remember = Boolean.parseBoolean(request.getParameter("remember-me"));

        if (email == null || password == null) {
            response.sendRedirect("login.jsp");
        }

        User loginUser = null;

        try {
            loginUser = UserDAO.findUserByEmail(email);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (loginUser == null) {
            request.setAttribute("error", "Incorrect Email or Password");
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.forward(request, response);
        } else if (PasswordUtil.checkPassword(password, loginUser.getPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("USER", loginUser);

            if (remember) {
                Cookie cookie = new Cookie("userId", String.valueOf(loginUser.getUserId()));
                cookie.setMaxAge(60 * 60 * 24 * 7);
                response.addCookie(cookie);
            }

            response.sendRedirect("index.jsp");
        } else {
            request.setAttribute("error", "Wrong password");
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.forward(request, response);
        }
    }
}
