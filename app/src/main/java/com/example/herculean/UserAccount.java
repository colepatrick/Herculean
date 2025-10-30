package com.example.herculean;

import android.util.Patterns;

import com.example.herculean.workout.Logger;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String username, password, email;
    private int level;
    public Logger workoutLog;

    public int[] customization; // [emailDisplayed]
    private String profileImageUri; // URI of the profile image

    public static int[] defaultSettings = {0}; // Default customization settings
    public enum CustomizationOptions {
        EMAIL_DISPLAYED
    }

    // No-argument constructor (required for Gson deserialization)
    public UserAccount() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.level = 1;
        this.workoutLog = new Logger();
        this.customization = defaultSettings.clone();
        this.profileImageUri = null;
    }

    // Constructor with parameters
    public UserAccount(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.level = 1;
        this.workoutLog = new Logger();
        this.customization = defaultSettings.clone(); // Default customization settings
        this.profileImageUri = null;
    }

    // Constructor with customization parameters
    public UserAccount(String username, String password, String email, int[] customization) {
        this(username, password, email); // Call the other constructor
        this.customization = customization;
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

    public Logger getWorkoutLog() {
        return workoutLog;
    }

    public void setWorkoutLog(Logger workoutLog) {
        this.workoutLog = workoutLog;
    }

    public boolean isEmailDisplayed() { return this.customization[CustomizationOptions.EMAIL_DISPLAYED.ordinal()] == 1; }
    public void emailDisplayed(boolean displayed) { this.customization[CustomizationOptions.EMAIL_DISPLAYED.ordinal()] = displayed ? 1 : 0; }

    public String getProfileImageUri() { return profileImageUri; }

    public void setProfileImageUri(String profileImageUri) { this.profileImageUri = profileImageUri; }

    public static boolean validPassword(String password) {
        return password.length() >= 6;
    }

    public static boolean validUsername(String username) {
        return username.length() >= 3;
    }

    public static boolean validEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}