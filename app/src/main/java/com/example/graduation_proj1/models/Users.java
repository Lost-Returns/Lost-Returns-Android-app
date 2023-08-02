package com.example.graduation_proj1.models;

// User.java (자바)
public class Users {
    private String name;
    private String contact;

    public Users() {
        // Default constructor required for Firestore
    }

    public Users(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }
}

