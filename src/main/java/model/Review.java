package model;

import java.time.LocalDateTime;

public class Review {
    private int reviewId;
    private int userId;
    private int trackId;
    private int rating;
    private String reviewText;
    private LocalDateTime reviewDate;
    private boolean isApproved;

    // Additional fields for display purposes
    private String userName;
    private String trackTitle;
    private String artistName;

    // Default constructor
    public Review() {}

    // Constructor for creating new review
    public Review(int userId, int trackId, int rating, String reviewText) {
        this.userId = userId;
        this.trackId = trackId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewDate = LocalDateTime.now();
        this.isApproved = false;
    }

    // Constructor with all fields
    public Review(int reviewId, int userId, int trackId, int rating, String reviewText,
                  LocalDateTime reviewDate, boolean isApproved) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.trackId = trackId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.isApproved = isApproved;
    }

    // Getters and Setters
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", userId=" + userId +
                ", trackId=" + trackId +
                ", rating=" + rating +
                ", reviewText='" + reviewText + '\'' +
                ", reviewDate=" + reviewDate +
                ", isApproved=" + isApproved +
                '}';
    }
}