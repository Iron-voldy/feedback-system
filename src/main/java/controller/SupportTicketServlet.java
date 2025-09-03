package controller;

import dao.SupportTicketDAO;
import dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.SupportTicket;
import model.SupportResponse;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "SupportTicketServlet", value = "/SupportTicketServlet")
public class SupportTicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "userTickets";
        }

        try {
            switch (action) {
                case "userTickets":
                    viewUserTickets(request, response);
                    break;
                case "viewTicket":
                    viewTicketDetails(request, response);
                    break;
                case "showCreateForm":
                    showCreateTicketForm(request, response);
                    break;
                case "adminAllTickets":
                    adminViewAllTickets(request, response);
                    break;
                case "adminTicketsByStatus":
                    adminViewTicketsByStatus(request, response);
                    break;
                case "adminViewTicket":
                    adminViewTicketDetails(request, response);
                    break;
                default:
                    viewUserTickets(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            switch (action) {
                case "createTicket":
                    createTicket(request, response);
                    break;
                case "addResponse":
                    addResponse(request, response);
                    break;
                case "updateStatus":
                    updateTicketStatus(request, response);
                    break;
                case "assignTicket":
                    assignTicket(request, response);
                    break;
                case "updatePriority":
                    updateTicketPriority(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    // View current user's tickets
    private void viewUserTickets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get user's tickets
        List<SupportTicket> userTickets = SupportTicketDAO.getTicketsByUserId(currentUser.getUserId());

        request.setAttribute("userTickets", userTickets);

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/support/userTickets.jsp");
        dispatcher.forward(request, response);
    }

    // View detailed ticket information with responses
    private void viewTicketDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String ticketIdParam = request.getParameter("ticketId");
        if (ticketIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=userTickets");
            return;
        }

        int ticketId = Integer.parseInt(ticketIdParam);

        // Get ticket details
        SupportTicket ticket = SupportTicketDAO.findTicketById(ticketId);

        if (ticket == null) {
            request.setAttribute("error", "Ticket not found.");
            viewUserTickets(request, response);
            return;
        }

        // Check if user owns this ticket or is admin
        if (ticket.getUserId() != currentUser.getUserId() && !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=userTickets");
            return;
        }

        request.setAttribute("ticket", ticket);

        // Forward to appropriate JSP based on user type
        if (currentUser.isAdmin()) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/ticketDetails.jsp");
            dispatcher.forward(request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/support/ticketDetails.jsp");
            dispatcher.forward(request, response);
        }
    }

    // Show create ticket form
    private void showCreateTicketForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Forward to create ticket form
        RequestDispatcher dispatcher = request.getRequestDispatcher("/support/createTicket.jsp");
        dispatcher.forward(request, response);
    }

    // Create a new support ticket
    private void createTicket(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            String ticketType = request.getParameter("ticketType");
            String subject = request.getParameter("subject");
            String description = request.getParameter("description");
            String priority = request.getParameter("priority");

            // Validate input
            if (ticketType == null || subject == null || description == null ||
                    ticketType.trim().isEmpty() || subject.trim().isEmpty() || description.trim().isEmpty()) {
                request.setAttribute("error", "All fields are required.");
                showCreateTicketForm(request, response);
                return;
            }

            if (priority == null || priority.trim().isEmpty()) {
                priority = "medium";
            }

            // Create new ticket
            SupportTicket ticket = new SupportTicket(currentUser.getUserId(), ticketType, subject, description);
            ticket.setPriority(priority);
            ticket.setCreatedDate(LocalDateTime.now());
            ticket.setLastUpdated(LocalDateTime.now());

            // Save ticket
            boolean success = SupportTicketDAO.createTicket(ticket);

            if (success) {
                request.setAttribute("success", "Your support ticket has been created successfully. Ticket ID: " + ticket.getTicketId());
                response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=userTickets");
            } else {
                request.setAttribute("error", "Failed to create support ticket. Please try again.");
                showCreateTicketForm(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "An error occurred while creating your ticket.");
            e.printStackTrace();
            showCreateTicketForm(request, response);
        }
    }

    // Add response to a ticket
    private void addResponse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            String responseText = request.getParameter("responseText");

            if (responseText == null || responseText.trim().isEmpty()) {
                request.setAttribute("error", "Response text cannot be empty.");
                response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=viewTicket&ticketId=" + ticketId);
                return;
            }

            // Get ticket to verify permissions
            SupportTicket ticket = SupportTicketDAO.findTicketById(ticketId);

            if (ticket == null) {
                request.setAttribute("error", "Ticket not found.");
                response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=userTickets");
                return;
            }

            // Check permissions
            if (ticket.getUserId() != currentUser.getUserId() && !currentUser.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=userTickets");
                return;
            }

            // Create response
            SupportResponse supportResponse = new SupportResponse(ticketId, currentUser.getUserId(),
                    responseText, currentUser.isAdmin());
            supportResponse.setResponseDate(LocalDateTime.now());

            // Save response
            boolean success = SupportTicketDAO.addResponseToTicket(supportResponse);

            if (success) {
                // If it's a customer response, change status to 'open' (customer replied)
                if (!currentUser.isAdmin() && !ticket.getStatus().equals("open")) {
                    SupportTicketDAO.updateTicketStatus(ticketId, "open");
                }
                // If it's an admin response, change status to 'in_progress'
                else if (currentUser.isAdmin() && ticket.getStatus().equals("open")) {
                    SupportTicketDAO.updateTicketStatus(ticketId, "in_progress");
                }

                request.setAttribute("success", "Response added successfully.");
            } else {
                request.setAttribute("error", "Failed to add response.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid ticket ID.");
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred while adding response.");
            e.printStackTrace();
        }

        // Redirect back to ticket details
        String ticketId = request.getParameter("ticketId");
        response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=viewTicket&ticketId=" + ticketId);
    }

    // Admin: View all tickets
    private void adminViewAllTickets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Get all tickets
        List<SupportTicket> allTickets = SupportTicketDAO.getAllTickets();

        // Get statistics
        int openTickets = SupportTicketDAO.getTicketCountByStatus("open");
        int inProgressTickets = SupportTicketDAO.getTicketCountByStatus("in_progress");
        int resolvedTickets = SupportTicketDAO.getTicketCountByStatus("resolved");
        int closedTickets = SupportTicketDAO.getTicketCountByStatus("closed");

        request.setAttribute("allTickets", allTickets);
        request.setAttribute("openTickets", openTickets);
        request.setAttribute("inProgressTickets", inProgressTickets);
        request.setAttribute("resolvedTickets", resolvedTickets);
        request.setAttribute("closedTickets", closedTickets);

        // Forward to admin JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/allTickets.jsp");
        dispatcher.forward(request, response);
    }

    // Admin: View tickets by status
    private void adminViewTicketsByStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String status = request.getParameter("status");
        if (status == null) {
            status = "open";
        }

        // Get tickets by status
        List<SupportTicket> tickets = SupportTicketDAO.getTicketsByStatus(status);

        request.setAttribute("tickets", tickets);
        request.setAttribute("filterStatus", status);

        // Forward to admin JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/ticketsByStatus.jsp");
        dispatcher.forward(request, response);
    }

    // Admin: View ticket details with admin actions
    private void adminViewTicketDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        viewTicketDetails(request, response); // Reuse the existing method
    }

    // Admin: Update ticket status
    private void updateTicketStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            String status = request.getParameter("status");

            boolean success = SupportTicketDAO.updateTicketStatus(ticketId, status);

            if (success) {
                request.setAttribute("success", "Ticket status updated successfully.");
            } else {
                request.setAttribute("error", "Failed to update ticket status.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid ticket ID.");
        }

        // Redirect back to ticket details
        String ticketId = request.getParameter("ticketId");
        response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=adminViewTicket&ticketId=" + ticketId);
    }

    // Admin: Assign ticket to admin
    private void assignTicket(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            int adminId = Integer.parseInt(request.getParameter("adminId"));

            boolean success = SupportTicketDAO.assignTicketToAdmin(ticketId, adminId);

            if (success) {
                request.setAttribute("success", "Ticket assigned successfully.");
            } else {
                request.setAttribute("error", "Failed to assign ticket.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid ID provided.");
        }

        // Redirect back to ticket details
        String ticketId = request.getParameter("ticketId");
        response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=adminViewTicket&ticketId=" + ticketId);
    }

    // Admin: Update ticket priority
    private void updateTicketPriority(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            String priority = request.getParameter("priority");

            boolean success = SupportTicketDAO.updateTicketPriority(ticketId, priority);

            if (success) {
                request.setAttribute("success", "Ticket priority updated successfully.");
            } else {
                request.setAttribute("error", "Failed to update ticket priority.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid ticket ID.");
        }

        // Redirect back to ticket details
        String ticketId = request.getParameter("ticketId");
        response.sendRedirect(request.getContextPath() + "/SupportTicketServlet?action=adminViewTicket&ticketId=" + ticketId);
    }
}