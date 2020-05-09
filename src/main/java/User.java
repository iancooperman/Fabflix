package main.java;

public class User {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String ccId;
    private final String address;
    private final String email;
    private final boolean isEmployee;

    public User(String id, String firstName, String lastName, String ccId, String address, String email, boolean isEmployee) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
        this.email = email;
        this.isEmployee = isEmployee;
    }
}
