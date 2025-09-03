package controller;

import dao.TrackDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Track;
import utils.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/trackPaginate")
public class TrackPaginationServlet extends HttpServlet {
    private final int RECORDS_PER_PAGE = 8;

    @Override
    public void init() throws ServletException {
        TrackDAO.ensureTableExists();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int page = 1;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List<Track> tracks = new ArrayList<>();
        int noOfRecords = 0;
        int noOfPages = 0;

        try {

            Connection conn = DatabaseConnection.getConnection();

            // Fetch limited songs
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM tracks ORDER BY trackId OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            ps.setInt(1, (page - 1) * RECORDS_PER_PAGE);
            ps.setInt(2, RECORDS_PER_PAGE);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Track track = new Track(
                        rs.getString("title"),
                        rs.getString("artist")
                );
                track.setTrackId(rs.getInt("trackId"));
                track.setPrice(rs.getDouble("price"));
                tracks.add(track);
            }
            rs.close();

            // Get total rows
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM tracks");
            if (rs.next()) {
                noOfRecords = rs.getInt(1);
            }
            noOfPages = (int) Math.ceil(noOfRecords * 1.0 / RECORDS_PER_PAGE);

            rs.close();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("trackList", tracks);
        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);

        RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
        rd.forward(request, response);
    }

}
