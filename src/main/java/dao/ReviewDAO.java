package dao;

import model.Review;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReviewDAO {

    static {
        ensureTableExists();
    }

    public static void ensureTableExists() {
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Create reviews table
            stmt.executeUpdate(
                    "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'reviews') " +
                            "CREATE TABLE reviews (" +
                            "reviewId INT IDENTITY(1,1) PRIMARY KEY, " +
                            "userId INT NOT NULL, " +
                            "trackId INT NOT NULL, " +
                            "rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5), " +
                            "reviewText NVARCHAR(1000), " +
                            "reviewDate DATETIME DEFAULT GETDATE(), " +
                            "isApproved BIT DEFAULT 0, " +
                            "FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY (trackId) REFERENCES tracks(trackId) ON DELETE CASCADE, " +
                            "UNIQUE(userId, trackId)" +
                            ")"
            );

            // Create indexes
            stmt.executeUpdate("IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_reviews_trackId') " +
                    "CREATE INDEX IX_reviews_trackId ON reviews(trackId)");
            stmt.executeUpdate("IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_reviews_userId') " +
                    "CREATE INDEX IX_reviews_userId ON reviews(userId)");

        } catch (SQLException e) {
            throw new RuntimeException("Error creating reviews table", e);
        }
    }

    // Add a new review
    public static boolean addReview(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (userId, trackId, rating, reviewText, reviewDate, isApproved) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, review.getUserId());
            pstmt.setInt(2, review.getTrackId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getReviewText());
            pstmt.setTimestamp(5, Timestamp.valueOf(review.getReviewDate()));
            pstmt.setBoolean(6, review.isApproved());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        review.setReviewId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // Get all reviews for a specific track
    public static List<Review> getReviewsByTrackId(int trackId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.firstName, u.lastName, t.title, t.artist " +
                "FROM reviews r " +
                "JOIN users u ON r.userId = u.userId " +
                "JOIN tracks t ON r.trackId = t.trackId " +
                "WHERE r.trackId = ? AND r.isApproved = 1 " +
                "ORDER BY r.reviewDate DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, trackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = createReviewFromResultSet(rs);
                    review.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                    review.setTrackTitle(rs.getString("title"));
                    review.setArtistName(rs.getString("artist"));
                    reviews.add(review);
                }
            }
        }
        return reviews;
    }

    // Get all reviews by a specific user
    public static List<Review> getReviewsByUserId(int userId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, t.title, t.artist " +
                "FROM reviews r " +
                "JOIN tracks t ON r.trackId = t.trackId " +
                "WHERE r.userId = ? " +
                "ORDER BY r.reviewDate DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = createReviewFromResultSet(rs);
                    review.setTrackTitle(rs.getString("title"));
                    review.setArtistName(rs.getString("artist"));
                    reviews.add(review);
                }
            }
        }
        return reviews;
    }

    // Get all pending reviews for admin approval
    public static List<Review> getPendingReviews() throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.firstName, u.lastName, t.title, t.artist " +
                "FROM reviews r " +
                "JOIN users u ON r.userId = u.userId " +
                "JOIN tracks t ON r.trackId = t.trackId " +
                "WHERE r.isApproved = 0 " +
                "ORDER BY r.reviewDate ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Review review = createReviewFromResultSet(rs);
                review.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                review.setTrackTitle(rs.getString("title"));
                review.setArtistName(rs.getString("artist"));
                reviews.add(review);
            }
        }
        return reviews;
    }

    // Approve a review
    public static boolean approveReview(int reviewId) throws SQLException {
        String sql = "UPDATE reviews SET isApproved = 1 WHERE reviewId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, reviewId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Delete a review
    public static boolean deleteReview(int reviewId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE reviewId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, reviewId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Get average rating for a track
    public static double getAverageRatingForTrack(int trackId) throws SQLException {
        String sql = "SELECT AVG(CAST(rating AS FLOAT)) as avgRating FROM reviews WHERE trackId = ? AND isApproved = 1";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, trackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avgRating");
                }
            }
        }
        return 0.0;
    }

    // Check if user has already reviewed a track
    public static boolean hasUserReviewedTrack(int userId, int trackId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reviews WHERE userId = ? AND trackId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, trackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Get review count for a track
    public static int getReviewCountForTrack(int trackId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reviews WHERE trackId = ? AND isApproved = 1";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, trackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Find review by ID
    public static Review findReviewById(int reviewId) throws SQLException {
        String sql = "SELECT r.*, u.firstName, u.lastName, t.title, t.artist " +
                "FROM reviews r " +
                "JOIN users u ON r.userId = u.userId " +
                "JOIN tracks t ON r.trackId = t.trackId " +
                "WHERE r.reviewId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, reviewId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Review review = createReviewFromResultSet(rs);
                    review.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                    review.setTrackTitle(rs.getString("title"));
                    review.setArtistName(rs.getString("artist"));
                    return review;
                }
            }
        }
        return null;
    }

    // Helper method to create Review object from ResultSet
    private static Review createReviewFromResultSet(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("reviewId"));
        review.setUserId(rs.getInt("userId"));
        review.setTrackId(rs.getInt("trackId"));
        review.setRating(rs.getInt("rating"));
        review.setReviewText(rs.getString("reviewText"));

        Timestamp timestamp = rs.getTimestamp("reviewDate");
        if (timestamp != null) {
            review.setReviewDate(timestamp.toLocalDateTime());
        }

        review.setApproved(rs.getBoolean("isApproved"));
        return review;
    }
}