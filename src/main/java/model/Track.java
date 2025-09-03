package model;

public class Track {
    private int trackId;
    private String title;
    private String artist;
    private double price;
    private String genre;
    private double rating;

    public Track(
            int trackId,
            String title,
            String artist,
            double price,
            String genre,
            double rating) {

        this.trackId = trackId;
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.genre = genre;
        this.rating = rating;
    }

    public Track(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getArtist() {
        return artist;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
