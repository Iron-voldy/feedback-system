package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    protected int userId;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    protected List<String> likedGenres = new ArrayList<>();

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User(int userId, String firstName, String lastName, String email, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public void addLikedGenre(String genre) {
        if (likedGenres.isEmpty()){
            likedGenres.add(genre);
            return;
        }

        if (!(likedGenres.contains(genre)))
            likedGenres.add(genre);
    }

    public List<String> getLikedGenres() {
        return likedGenres;
    }

    public void setLikedGenres(List<String> likedGenres) {
        this.likedGenres = likedGenres;
    }

    public String likedGenreToString() {
        return String.join("|", likedGenres);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return false;
    }

    public String getRoleDescription() {
        return "Standard User";
    }

}