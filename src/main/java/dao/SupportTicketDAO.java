package dao;

import model.SupportTicket;
import model.SupportResponse;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SupportTicketDAO {

    static {
        ensureTablesExist();
    }

    public static void ensureTablesExist() {
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Create supportTickets table
            stmt.executeUpdate(
                    "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'supportTickets') " +
                            "CREATE TABLE supportTickets (" +
                            "ticketId INT IDENTITY(1,1) PRIMARY KEY, " +
                            "userId INT NOT NULL, " +
                            "ticketType VARCHAR(50) NOT NULL, " +
                            "subject NVARCHAR(200) NOT NULL, " +
                            "description NVARCHAR(2000) NOT NULL, " +
                            "status VARCHAR(20) DEFAULT 'open', " +
                            "priority VARCHAR(10) DEFAULT 'medium', " +
                            "createdDate DATETIME DEFAULT GETDATE(), " +
                            "lastUpdated DATETIME DEFAULT GETDATE(), " +
                            "assignedToAdmin INT NULL, " +
                            "FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE" +
                            ")"
            );

            // Create supportResponses table
            stmt.executeUpdate(
                    "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'supportResponses') " +
                            "CREATE TABLE supportResponses (" +
                            "responseId INT IDENTITY(1,1) PRIMARY KEY, " +
                            "ticketId INT NOT NULL, " +
                            "responderId INT NOT NULL, " +
                            "responseText NVARCHAR(2000) NOT NULL, " +
                            "responseDate DATETIME DEFAULT GETDATE(), " +
                            "isAdminResponse BIT DEFAULT 1, " +
                            "FOREIGN KEY (ticketId) REFERENCES supportTickets(ticketId) ON DELETE CASCADE, " +
                            "FOREIGN KEY (responderId) REFERENCES users(userId)" +
                            ")"
            );

            // Create indexes
            stmt.executeUpdate("IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_supportTickets_status') " +
                    "CREATE INDEX IX_supportTickets_status ON supportTickets(status)");
            stmt.executeUpdate("IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_supportTickets_userId') " +
                    "CREATE INDEX IX_supportTickets_userId ON supportTickets(userId)");

        } catch (SQLException e) {
            throw new RuntimeException("Error creating support ticket tables", e);
        }
    }

    // Create a new support ticket
    public static boolean createTicket(SupportTicket ticket) throws SQLException {
        String sql = "INSERT INTO supportTickets (userId, ticketType, subject, description, status, priority, createdDate, lastUpdated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, ticket.getUserId());
            pstmt.setString(2, ticket.getTicketType());
            pstmt.setString(3, ticket.getSubject());
            pstmt.setString(4, ticket.getDescription());
            pstmt.setString(5, ticket.getStatus());
            pstmt.setString(6, ticket.getPriority());
            pstmt.setTimestamp(7, Timestamp.valueOf(ticket.getCreatedDate()));
            pstmt.setTimestamp(8, Timestamp.valueOf(ticket.getLastUpdated()));

            int result = pstmt.executeUpdate();

            if (result > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ticket.setTicketId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // Get all tickets for a specific user
    public static List<SupportTicket> getTicketsByUserId(int userId) throws SQLException {
        List<SupportTicket> tickets = new ArrayList<>();
        String sql = "SELECT st.*, u.firstName, u.lastName, u.email, " +
                "a.firstName as adminFirstName, a.lastName as adminLastName " +
                "FROM supportTickets st " +
                "JOIN users u ON st.userId = u.userId " +
                "LEFT JOIN users a ON st.assignedToAdmin = a.userId " +
                "WHERE st.userId = ? " +
                "ORDER BY st.lastUpdated DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SupportTicket ticket = createTicketFromResultSet(rs);
                    ticket.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                    ticket.setUserEmail(rs.getString("email"));

                    String adminFirstName = rs.getString("adminFirstName");
                    if (adminFirstName != null) {
                        ticket.setAssignedAdminName(adminFirstName + " " + rs.getString("adminLastName"));
                    }

                    tickets.add(ticket);
                }
            }
        }
        return tickets;
    }

    // Get all tickets (for admin view)
    public static List<SupportTicket> getAllTickets() throws SQLException {
        List<SupportTicket> tickets = new ArrayList<>();
        String sql = "SELECT st.*, u.firstName, u.lastName, u.email, " +
                "a.firstName as adminFirstName, a.lastName as adminLastName " +
                "FROM supportTickets st " +
                "JOIN users u ON st.userId = u.userId " +
                "LEFT JOIN users a ON st.assignedToAdmin = a.userId " +
                "ORDER BY " +
                "CASE st.priority " +
                "WHEN 'urgent' THEN 1 " +
                "WHEN 'high' THEN 2 " +
                "WHEN 'medium' THEN 3 " +
                "WHEN 'low' THEN 4 " +
                "END, st.createdDate ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                SupportTicket ticket = createTicketFromResultSet(rs);
                ticket.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                ticket.setUserEmail(rs.getString("email"));

                String adminFirstName = rs.getString("adminFirstName");
                if (adminFirstName != null) {
                    ticket.setAssignedAdminName(adminFirstName + " " + rs.getString("adminLastName"));
                }

                tickets.add(ticket);
            }
        }
        return tickets;
    }

    // Get tickets by status
    public static List<SupportTicket> getTicketsByStatus(String status) throws SQLException {
        List<SupportTicket> tickets = new ArrayList<>();
        String sql = "SELECT st.*, u.firstName, u.lastName, u.email, " +
                "a.firstName as adminFirstName, a.lastName as adminLastName " +
                "FROM supportTickets st " +
                "JOIN users u ON st.userId = u.userId " +
                "LEFT JOIN users a ON st.assignedToAdmin = a.userId " +
                "WHERE st.status = ? " +
                "ORDER BY st.lastUpdated DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SupportTicket ticket = createTicketFromResultSet(rs);
                    ticket.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                    ticket.setUserEmail(rs.getString("email"));

                    String adminFirstName = rs.getString("adminFirstName");
                    if (adminFirstName != null) {
                        ticket.setAssignedAdminName(adminFirstName + " " + rs.getString("adminLastName"));
                    }

                    tickets.add(ticket);
                }
            }
        }
        return tickets;
    }

    // Find ticket by ID
    public static SupportTicket findTicketById(int ticketId) throws SQLException {
        String sql = "SELECT st.*, u.firstName, u.lastName, u.email, " +
                "a.firstName as adminFirstName, a.lastName as adminLastName " +
                "FROM supportTickets st " +
                "JOIN users u ON st.userId = u.userId " +
                "LEFT JOIN users a ON st.assignedToAdmin = a.userId " +
                "WHERE st.ticketId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, ticketId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    SupportTicket ticket = createTicketFromResultSet(rs);
                    ticket.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                    ticket.setUserEmail(rs.getString("email"));

                    String adminFirstName = rs.getString("adminFirstName");
                    if (adminFirstName != null) {
                        ticket.setAssignedAdminName(adminFirstName + " " + rs.getString("adminLastName"));
                    }

                    // Load responses for this ticket
                    ticket.setResponses(getResponsesByTicketId(ticketId));

                    return ticket;
                }
            }
        }
        return null;
    }

    // Update ticket status
    public static boolean updateTicketStatus(int ticketId, String status) throws SQLException {
        String sql = "UPDATE supportTickets SET status = ?, lastUpdated = GETDATE() WHERE ticketId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, ticketId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // Assign ticket to admin
    public static boolean assignTicketToAdmin(int ticketId, int adminId) throws SQLException {
        String sql = "UPDATE supportTickets SET assignedToAdmin = ?, status = 'in_progress', lastUpdated = GETDATE() WHERE ticketId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, adminId);
            pstmt.setInt(2, ticketId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // Update ticket priority
    public static boolean updateTicketPriority(int ticketId, String priority) throws SQLException {
        String sql = "UPDATE supportTickets SET priority = ?, lastUpdated = GETDATE() WHERE ticketId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, priority);
            pstmt.setInt(2, ticketId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // Get responses for a ticket
    public static List<SupportResponse> getResponsesByTicketId(int ticketId) throws SQLException {
        List<SupportResponse> responses = new ArrayList<>();
        String sql = "SELECT sr.*, u.firstName, u.lastName " +
                "FROM supportResponses sr " +
                "JOIN users u ON sr.responderId = u.userId " +
                "WHERE sr.ticketId = ? " +
                "ORDER BY sr.responseDate ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, ticketId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SupportResponse response = new SupportResponse();
                    response.setResponseId(rs.getInt("responseId"));
                    response.setTicketId(rs.getInt("ticketId"));
                    response.setResponderId(rs.getInt("responderId"));
                    response.setResponseText(rs.getString("responseText"));

                    Timestamp timestamp = rs.getTimestamp("responseDate");
                    if (timestamp != null) {
                        response.setResponseDate(timestamp.toLocalDateTime());
                    }

                    response.setAdminResponse(rs.getBoolean("isAdminResponse"));
                    response.setResponderName(rs.getString("firstName") + " " + rs.getString("lastName"));

                    responses.add(response);
                }
            }
        }
        return responses;
    }

    // Add response to ticket
    public static boolean addResponseToTicket(SupportResponse response) throws SQLException {
        String sql = "INSERT INTO supportResponses (ticketId, responderId, responseText, responseDate, isAdminResponse) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, response.getTicketId());
            pstmt.setInt(2, response.getResponderId());
            pstmt.setString(3, response.getResponseText());
            pstmt.setTimestamp(4, Timestamp.valueOf(response.getResponseDate()));
            pstmt.setBoolean(5, response.isAdminResponse());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                // Update ticket's last updated time
                updateTicketLastUpdated(response.getTicketId());

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        response.setResponseId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // Helper method to update ticket's last updated time
    private static void updateTicketLastUpdated(int ticketId) throws SQLException {
        String sql = "UPDATE supportTickets SET lastUpdated = GETDATE() WHERE ticketId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, ticketId);
            pstmt.executeUpdate();
        }
    }

    // Get ticket statistics
    public static int getTicketCountByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM supportTickets WHERE status = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Helper method to create SupportTicket from ResultSet
    private static SupportTicket createTicketFromResultSet(ResultSet rs) throws SQLException {
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketId(rs.getInt("ticketId"));
        ticket.setUserId(rs.getInt("userId"));
        ticket.setTicketType(rs.getString("ticketType"));
        ticket.setSubject(rs.getString("subject"));
        ticket.setDescription(rs.getString("description"));
        ticket.setStatus(rs.getString("status"));
        ticket.setPriority(rs.getString("priority"));

        Timestamp createdTimestamp = rs.getTimestamp("createdDate");
        if (createdTimestamp != null) {
            ticket.setCreatedDate(createdTimestamp.toLocalDateTime());
        }

        Timestamp updatedTimestamp = rs.getTimestamp("lastUpdated");
        if (updatedTimestamp != null) {
            ticket.setLastUpdated(updatedTimestamp.toLocalDateTime());
        }

        ticket.setAssignedToAdmin(rs.getInt("assignedToAdmin"));
        return ticket;
    }
}