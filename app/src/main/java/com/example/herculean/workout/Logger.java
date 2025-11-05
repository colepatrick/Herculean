package com.example.herculean.workout;

import com.example.herculean.GlobalData;
import com.example.herculean.UserAccount;
import com.example.herculean.UserStreak;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a list of Workout objects — adding, viewing, or clearing them.
 */
public class Logger implements Serializable {

    private final ArrayList<Workout> workouts;

    public Logger() {
        workouts = new ArrayList<>();
    }

    //Add a workout to the log.
    public void addWorkout(Workout workout) {
        workouts.add(workout);
        updateUserStreak();
    }

    private void updateUserStreak() {
        UserAccount currentUser = GlobalData.currentUser;
        if (currentUser != null) {
            UserStreak userStreak = currentUser.getUserStreak();
            if (userStreak != null) {
                List<LocalDate> workoutDates = workouts.stream()
                        .map(Workout::getDate)
                        .collect(Collectors.toList());
                int requiredWorkouts = currentUser.getUserGoal().getDaysPerWeek();
                userStreak.updateStreak(workoutDates, requiredWorkouts);
            }
        }
    }

    /*
     Get all workouts in the log.
     */

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts.clear();
        this.workouts.addAll(workouts);
    }
    public List<Workout> getWorkouts() {
        return workouts;
    }

    /*
     Print workouts for testing
     */
    public void showWorkouts() {
        if (workouts.isEmpty()) {
            System.out.println("No workouts recorded yet.");
            return;
        }

        System.out.println("=== Workouts Recorded ===");
        for (Workout w : workouts) {
            System.out.println(w);
        }
    }

}
