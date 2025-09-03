package model;

import java.time.LocalDateTime;
import java.util.List;

public class SupportTicket {
    private int ticketId;
    private int userId;
    private String ticketType;
    private String subject;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;
    private int assignedToAdmin;

    // Additional fields for display
    private String userName;
    private String userEmail;
    private String assignedAdminName;
    private List<SupportResponse> responses;

    // Default constructor
    public SupportTicket() {}

    // Constructor for creating new ticket
    public SupportTicket(int userId, String ticketType, String subject, String description) {
        this.userId = userId;
        this.ticketType = ticketType;
        this.subject = subject;
        this.description = description;
        this.status = "open";
        this.priority = "medium";
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    // Constructor with all fields
    public SupportTicket(int ticketId, int userId, String ticketType, String subject,
                         String description, String status, String priority,
                         LocalDateTime createdDate, LocalDateTime lastUpdated,
                         int assignedToAdmin) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.ticketType = ticketType;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.assignedToAdmin = assignedToAdmin;
    }

    // Getters and Setters
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getAssignedToAdmin() {
        return assignedToAdmin;
    }

    public void setAssignedToAdmin(int assignedToAdmin) {
        this.assignedToAdmin = assignedToAdmin;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAssignedAdminName() {
        return assignedAdminName;
    }

    public void setAssignedAdminName(String assignedAdminName) {
        this.assignedAdminName = assignedAdminName;
    }

    public List<SupportResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<SupportResponse> responses) {
        this.responses = responses;
    }

    @Override
    public String toString() {
        return "SupportTicket{" +
                "ticketId=" + ticketId +
                ", userId=" + userId +
                ", ticketType='" + ticketType + '\'' +
                ", subject='" + subject + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}