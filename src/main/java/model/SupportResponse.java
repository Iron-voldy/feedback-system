package model;

import java.time.LocalDateTime;

public class SupportResponse {
    private int responseId;
    private int ticketId;
    private int responderId;
    private String responseText;
    private LocalDateTime responseDate;
    private boolean isAdminResponse;

    // Additional fields for display
    private String responderName;

    // Default constructor
    public SupportResponse() {}

    // Constructor for creating new response
    public SupportResponse(int ticketId, int responderId, String responseText, boolean isAdminResponse) {
        this.ticketId = ticketId;
        this.responderId = responderId;
        this.responseText = responseText;
        this.isAdminResponse = isAdminResponse;
        this.responseDate = LocalDateTime.now();
    }

    // Constructor with all fields
    public SupportResponse(int responseId, int ticketId, int responderId, String responseText,
                           LocalDateTime responseDate, boolean isAdminResponse) {
        this.responseId = responseId;
        this.ticketId = ticketId;
        this.responderId = responderId;
        this.responseText = responseText;
        this.responseDate = responseDate;
        this.isAdminResponse = isAdminResponse;
    }

    // Getters and Setters
    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getResponderId() {
        return responderId;
    }

    public void setResponderId(int responderId) {
        this.responderId = responderId;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public boolean isAdminResponse() {
        return isAdminResponse;
    }

    public void setAdminResponse(boolean adminResponse) {
        isAdminResponse = adminResponse;
    }

    public String getResponderName() {
        return responderName;
    }

    public void setResponderName(String responderName) {
        this.responderName = responderName;
    }

    @Override
    public String toString() {
        return "SupportResponse{" +
                "responseId=" + responseId +
                ", ticketId=" + ticketId +
                ", responderId=" + responderId +
                ", responseText='" + responseText + '\'' +
                ", responseDate=" + responseDate +
                ", isAdminResponse=" + isAdminResponse +
                '}';
    }
}