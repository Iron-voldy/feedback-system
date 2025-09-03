package controller;

import dao.ReviewDAO;
import dao.TrackDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Review;
import model.Track;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "ReviewServlet", value = "/ReviewServlet")
public class ReviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "viewTrackReviews":
                    viewTrackReviews(request, response);
                    break;
                case "viewUserReviews":
                    viewUserReviews(request, response);
                    break;
                case "showReviewForm":
                    showReviewForm(request, response);
                    break;
                case "adminPendingReviews":
                    adminPendingReviews(request, response);
                    break;
                case "approveReview":
                    approveReview(request, response);
                    break;
                case "deleteReview":
                    deleteReview(request, response);
                    break;
                default:
                    viewTrackReviews(request, response);
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
                case "submitReview":
                    submitReview(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    // View all reviews for a specific track
    private void viewTrackReviews(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        String trackIdParam = request.getParameter("trackId");
        if (trackIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        int trackId = Integer.parseInt(trackIdParam);

        // Get track details
        Track track = TrackDAO.findTrackById(trackId);
        if (track == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Get reviews for this track
        List<Review> reviews = ReviewDAO.getReviewsByTrackId(trackId);

        // Get average rating and count
        double averageRating = ReviewDAO.getAverageRatingForTrack(trackId);
        int reviewCount = ReviewDAO.getReviewCountForTrack(trackId);

        // Check if current user has already reviewed this track
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");
        boolean userHasReviewed = false;

        if (currentUser != null) {
            userHasReviewed = ReviewDAO.hasUserReviewedTrack(currentUser.getUserId(), trackId);
        }

        // Set attributes
        request.setAttribute("track", track);
        request.setAttribute("reviews", reviews);
        request.setAttribute("averageRating", averageRating);
        request.setAttribute("reviewCount", reviewCount);
        request.setAttribute("userHasReviewed", userHasReviewed);

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/reviews/trackReviews.jsp");
        dispatcher.forward(request, response);
    }

    // View all reviews by a specific user
    private void viewUserReviews(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get user's reviews
        List<Review> userReviews = ReviewDAO.getReviewsByUserId(currentUser.getUserId());

        request.setAttribute("userReviews", userReviews);

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/reviews/userReviews.jsp");
        dispatcher.forward(request, response);
    }

    // Show review form for a track
    private void showReviewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String trackIdParam = request.getParameter("trackId");
        if (trackIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        int trackId = Integer.parseInt(trackIdParam);

        // Check if user has already reviewed this track
        if (ReviewDAO.hasUserReviewedTrack(currentUser.getUserId(), trackId)) {
            request.setAttribute("error", "You have already reviewed this track.");
            viewTrackReviews(request, response);
            return;
        }

        // Get track details
        Track track = TrackDAO.findTrackById(trackId);
        if (track == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.setAttribute("track", track);

        // Forward to review form JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/reviews/reviewForm.jsp");
        dispatcher.forward(request, response);
    }

    // Submit a new review
    private void submitReview(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int trackId = Integer.parseInt(request.getParameter("trackId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String reviewText = request.getParameter("reviewText");

            // Validate rating
            if (rating < 1 || rating > 5) {
                request.setAttribute("error", "Rating must be between 1 and 5 stars.");
                showReviewForm(request, response);
                return;
            }

            // Check if user has already reviewed this track
            if (ReviewDAO.hasUserReviewedTrack(currentUser.getUserId(), trackId)) {
                request.setAttribute("error", "You have already reviewed this track.");
                viewTrackReviews(request, response);
                return;
            }

            // Create new review
            Review review = new Review(currentUser.getUserId(), trackId, rating, reviewText);
            review.setReviewDate(LocalDateTime.now());
            review.setApproved(false); // Reviews need admin approval

            // Save review
            boolean success = ReviewDAO.addReview(review);

            if (success) {
                request.setAttribute("success", "Your review has been submitted for approval. Thank you!");
            } else {
                request.setAttribute("error", "Failed to submit review. Please try again.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid input provided.");
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred while submitting your review.");
            e.printStackTrace();
        }

        // Redirect back to track reviews
        String trackId = request.getParameter("trackId");
        response.sendRedirect(request.getContextPath() + "/ReviewServlet?action=viewTrackReviews&trackId=" + trackId);
    }

    // Admin: View pending reviews for approval
    private void adminPendingReviews(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        // Check if user is admin
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Get pending reviews
        List<Review> pendingReviews = ReviewDAO.getPendingReviews();

        request.setAttribute("pendingReviews", pendingReviews);

        // Forward to admin JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/pendingReviews.jsp");
        dispatcher.forward(request, response);
    }

    // Admin: Approve a review
    private void approveReview(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        // Check if user is admin
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String reviewIdParam = request.getParameter("reviewId");
        if (reviewIdParam != null) {
            try {
                int reviewId = Integer.parseInt(reviewIdParam);
                boolean success = ReviewDAO.approveReview(reviewId);

                if (success) {
                    request.setAttribute("success", "Review approved successfully.");
                } else {
                    request.setAttribute("error", "Failed to approve review.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid review ID.");
            }
        }

        // Redirect back to pending reviews
        response.sendRedirect(request.getContextPath() + "/ReviewServlet?action=adminPendingReviews");
    }

    // Admin: Delete a review
    private void deleteReview(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("USER");

        // Check if user is admin
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String reviewIdParam = request.getParameter("reviewId");
        if (reviewIdParam != null) {
            try {
                int reviewId = Integer.parseInt(reviewIdParam);
                boolean success = ReviewDAO.deleteReview(reviewId);

                if (success) {
                    request.setAttribute("success", "Review deleted successfully.");
                } else {
                    request.setAttribute("error", "Failed to delete review.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid review ID.");
            }
        }

        // Redirect back to pending reviews
        response.sendRedirect(request.getContextPath() + "/ReviewServlet?action=adminPendingReviews");
    }
}