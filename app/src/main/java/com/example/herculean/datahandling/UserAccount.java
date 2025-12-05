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
    private double height, weight;
    private int age;
    private String gender;
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

    // ───────────────────────────────────────────────────────────────
    // FOLLOWING SYSTEM (your required fix)
    // ───────────────────────────────────────────────────────────────
    private ArrayList<String> following = new ArrayList<>();

    public void followUser(String username) {
        if (following == null) following = new ArrayList<>();
        following.remove(username);
        following.add(0, username);
    }

    public void unfollowUser(String username) {
        if (following == null) following = new ArrayList<>();
        following.remove(username);
    }


    public ArrayList<String> getFollowing() {
        if (following == null) following = new ArrayList<>();
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = (following == null) ? new ArrayList<>() : following;
    }

    // ───────────────────────────────────────────────────────────────


    // No-argument constructor (required for Gson deserialization)
    public UserAccount() {
        // DO NOT overwrite fields Gson will deserialize
        this.customExercises = new ArrayList<>();
        this.workoutLog = new Logger(); // Initialize to prevent null pointers

        // Safe defaults
        if (following == null) following = new ArrayList<>();
    }

    // Constructor with parameters
    public UserAccount(String username, String password, String email) {
        this(); // do not reset following
        this.username = username;
        this.salt = Encryption.generateSalt();
        this.password = Encryption.hashPassword(password, this.salt);
        this.email = email;
        this.level = 1;
        this.workoutLog = new Logger();
        this.customization = defaultSettings.clone();
        this.profileImageUri = null;
        this.userGoal = new UserGoal("General Fitness", 3);
        this.userSchedule = new UserSchedule("Rest", "Rest", "Rest", "Rest", "Rest", "Rest", "Rest");
        this.userStreak = new UserStreak();
    }

    // Constructor with customization parameters
    public UserAccount(String username, String password, String email, int[] customization) {
        this(username, password, email);
        this.customization = customization;
        this.userGoal = new UserGoal("General Fitness", 3);
        this.userSchedule = new UserSchedule("Rest", "Rest", "Rest", "Rest", "Rest", "Rest", "Rest");
        this.userStreak = new UserStreak();
    }

    public double calculateBmi() {
        if (height > 0 && weight > 0) {
            return weight / (height * height);
        }
        return 0;
    }

    public double calculateBmr() {
        if (gender != null && age > 0 && weight > 0 && height > 0) {
            double heightInCm = height * 100;
            if (gender.equalsIgnoreCase("male")) {
                return 88.362 + (13.397 * weight) + (4.799 * heightInCm) - (5.677 * age);
            } else if (gender.equalsIgnoreCase("female")) {
                return 447.593 + (9.247 * weight) + (3.098 * heightInCm) - (4.330 * age);
            }
        }
        return 0;
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Logger getWorkoutLog() {
        if (workoutLog == null) workoutLog = new Logger();
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
        if (getWorkoutLog().getWorkouts().isEmpty()) return null;
        Workout best = null;
        for (Workout workout : getWorkoutLog().getWorkouts()) {
            if ((best == null) || (workout.getScore() > best.getScore())) {
                best = workout;
            }
        }
        return best;
    }

    public String getFavoriteWorkoutType() {
        if (getWorkoutLog().getWorkouts().isEmpty()) return "None";
        Map<String, Integer> workoutTypes = new HashMap<>();
        for (Workout workout : getWorkoutLog().getWorkouts()) {
            String name = workout.getExerciseName();

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
        if (getWorkoutLog().getWorkouts().isEmpty()) return "None";
        Map<String, Integer> muscleGroups = new HashMap<>();
        for (Workout workout : getWorkoutLog().getWorkouts()) {
            String name = workout.getBodyPart();

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
        if (getWorkoutLog().getWorkouts().isEmpty()) return recentWorkouts;
        for (Workout workout : getWorkoutLog().getWorkouts()) {
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
        if (userStreak == null) userStreak = new UserStreak();
        return userStreak;
    }

    public void setUserStreak(UserStreak userStreak) {
        this.userStreak = userStreak;
    }

    public ArrayList<Workout> getCustomExercises() {
        if (customExercises == null) customExercises = new ArrayList<>();
        return customExercises;
    }

    public void setCustomExercises(ArrayList<Workout> exercises) {
        customExercises = exercises;
    }

    public void addCustomExercise(Workout exercise) {
        if (customExercises == null) customExercises = new ArrayList<>();
        customExercises.add(exercise);
    }

    // ──────────────────── Graph Data ────────────────────

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
