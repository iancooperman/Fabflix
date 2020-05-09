package main.java;

public class User {
    private final String fullname;
    private final String email;
    private final boolean isEmployee;

    public User(String email, String fullname, boolean isEmployee) {
        this.email = email;
        this.fullname = fullname;
        this.isEmployee = isEmployee;
    }
}
