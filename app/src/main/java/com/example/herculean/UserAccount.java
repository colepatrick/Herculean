package com.example.herculean;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String username, password, email;
    private int level;

    // No-argument constructor (required for Gson deserialization)
    public UserAccount() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.level = 1;
    }

    // Constructor with parameters
    public UserAccount(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.level = 1;
    }

    /**************************************************************************************
     * Getters and Setters
     **************************************************************************************/
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}