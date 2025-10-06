package com.example.herculean.workout;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of Workout objects — adding, viewing, or clearing them.
 */
public class WorkoutLog {

    private final ArrayList<Workout> workouts;

    public WorkoutLog() {
        workouts = new ArrayList<>();
    }

//Add a workout to the log.
    public void addWorkout(Workout workout) {
        workouts.add(workout);
    }

    /*
     Get all workouts in the log.
     */
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
