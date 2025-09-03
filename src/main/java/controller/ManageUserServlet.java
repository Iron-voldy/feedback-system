package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Admin;
import model.User;
import dao.UserDAO;
import utils.PasswordUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@WebServlet("/manageUser")
public class ManageUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phoneNumber");
        String password = request.getParameter("editPassword");
        String role = request.getParameter("role");

        switch (action) {
            case "add":
                addUser(firstName, lastName, email, password, role);
                break;
            case "update":
                // TO DO
                break;
            case "delete":
                // TO DO
                break;
        }

        response.sendRedirect(request.getContextPath() + "/manageUser");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LinkedList<User> allUsers = new LinkedList<>();

        try {
            allUsers = UserDAO.getUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("allUsers", allUsers);
        RequestDispatcher rd = req.getRequestDispatcher("/admin/ManageUsers.jsp");
        rd.forward(req, resp);
    }

    private void addUser(String firstName,
                         String lastName,
                         String email,
                         String password,
                         String role) {

        String passwordHash = PasswordUtil.hashPassword(password);

        switch (role) {
            case "admin" :
                try {
                    Admin admin = new Admin(firstName, lastName, email, passwordHash);
                    UserDAO.addUser(admin);
                    break;
                } catch (SQLException | IOException e) {
                    System.out.println("SQL Issue! Admin User not Added! ");
                }
            case "user":
                try {
                    User user = new User(firstName, lastName, email, passwordHash);
                    UserDAO.addUser(user);
                    break;
                } catch (SQLException | IOException e){
                    System.out.println("SQL Issue! User not Added! ");
            }
        }

    }

}
