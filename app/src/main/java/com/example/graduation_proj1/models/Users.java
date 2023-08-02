package com.example.graduation_proj1.models;

// User.java (자바)
public class Users {
    private String email;
    private String name;
    private String contact;

    public Users() {
        // Default constructor required for Firestore
    }

    public Users(String email, String name, String contact) {
        this.email = email;
        this.name = name;
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }
}

