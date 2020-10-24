package com.example.mybus.models;

public class User {

    String email;
    String id;
    String profile;

    public User(String email, String id, String profile) {
        this.email = email;
        this.id = id;
        this.profile = profile;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
