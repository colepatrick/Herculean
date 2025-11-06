package com.example.herculean;

import com.example.herculean.workout.Logger;
import com.example.herculean.workout.Workout;

import java.io.Serializable;
import java.time.LocalDate;

public class UserAccount implements Serializable {
    private String username, password, email;
    private int level;
    public Logger workoutLog;

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