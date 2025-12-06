package com.example.herculean.workout;

import android.annotation.SuppressLint;

public class Cardio extends Workout {
    private boolean supportsDistance;

    // For database initialization
    public Cardio(String exerciseName, String bodyPart, boolean supportsDistance) {
        super(exerciseName, bodyPart);
        this.supportsDistance = supportsDistance;
    }

    // For logging a duration-only workout
    public Cardio(String exerciseName, String bodyPart, double duration) {
        super(exerciseName, bodyPart);
        this.duration = duration;
        this.supportsDistance = false;
    }

    // For logging a workout with distance
    public Cardio(String exerciseName, String bodyPart, double duration, double distance) {
        super(exerciseName, bodyPart);
        this.duration = duration;
        this.distance = distance;
        this.supportsDistance = true;
    }
    public boolean isDistanceBased() { return supportsDistance; }

    @Override
    public double getScore() {
        // Prioritize distance-based scoring, fall back to duration
        if (supportsDistance && distance > 0 && duration > 0) {
            return (distance / duration) * 100; // Adjusted score
        } else {
            return duration;
        }
    }
}
