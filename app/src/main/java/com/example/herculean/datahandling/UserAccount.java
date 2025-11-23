package com.example.herculean.datahandling;

import android.util.Patterns;

import com.example.herculean.goals.UserGoal;
import com.example.herculean.goals.UserSchedule;
import com.example.herculean.goals.UserStreak;
import com.example.herculean.security.Encryption;
import com.example.herculean.workout.Logger;
import com.example.herculean.workout.Workout;
import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAccount implements Serializable {
    private String username, password, email, salt;
    private int level;
    public Logger workoutLog;

    public int[] customization; // [emailDisplayed]
    private String profileImageUri; // URI of the profile image

    // Workout notifications
    private boolean workoutNotifications;
    private String notificationTime;

    public static int[] defaultSettings = {0}; // Default customization settings

    public enum CustomizationOptions {
        EMAIL_DISPLAYED
    }

    private UserGoal userGoal;
    private UserSchedule userSchedule;
    private UserStreak userStreak;

    public ArrayList<Workout> customExercises;

    // No-argument constructor (required for Gson deserialization)
    public UserAccount() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.salt = "";
        this.level = 1;
        this.workoutLog = new Logger();
        this.customization = defaultSettings.clone();
        this.profileImageUri = null;
        this.userGoal = new UserGoal("General Fitness", 3);
        this.userSchedule = new UserSchedule("Rest", "Rest", "Rest", "Rest", "Rest", "Rest", "Rest");
        this.userStreak = new UserStreak();
        this.customExercises = new ArrayList<>();
        this.workoutNotifications = false;
        this.notificationTime = "";
    }

    // Constructor with parameters
    public UserAccount(String username, String password, String email) {
        this(); // Call default constructor, fill in what we know
        this.username = username;
        this.salt = Encryption.generateSalt();
        this.password = Encryption.hashPassword(password, this.salt);
        this.email = email;
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
        this.salt = Encryption.generateSalt();
        this.password = Encryption.hashPassword(password, this.salt);
    }

    // New checkPassword function that uses the salt
    public boolean checkPassword(String password) {
        if (password == null || this.salt == null || this.salt.isEmpty()) {
            return false;
        }
        String hash = Encryption.hashPassword(password, this.salt);
        return hash != null && hash.equals(this.password);
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

    public String getSalt() {
        return salt;
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

    public boolean isEmailDisplayed() {
        return this.customization[CustomizationOptions.EMAIL_DISPLAYED.ordinal()] == 1;
    }

    public void emailDisplayed(boolean displayed) {
        this.customization[CustomizationOptions.EMAIL_DISPLAYED.ordinal()] = displayed ? 1 : 0;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    // ──────────────────── Notifications ────────────────────

    public boolean areWorkoutNotificationsEnabled() {
        return workoutNotifications;
    }

    public void setWorkoutNotifications(boolean workoutNotifications) {
        this.workoutNotifications = workoutNotifications;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    // ──────────────────── Validation ────────────────────

    public static boolean validPassword(String password) {
        return password.length() >= 6;
    }

    public static boolean validUsername(String username) {
        return username.length() >= 3;
    }

    public static boolean validEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // ──────────────────── Stats helpers ────────────────────

    public Workout getBestWorkout() {
        Workout best = null;
        for (Workout workout : workoutLog.getWorkouts()) {
            if ((best == null) || (workout.getScore() > best.getScore())) {
                best = workout;
            }
        }
        return best;
    }

    public String getFavoriteWorkoutType() {
        Map<String, Integer> workoutTypes = new HashMap<>();
        for (Workout workout : workoutLog.getWorkouts()) {
            String name = workout.getExerciseName();

            // Workout types are weighted by how much you did of them
            if (workoutTypes.containsKey(name)) {
                workoutTypes.put(name, workoutTypes.get(name) + (int) workout.getScore());
            } else {
                workoutTypes.put(name, (int) workout.getScore());
            }
        }

        if (workoutTypes.isEmpty()) {
            return "None";
        }
        return Collections.max(workoutTypes.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public String getFavoriteMuscleGroup() {
        Map<String, Integer> muscleGroups = new HashMap<>();
        for (Workout workout : workoutLog.getWorkouts()) {
            String name = workout.getBodyPart();

            // Muscle groups are weighted by how much you did of them
            if (muscleGroups.containsKey(name)) {
                muscleGroups.put(name, muscleGroups.get(name) + (int) workout.getScore());
            } else {
                muscleGroups.put(name, (int) workout.getScore());
            }
        }
        if (muscleGroups.isEmpty()) {
            return "None";
        }
        return Collections.max(muscleGroups.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public Logger getRecentWorkouts(LocalDate start) {
        Logger recentWorkouts = new Logger();
        for (Workout workout : workoutLog.getWorkouts()) {
            if (workout.getDate().isAfter(start)) {
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

    public ArrayList<Workout> getCustomExercises() {
        return customExercises;
    }

    public void setCustomExercises(ArrayList<Workout> exercises) {
        customExercises = exercises;
    }

    public void addCustomExercise(Workout exercise) {
        customExercises.add(exercise);
    }

    // ──────────────────── Graph Data (from master) ────────────────────

    public DataPoint[] getDayDataPoints(int days) {
        List<Workout> recents = getRecentWorkouts(LocalDate.now().minusDays(days)).getWorkouts();
        DataPoint[] points = new DataPoint[days];
        int[] scores = new int[days];

        for (int i = 0; i < days; i++) {
            scores[i] = 0;
            for (Workout workout : recents) {
                if (LocalDate.now().minusDays(days - i - 1).isEqual(workout.getDate())) {
                    scores[i] += workout.getScore();
                }
            }
            points[i] = new DataPoint(i, scores[i]);
        }
        return points;
    }

    public DataPoint[] getMonthDataPoints(int months) {
        List<Workout> recents = getRecentWorkouts(LocalDate.now().minusMonths(months)).getWorkouts();
        DataPoint[] points = new DataPoint[months];
        int[] scores = new int[months];

        for (int i = 0; i < months; i++) {
            scores[i] = 0;
            for (Workout workout : recents) {
                if (LocalDate.now().minusMonths(months - i - 1).getMonth() ==
                        workout.getDate().getMonth()) {
                    scores[i] += workout.getScore();
                }
            }
            points[i] = new DataPoint(i, scores[i]);
        }
        return points;
    }
}
