package dao;

import model.Admin;
import model.User;
import utils.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UserDAO {

    static {
        ensureTableExists();
    }

    public static LinkedList<User> getUsers() throws SQLException {
        LinkedList<User> allUsers = new LinkedList<>();
        String sql = "SELECT * From users";

        try (Connection con = DatabaseConnection.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allUsers.add(createUserFromResultSet(rs));
            }
        }

        return allUsers;
    }

    public static void addUser(User user) throws IOException, SQLException {
        if (user != null) {
            try (Connection con = DatabaseConnection.getConnection()) {
                System.out.println("Connected to Database!");

                String sql = "INSERT INTO users (role, firstName, lastName, email, password) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    pstmt.setString(1, user.isAdmin() ? "adminUser" : "standardUser");
                    pstmt.setString(2, user.getFirstName());
                    pstmt.setString(3, user.getLastName());
                    pstmt.setString(4, user.getEmail());
                    pstmt.setString(5, user.getPassword());

                    int result = pstmt.executeUpdate();

                System.out.println("Number of changes made " + result);

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }

                // insert Liked Genres to table
                if (user.getLikedGenres() != null) {
                    String genreSql = "INSERT INTO UserGenres (userId, genre) VALUES (?, ?)";
                    try (PreparedStatement ps2 = con.prepareStatement(genreSql)) {
                        for (String genre : user.getLikedGenres()) {
                            ps2.setInt(1, user.getUserId());
                            ps2.setString(2, genre);
                            ps2.addBatch();
                        }
                        ps2.executeBatch();
                    }
                }
            }
        }
    }

    public static void removeUser(User user) throws IOException, SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String sql = "DELETE FROM users WHERE userId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, user.getUserId());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }

            System.out.println("User deleted successfully. Rows affected: " + affectedRows);
        }
    }

    public static User findUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE userId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public static User findUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public static void updateUser(int userId, String firstName, String lastName, String email, String phoneNumber, String password) throws IOException, SQLException {
        String sql = "UPDATE users SET firstName = ?, lastName = ?, email = ?, phoneNumber = ?, password = ? WHERE userId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, password);
            pstmt.setInt(6, userId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            System.out.println("User updated successfully. Rows affected: " + affectedRows);
        }
    }

    private static User createUserFromResultSet(ResultSet rs) throws SQLException {
        boolean isAdmin = "adminUser".equals(rs.getString("role"));

        if (isAdmin) {
            Admin admin = new Admin(
                    rs.getInt("userId"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("password")
            );

            admin.setLikedGenres(getUserLikedGenres(rs.getInt("userId")));
            return admin;

        } else {
            User user = new User(
                    rs.getInt("userId"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("password")
            );

            user.setLikedGenres(getUserLikedGenres(rs.getInt("userId")));
            return user;

        }
    }



    private static List<String> getUserLikedGenres(int userId) throws SQLException {
        List<String> genres = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String genreSql = "SELECT genre FROM UserGenres WHERE userId=?";

            try (PreparedStatement ps = conn.prepareStatement(genreSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    genres.add(rs.getString("genre"));
                }
            }
        }
        return genres;
    }

    private static void ensureTableExists() {
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            // Create users table
            stmt.executeUpdate(
                    "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users') " +
                            "CREATE TABLE users (" +
                            "userId INT IDENTITY(1,1) PRIMARY KEY, " +
                            "role VARCHAR(20), " +
                            "firstName VARCHAR(50), " +
                            "lastName VARCHAR(50), " +
                            "email VARCHAR(100) UNIQUE, " +
                            "password VARCHAR(255)" +
                            ")"
            );

            // Create UserGenres table
            stmt.executeUpdate("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'UserGenres') " +
                    "CREATE TABLE UserGenres (" +
                    "id INT PRIMARY KEY IDENTITY, " +
                    "userId INT, " +
                    "genre VARCHAR(50), " +
                    "FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE" +
                    ")"
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
 