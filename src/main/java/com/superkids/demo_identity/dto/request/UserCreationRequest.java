package com.superkids.demo_identity.dto.request;


import jakarta.validation.constraints.Size;

public class UserCreationRequest {
    @Size(min = 3, max = 50, message = "Username must be at least 3 characters")
    private String username;

    @Size(min = 8, max = 50, message = "Password must be at least 8 characters")
    private String password;
    private String firstName;
    private String lastName;
    private String dob;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
