package com.example.herculean;

import android.util.Patterns;

import com.example.herculean.workout.Logger;
import com.example.herculean.workout.Workout;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

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

    private UserGoal userGoal;
    private UserSchedule userSchedule;
    private UserStreak userStreak;


    // No-argument constructor (required for Gson deserialization)
    public UserAccount() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.level = 1;
        this.workoutLog = new Logger();
        this.customization = defaultSettings.clone();
        this.profileImageUri = null;
        this.userGoal = new UserGoal("General Fitness", 3);
        this.userSchedule = new UserSchedule("Rest", "Rest", "Rest", "Rest", "Rest", "Rest", "Rest");
        this.userStreak = new UserStreak();

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
        this.userGoal = new UserGoal("General Fitness", 3);
        this.userSchedule = new UserSchedule("Rest", "Rest", "Rest", "Rest", "Rest", "Rest", "Rest");
        this.userStreak = new UserStreak();
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

    public Workout getBestWorkout() {
        Workout best = null;
        for(Workout workout : workoutLog.getWorkouts()) {
            if((best == null) || (workout.getScore() > best.getScore())) {
                best = workout;
            }
        }
        return best;
    }

    public String getFavoriteWorkoutType() {
        Map<String, Integer> workoutTypes = new HashMap<>();
        for(Workout workout : workoutLog.getWorkouts()) {
            String name = workout.getExerciseName();

            // Workout types are weighted by how much you did of them
            if(workoutTypes.containsKey(name)) {
                workoutTypes.put(name, workoutTypes.get(name) + (int) workout.getScore());
            } else {
                workoutTypes.put(name, (int) workout.getScore());
            }
        }

        if(workoutTypes.isEmpty()) {
            return "None";
        }
        return Collections.max(workoutTypes.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public String getFavoriteMuscleGroup() {
        Map<String, Integer> muscleGroups = new HashMap<>();
        for(Workout workout : workoutLog.getWorkouts()) {
            String name = workout.getBodyPart();

            // Muscle groups are weighted by how much you did of them
            if(muscleGroups.containsKey(name)) {
                muscleGroups.put(name, muscleGroups.get(name) + (int) workout.getScore());
            } else {
                muscleGroups.put(name, (int) workout.getScore());
            }
        }
        if(muscleGroups.isEmpty()) {
            return "None";
        }
        return Collections.max(muscleGroups.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public Logger getRecentWorkouts(LocalDate start) {
        Logger recentWorkouts = new Logger();
        for(Workout workout : workoutLog.getWorkouts()) {
            if(workout.getDate().isAfter(start)) {
                recentWorkouts.addWorkout(workout);
            }
        }
        return recentWorkouts;
    }

    public UserGoal getUserGoal() {
        return userGoal;
    }

    public void setUserGoal(UserGoal userGoal) {
        this.userGoal = userGoal;
    }

    public UserSchedule getUserSchedule() {
        return userSchedule;
    }

    public void setUserSchedule(UserSchedule userSchedule) {
        this.userSchedule = userSchedule;
    }

    public UserStreak getUserStreak() {
        return userStreak;
    }

    public void setUserStreak(UserStreak userStreak) {
        this.userStreak = userStreak;
    }
}