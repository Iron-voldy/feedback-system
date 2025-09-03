package dao;

import model.Track;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TrackDAO {

    static {
        ensureTableExists();
    }

    public static void ensureTableExists() {
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            // Create tracks table
            stmt.executeUpdate(
                    "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'tracks') " +
                            "CREATE TABLE tracks (" +
                            "trackId INT IDENTITY(1,1) PRIMARY KEY, " +
                            "title VARCHAR(50), " +
                            "artist VARCHAR(50), " +
                            "genre VARCHAR(20), " +
                            "rating FLOAT, " +
                            "price DECIMAL(10,2)" +
                            ")"
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tracks table", e);
        }
    }

    public static LinkedList<Track> getAllTracks() throws SQLException {
        LinkedList<Track> tracks = new LinkedList<>();
        String sql = "SELECT * FROM tracks";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Track track = new Track(
                        rs.getString("title"),
                        rs.getString("artist")
                );
                track.setTrackId(rs.getInt("trackId"));
                track.setPrice(rs.getDouble("price"));
                track.setGenre(rs.getString("genre"));
                track.setRating(rs.getDouble("rating"));
                tracks.add(track);
            }
        }
        return tracks;
    }

    public static List<Track> getAllTracksPaginated(int page, int pageSize) throws SQLException {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks ORDER BY trackId OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, (page - 1) * pageSize);
            pstmt.setInt(2, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Track track = new Track(
                            rs.getString("title"),
                            rs.getString("artist")
                    );
                    track.setTrackId(rs.getInt("trackId"));
                    track.setPrice(rs.getDouble("price"));
                    track.setGenre(rs.getString("genre"));
                    track.setRating(rs.getDouble("rating"));
                    tracks.add(track);
                }
            }
        }
        return tracks;
    }

    public static int countAllTracks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tracks";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public static void addTrack(Track track) throws SQLException {
        if (track == null) return;

        String sql = "INSERT INTO tracks (title, artist, price, genre, rating) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, track.getTitle());
            pstmt.setString(2, track.getArtist());
            pstmt.setDouble(3, track.getPrice());
            pstmt.setString(4, track.getGenre());
            pstmt.setDouble(5, track.getRating());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    track.setTrackId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public static void removeTrack(int trackId) throws SQLException {
        String sql = "DELETE FROM tracks WHERE trackId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, trackId);
            pstmt.executeUpdate();
        }
    }

    public static Track findTrackById(int trackId) throws SQLException {
        String sql = "SELECT * FROM tracks WHERE trackId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, trackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Track track = new Track(
                            rs.getString("title"),
                            rs.getString("artist")
                    );
                    track.setTrackId(rs.getInt("trackId"));
                    track.setPrice(rs.getDouble("price"));
                    track.setGenre(rs.getString("genre"));
                    track.setRating(rs.getDouble("rating"));
                    return track;
                }
            }
        }
        return null;
    }

    public static void updateTrack(Track track) throws SQLException {
        if (track == null) return;

        String sql = "UPDATE tracks SET title = ?, artist = ?, price = ?, genre = ?, rating = ? WHERE trackId = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, track.getTitle());
            pstmt.setString(2, track.getArtist());
            pstmt.setDouble(3, track.getPrice());
            pstmt.setString(4, track.getGenre());
            pstmt.setDouble(5, track.getRating());
            pstmt.setInt(6, track.getTrackId());
            pstmt.executeUpdate();
        }
    }

    public static List<Track> searchProducts(String title,
                                             String genre,
                                             Double minPrice,
                                             Double maxPrice,
                                             Double rating,
                                             int page,
                                             int pageSize) {

        List<Track> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tracks WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title + "%");
        }

        if (genre != null && !genre.isEmpty()) {
            sql.append(" AND genre = ?");
            params.add(genre);
        }

        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }

        if (rating != null) {
            sql.append(" AND rating >= ?");
            params.add(rating);
        }

        sql.append(" ORDER BY trackId OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Track(
                        rs.getInt("trackId"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getDouble("price"),
                        rs.getString("genre"),
                        rs.getDouble("rating")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int countProducts(String title,
                                    String genre,
                                    Double minPrice,
                                    Double maxPrice) {

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tracks WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title + "%");
        }
        if (genre != null && !genre.isEmpty()) {
            sql.append(" AND genre = ?");
            params.add(genre);
        }

        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}