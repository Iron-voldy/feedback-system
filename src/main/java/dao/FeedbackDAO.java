package dao;

import model.Feedback;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    static {
        ensureTableExists();
    }

    public static void ensureTableExists() {
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Create feedback table
            stmt.executeUpdate(
                    "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'feedback') " +
                            "CREATE TABLE feedback (" +
                            "feedbackId INT IDENTITY(1,1) PRIMARY KEY, " +
                            "userId INT NULL, " +
                            "email VARCHAR(255) NULL, " +
                            "feedbackType VARCHAR(50) NOT NULL, " +
                            "subject NVARCHAR(200) NOT NULL, " +
                            "message NVARCHAR(2000) NOT NULL, " +
                            "rating INT CHECK (rating >= 1 AND rating <= 5), " +
                            "submittedDate DATETIME DEFAULT GETDATE(), " +
                            "isRead BIT DEFAULT 0, " +
                            "adminNotes NVARCHAR(1000) NULL, " +
                            "FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE SET NULL" +
                            ")"
            );

            // Create indexes
            stmt.executeUpdate("IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_feedback_type') " +
                    "CREATE INDEX IX_feedback_type ON feedback(feedbackType)");
            stmt.executeUpdate("IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_feedback_date') " +
                    "CREATE INDEX IX_feedback_date ON feedback(submittedDate)");

        } catch (SQLException e) {
            throw new RuntimeException("Error creating feedback table", e);
        }
    }

    // Submit new feedback
    public static boolean submitFeedback(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO feedback (userId, email, feedbackType, subject, message, rating, submittedDate, isRead) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Handle anonymous feedback (userId = 0 means null in database)
            if (feedback.getUserId() == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, feedback.getUserId());
            }

            pstmt.setString(2, feedback.getEmail());
            pstmt.setString(3, feedback.getFeedbackType());
            pstmt.setString(4, feedback.getSubject());
            pstmt.setString(5, feedback.getMessage());
            pstmt.setInt(6, feedback.getRating());
            pstmt.setTimestamp(7, Timestamp.valueOf(feedback.getSubmittedDate()));
            pstmt.setBoolean(8, feedback.isRead());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        feedback.setFeedbackId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // Get all feedback (for admin)
    public static List<Feedback> getAllFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, u.firstName, u.lastName " +
                "FROM feedback f " +
                "LEFT JOIN users u ON f.userId = u.userId " +
                "ORDER BY f.submittedDate DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Feedback feedback = createFeedbackFromResultSet(rs);

                String firstName = rs.getString("firstName");
                if (firstName != null) {
                    feedback.setUserName(firstName + " " + rs.getString("lastName"));
                }

                feedbackList.add(feedback);
            }
        }
        return feedbackList;
    }

    // Get feedback by type
    public static List<Feedback> getFeedbackByType(String feedbackType) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, u.firstName, u.lastName " +
                "FROM feedback f " +
                "LEFT JOIN users u ON f.userId = u.userId " +
                "WHERE f.feedbackType = ? " +
                "ORDER BY f.submittedDate DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, feedbackType);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = createFeedbackFromResultSet(rs);

                    String firstName = rs.getString("firstName");
                    if (firstName != null) {
                        feedback.setUserName(firstName + " " + rs.getString("lastName"));
                    }

                    feedbackList.add(feedback);
                }
            }
        }
        return feedbackList;
    }

    // Get unread feedback
    public static List<Feedback> getUnreadFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, u.firstName, u.lastName " +
                "FROM feedback f " +
                "LEFT JOIN users u ON f.userId = u.userId " +
                "WHERE f.isRead = 0 " +
                "ORDER BY f.submittedDate ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Feedback feedback = createFeedbackFromResultSet(rs);

                String firstName = rs.getString("firstName");
                if (firstName != null) {
                    feedback.setUserName(firstName + " " + rs.getString("lastName"));
                }

                feedbackList.add(feedback);
            }
        }
        return feedbackList;
    }

    // Get feedback by user ID
    public static List<Feedback> getFeedbackByUserId(int userId) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, u.firstName, u.lastName " +
                "FROM feedback f " +
                "LEFT JOIN users u ON f.userId = u.userId " +
                "WHERE f.userId = ? " +
                "ORDER BY f.submittedDate DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = createFeedbackFromResultSet(rs);
                    feedback.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                    feedbackList.add(feedback);
                }
            }
        }
        return feedbackList;
    }

    // Find feedback by ID
    public static Feedback findFeedbackById(int feedbackId) throws SQLException {
        String sql = "SELECT f.*, u.firstName, u.lastName " +
                "FROM feedback f " +
                "LEFT JOIN users u ON f.userId = u.userId " +
                "WHERE f.feedbackId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, feedbackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Feedback feedback = createFeedbackFromResultSet(rs);

                    String firstName = rs.getString("firstName");
                    if (firstName != null) {
                        feedback.setUserName(firstName + " " + rs.getString("lastName"));
                    }

                    return feedback;
                }
            }
        }
        return null;
    }

    // Mark feedback as read
    public static boolean markFeedbackAsRead(int feedbackId) throws SQLException {
        String sql = "UPDATE feedback SET isRead = 1 WHERE feedbackId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, feedbackId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Add admin notes to feedback
    public static boolean addAdminNotes(int feedbackId, String adminNotes) throws SQLException {
        String sql = "UPDATE feedback SET adminNotes = ?, isRead = 1 WHERE feedbackId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, adminNotes);
            pstmt.setInt(2, feedbackId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // Delete feedback
    public static boolean deleteFeedback(int feedbackId) throws SQLException {
        String sql = "DELETE FROM feedback WHERE feedbackId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, feedbackId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Get feedback statistics
    public static int getFeedbackCountByType(String feedbackType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback WHERE feedbackType = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, feedbackType);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Get unread feedback count
    public static int getUnreadFeedbackCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback WHERE isRead = 0";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Get average platform rating
    public static double getAveragePlatformRating() throws SQLException {
        String sql = "SELECT AVG(CAST(rating AS FLOAT)) as avgRating FROM feedback WHERE rating IS NOT NULL";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("avgRating");
            }
        }
        return 0.0;
    }

    // Get feedback statistics by date range
    public static List<Feedback> getFeedbackByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, u.firstName, u.lastName " +
                "FROM feedback f " +
                "LEFT JOIN users u ON f.userId = u.userId " +
                "WHERE f.submittedDate BETWEEN ? AND ? " +
                "ORDER BY f.submittedDate DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = createFeedbackFromResultSet(rs);

                    String firstName = rs.getString("firstName");
                    if (firstName != null) {
                        feedback.setUserName(firstName + " " + rs.getString("lastName"));
                    }

                    feedbackList.add(feedback);
                }
            }
        }
        return feedbackList;
    }

    // Helper method to create Feedback object from ResultSet
    private static Feedback createFeedbackFromResultSet(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(rs.getInt("feedbackId"));

        // Handle nullable userId
        int userId = rs.getInt("userId");
        if (rs.wasNull()) {
            feedback.setUserId(0); // 0 means anonymous
        } else {
            feedback.setUserId(userId);
        }

        feedback.setEmail(rs.getString("email"));
        feedback.setFeedbackType(rs.getString("feedbackType"));
        feedback.setSubject(rs.getString("subject"));
        feedback.setMessage(rs.getString("message"));
        feedback.setRating(rs.getInt("rating"));

        Timestamp timestamp = rs.getTimestamp("submittedDate");
        if (timestamp != null) {
            feedback.setSubmittedDate(timestamp.toLocalDateTime());
        }

        feedback.setRead(rs.getBoolean("isRead"));
        feedback.setAdminNotes(rs.getString("adminNotes"));

        return feedback;
    }
}