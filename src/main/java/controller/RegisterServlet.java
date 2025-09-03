package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import dao.UserDAO;
import utils.PasswordUtil;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("first-name");
        String lastName = request.getParameter("last-name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String likedGenre = request.getParameter("signup-genre");

        // Hashing the Password using Bcrypt
        String passwordHash = PasswordUtil.hashPassword(password);

        User user = new User(firstName, lastName, email, passwordHash);
        user.addLikedGenre(likedGenre);

        try {
            UserDAO.addUser(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("User added Successfully");

        response.sendRedirect("login.jsp");

    }

}
