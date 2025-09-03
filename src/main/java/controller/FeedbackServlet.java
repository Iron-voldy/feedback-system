package controller;

import dao.FeedbackDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Feedback;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "FeedbackServlet", value = "/FeedbackServlet")
public class FeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "showFeedbackForm";
        }

        try {
            switch (action) {
                case "showFeedbackForm":
                    showFeedbackForm(request, response);
                    break;
                case "userFeedback":
                    viewUserFeedback(request, response);
                    break;
                case "adminAllFeedback":
                    adminViewAllFeedback(request, response);
                    break;
                case "adminFeedbackByType":
                    adminViewFeedbackByType(request, response);
                    break;
                case "adminUnreadFeedback":
                    adminViewUnreadFeedback(request, response);
                    break;
                case "adminFeedbackDetails":
                    adminViewFeedbackDetails(request, response);
                    break;
                case "markAsRead":
                    markFeedbackAsRead(request, response);
                    break;
                case "deleteFeedback":
                    deleteFeedback(request, response);
                    break;
                case "feedbackStats":
                    viewFeedbackStats(request, response);
                    break;
                default:
                    showFeedbackForm(request, response);
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
                case "submitFeedback":
                    submitFeedback(request, response);
                    break;
                case "addAdminNotes":
                    addAdminNotes(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    // Show feedback form (for both logged-in users and anonymous users)
    private void showFeedbackForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Forward to feedback form JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/feedback/feedbackForm.jsp");
        dispatcher.forward(request, response);
    }

    // Submit feedback
    private void submitFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        try {
            String feedbackType = request.getParameter("feedbackType");
            String subject = request.getParameter("subject");
            String message = request.getParameter("message");
            String ratingParam = request.getParameter("rating");
            String email = request.getParameter("email");

            // Validate required fields
            if (feedbackType == null || subject == null || message == null ||
                    feedbackType.trim().isEmpty() || subject.trim().isEmpty() || message.trim().isEmpty()) {
                request.setAttribute("error", "Feedback type, subject, and message are required fields.");
                showFeedbackForm(request, response);
                return;
            }

            int rating = 0;
            if (ratingParam != null && !ratingParam.trim().isEmpty()) {
                try {
                    rating = Integer.parseInt(ratingParam);
                    if (rating < 1 || rating > 5) {
                        request.setAttribute("error", "Rating must be between 1 and 5 stars.");
                        showFeedbackForm(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Invalid rating value.");
                    showFeedbackForm(request, response);
                    return;
                }
            }

            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("USER");

            Feedback feedback;

            if (currentUser != null) {
                // Logged-in user feedback
                feedback = new Feedback(currentUser.getUserId(), feedbackType, subject, message, rating);
            } else {
                // Anonymous feedback
                if (email == null || email.trim().isEmpty()) {
                    request.setAttribute("error", "Email is required for anonymous feedback.");
                    showFeedbackForm(request, response);
                    return;
                }

                // Basic email validation
                if (!email.contains("@") || !email.contains(".")) {
                    request.setAttribute("error", "Please enter a valid email address.");
                    showFeedbackForm(request, response);
                    return;
                }

                feedback = new Feedback(email, feedbackType, subject, message, rating);
            }

            feedback.setSubmittedDate(LocalDateTime.now());
            feedback.setRead(false);

            // Save feedback
            boolean success = FeedbackDAO.submitFeedback(feedback);

            if (success) {
                request.setAttribute("success", "Thank you for your feedback! We appreciate your input and will review it soon.");
                // Clear form by redirecting
                response.sendRedirect(request.getContextPath() + "/FeedbackServlet?action=showFeedbackForm&success=true");
            } else {
                request.setAttribute("error", "Failed to submit feedback. Please try again.");
                showFeedbackForm(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "An error occurred while submitting your feedback.");
            e.printStackTrace();
            showFeedbackForm(request, response);
        }
    }

    // View user's own feedback history
    private void viewUserFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get user's feedback
        List<Feedback> userFeedback = FeedbackDAO.getFeedbackByUserId(currentUser.getUserId());

        request.setAttribute("userFeedback", userFeedback);

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/feedback/userFeedback.jsp");
        dispatcher.forward(request, response);
    }

    // Admin: View all feedback
    private void adminViewAllFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Get all feedback
        List<Feedback> allFeedback = FeedbackDAO.getAllFeedback();

        // Get statistics
        int totalFeedback = allFeedback.size();
        int unreadCount = FeedbackDAO.getUnreadFeedbackCount();
        int bugReports = FeedbackDAO.getFeedbackCountByType("bug_report");
        int featureRequests = FeedbackDAO.getFeedbackCountByType("feature_request");
        int generalFeedback = FeedbackDAO.getFeedbackCountByType("general");
        double averageRating = FeedbackDAO.getAveragePlatformRating();

        request.setAttribute("allFeedback", allFeedback);
        request.setAttribute("totalFeedback", totalFeedback);
        request.setAttribute("unreadCount", unreadCount);
        request.setAttribute("bugReports", bugReports);
        request.setAttribute("featureRequests", featureRequests);
        request.setAttribute("generalFeedback", generalFeedback);
        request.setAttribute("averageRating", Math.round(averageRating * 100.0) / 100.0);

        // Forward to admin JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/allFeedback.jsp");
        dispatcher.forward(request, response);
    }

    // Admin: View feedback by type
    private void adminViewFeedbackByType(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String feedbackType = request.getParameter("type");
        if (feedbackType == null || feedbackType.trim().isEmpty()) {
            feedbackType = "general";
        }

        // Get feedback by type
        List<Feedback> feedbackList = FeedbackDAO.getFeedbackByType(feedbackType);

        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("filterType", feedbackType);

        // Forward to admin JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/feedbackByType.jsp");
        dispatcher.forward(request, response);
    }

    // Admin: View unread feedback
    private void adminViewUnreadFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Get unread feedback
        List<Feedback> unreadFeedback = FeedbackDAO.getUnreadFeedback();

        request.setAttribute("unreadFeedback", unreadFeedback);

        // Forward to admin JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/unreadFeedback.jsp");
        dispatcher.forward(request, response);
    }

    // Admin: View feedback details
    private void adminViewFeedbackDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String feedbackIdParam = request.getParameter("feedbackId");
        if (feedbackIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/FeedbackServlet?action=adminAllFeedback");
            return;
        }

        try {
            int feedbackId = Integer.parseInt(feedbackIdParam);

            // Get feedback details
            Feedback feedback = FeedbackDAO.findFeedbackById(feedbackId);

            if (feedback == null) {
                request.setAttribute("error", "Feedback not found.");
                response.sendRedirect(request.getContextPath() + "/FeedbackServlet?action=adminAllFeedback");
                return;
            }

            // Automatically mark as read when admin views details
            if (!feedback.isRead()) {
                FeedbackDAO.markFeedbackAsRead(feedbackId);
                feedback.setRead(true);
            }

            request.setAttribute("feedback", feedback);

            // Forward to admin JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/feedbackDetails.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid feedback ID.");
            response.sendRedirect(request.getContextPath() + "/FeedbackServlet?action=adminAllFeedback");
        }
    }

    // Admin: Mark feedback as read
    private void markFeedbackAsRead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String feedbackIdParam = request.getParameter("feedbackId");
        if (feedbackIdParam != null) {
            try {
                int feedbackId = Integer.parseInt(feedbackIdParam);
                boolean success = FeedbackDAO.markFeedbackAsRead(feedbackId);

                if (success) {
                    request.setAttribute("success", "Feedback marked as read.");
                } else {
                    request.setAttribute("error", "Failed to mark feedback as read.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid feedback ID.");
            }
        }

        // Redirect back to feedback list
        response.sendRedirect(request.getContextPath() + "/FeedbackServlet?action=adminAllFeedback");
    }

    // Admin: Add admin notes to feedback
    private void addAdminNotes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));
            String adminNotes = request.getParameter("adminNotes");

            if (adminNotes == null || adminNotes.trim().isEmpty()) {
                request.setAttribute("error", "Admin notes cannot be empty.");
            } else {
                boolean success = FeedbackDAO.addAdminNotes(feedbackId, adminNotes);

                if (success) {
                    request.setAttribute("success", "Admin notes added successfully.");
                } else {
                    request.setAttribute("error", "Failed to add admin notes.");
                }
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid feedback ID.");
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred while adding admin notes.");
            e.printStackTrace();
        }

        // Redirect back to feedback details
        String feedbackId = request.getParameter("feedbackId");
        response.sendRedirect(request.getContextPath() + "/FeedbackServlet?action=adminViewFeedbackDetails&feedbackId=" + feedbackId);
    }

    // Admin: Delete feedback
    private void deleteFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String feedbackIdParam = request.getParameter("feedbackId");
        if (feedbackIdParam != null) {
            try {
                int feedbackId = Integer.parseInt(feedbackIdParam);
                boolean success = FeedbackDAO.deleteFeedback(feedbackId);

                if (success) {
                    request.setAttribute("success", "Feedback deleted successfully.");
                } else {
                    request.setAttribute("error", "Failed to delete feedback.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid feedback ID.");
            }
        }

        // Redirect back to feedback list
        response.sendRedirect(request.getContextPath() + "/FeedbackServlet?action=adminAllFeedback");
    }

    // View feedback statistics
    private void viewFeedbackStats(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Get comprehensive statistics
        int totalFeedback = FeedbackDAO.getAllFeedback().size();
        int unreadCount = FeedbackDAO.getUnreadFeedbackCount();
        int bugReports = FeedbackDAO.getFeedbackCountByType("bug_report");
        int featureRequests = FeedbackDAO.getFeedbackCountByType("feature_request");
        int generalFeedback = FeedbackDAO.getFeedbackCountByType("general");
        double averageRating = FeedbackDAO.getAveragePlatformRating();

        // Calculate percentages
        double readPercentage = totalFeedback > 0 ? ((totalFeedback - unreadCount) * 100.0) / totalFeedback : 0;
        double bugPercentage = totalFeedback > 0 ? (bugReports * 100.0) / totalFeedback : 0;
        double featurePercentage = totalFeedback > 0 ? (featureRequests * 100.0) / totalFeedback : 0;
        double generalPercentage = totalFeedback > 0 ? (generalFeedback * 100.0) / totalFeedback : 0;

        request.setAttribute("totalFeedback", totalFeedback);
        request.setAttribute("unreadCount", unreadCount);
        request.setAttribute("bugReports", bugReports);
        request.setAttribute("featureRequests", featureRequests);
        request.setAttribute("generalFeedback", generalFeedback);
        request.setAttribute("averageRating", Math.round(averageRating * 100.0) / 100.0);
        request.setAttribute("readPercentage", Math.round(readPercentage * 100.0) / 100.0);
        request.setAttribute("bugPercentage", Math.round(bugPercentage * 100.0) / 100.0);
        request.setAttribute("featurePercentage", Math.round(featurePercentage * 100.0) / 100.0);
        request.setAttribute("generalPercentage", Math.round(generalPercentage * 100.0) / 100.0);

        // Forward to stats JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/feedbackStats.jsp");
        dispatcher.forward(request, response);
    }
}