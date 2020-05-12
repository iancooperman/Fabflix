package main.java;

public class User {
    private final String fullname;
    private final String email;
    private final boolean employee;

    public User(String email, String fullname, boolean employee) {
        this.email = email;
        this.fullname = fullname;
        this.employee = employee;
    }

    public boolean isEmployee() {
        return employee;
    }
}
