package model;

public class Admin extends User {
    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }

    public Admin(int userId, String firstName, String lastName, String email, String password) {
        super(userId, firstName, lastName, email, password);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public String getRoleDescription() {
        return "Administrator";
    }

}
