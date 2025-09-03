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

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private final int RECORDS_PER_PAGE = 8;

    @Override
    public void init() throws ServletException {
        TrackDAO.ensureTableExists();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve search parameters
        String title = request.getParameter("query");
        String genre = request.getParameter("genre");
        String priceRange = request.getParameter("price");
        String rating = request.getParameter("rating");

        // Parse price range
        Double minPrice = null;
        Double maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            switch (priceRange) {
                case "under100":
                    maxPrice = 100.0;
                    break;
                case "100-200":
                    minPrice = 100.0;
                    maxPrice = 200.0;
                    break;
                case "200-400":
                    minPrice = 200.0;
                    maxPrice = 400.0;
                    break;
                case "over400":
                    minPrice = 400.0;
                    break;
            }
        }

        // Parse rating
        Double minRating = null;
        if (rating != null && !rating.isEmpty()) {
            try {
                minRating = Double.parseDouble(rating);
            } catch (NumberFormatException e) {
                minRating = null;
            }
        }

        // Handle pagination
        int page = 1;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<Track> tracks = new ArrayList<>();
        int noOfRecords = 0;
        int noOfPages = 0;

        try {
            // Check if no search parameters are provided
            if ((title == null || title.isEmpty()) &&
                    (genre == null || genre.isEmpty()) &&
                    (priceRange == null || priceRange.isEmpty()) &&
                    (rating == null || rating.isEmpty())) {
                // Fetch all tracks with pagination
                tracks = TrackDAO.getAllTracksPaginated(page, RECORDS_PER_PAGE);
                noOfRecords = TrackDAO.countAllTracks();
            } else {
                // Use TrackDAO to search products
                tracks = TrackDAO.searchProducts(title, genre, minPrice, maxPrice, minRating, page, RECORDS_PER_PAGE);
                noOfRecords = TrackDAO.countProducts(title, genre, minPrice, maxPrice);
            }
            noOfPages = (int) Math.ceil(noOfRecords * 1.0 / RECORDS_PER_PAGE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set attributes for JSP
        request.setAttribute("trackList", tracks);
        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);

        // Forward to JSP
        RequestDispatcher rd = request.getRequestDispatcher("searchMusic.jsp");
        rd.forward(request, response);
    }
}