package model;

import java.time.LocalDateTime;

public class Feedback {
    private int feedbackId;
    private int userId; // Can be 0 for anonymous feedback
    private String email; // For anonymous users
    private String feedbackType;
    private String subject;
    private String message;
    private int rating; // Overall platform rating
    private LocalDateTime submittedDate;
    private boolean isRead;
    private String adminNotes;

    // Additional fields for display
    private String userName;

    // Default constructor
    public Feedback() {}

    // Constructor for anonymous feedback
    public Feedback(String email, String feedbackType, String subject, String message, int rating) {
        this.userId = 0;
        this.email = email;
        this.feedbackType = feedbackType;
        this.subject = subject;
        this.message = message;
        this.rating = rating;
        this.submittedDate = LocalDateTime.now();
        this.isRead = false;
    }

    // Constructor for logged-in user feedback
    public Feedback(int userId, String feedbackType, String subject, String message, int rating) {
        this.userId = userId;
        this.feedbackType = feedbackType;
        this.subject = subject;
        this.message = message;
        this.rating = rating;
        this.submittedDate = LocalDateTime.now();
        this.isRead = false;
    }

    // Constructor with all fields
    public Feedback(int feedbackId, int userId, String email, String feedbackType, String subject,
                    String message, int rating, LocalDateTime submittedDate, boolean isRead, String adminNotes) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.email = email;
        this.feedbackType = feedbackType;
        this.subject = subject;
        this.message = message;
        this.rating = rating;
        this.submittedDate = submittedDate;
        this.isRead = isRead;
        this.adminNotes = adminNotes;
    }

    // Getters and Setters
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAnonymous() {
        return userId == 0;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", feedbackType='" + feedbackType + '\'' +
                ", subject='" + subject + '\'' +
                ", rating=" + rating +
                ", submittedDate=" + submittedDate +
                ", isRead=" + isRead +
                '}';
    }
}