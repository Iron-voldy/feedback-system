package controller;

import dao.TrackDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Track;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CartServlet", value = "/CartServlet")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String trackId = request.getParameter("trackId");
        Double cartTotal = 0.0;
        Track track = null;

        // Get or create cart in session
        HttpSession session = request.getSession();
        List<Track> cartItems = (List<Track>) session.getAttribute("cartItems");

        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        if (trackId != null) {
            try {
                track = TrackDAO.findTrackById(Integer.parseInt(trackId));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (track != null) {
                switch (action) {
                    case "add":
                        addItemtoCart(cartItems, track);
                        cartTotal = calculateTotal(cartItems);
                        System.out.println("Item Added to Cart");
                        break;
                    case "remove":
                        removeFromCart(cartItems, track);
                        cartTotal = calculateTotal(cartItems);
                        break;
                }
            }

            // Update cart in session
            session.setAttribute("cartItems", cartItems);
            session.setAttribute("cartTotal", cartTotal);

            // Redirect back to home page
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
    }

    private void addItemtoCart(List<Track> cartItems, Track track) {
        // Check if track already exists in cart
        boolean exists = false;
        for (Track item : cartItems) {
            if (item.getTrackId() == track.getTrackId()) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            cartItems.add(track);
        }
    }

    private void removeFromCart(List<Track> cartItems, Track track) {
        // Find and remove the track by ID
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getTrackId() == track.getTrackId()) {
                cartItems.remove(i);
                break;
            }
        }
    }

    private double calculateTotal(List<Track> cartItems) {
        double total = 0;
        for (Track track: cartItems) {
            total += track.getPrice();
        }
        return total;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}