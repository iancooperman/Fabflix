package main.java;

public class User {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String ccId;
    private final String address;
    private final String email;

    public User(String id, String firstName, String lastName, String ccId, String address, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
        this.email = email;
    }
}
