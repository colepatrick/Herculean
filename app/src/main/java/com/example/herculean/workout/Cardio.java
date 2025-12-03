package com.example.herculean.workout;

import android.annotation.SuppressLint;

public class Cardio extends Workout {
    private double duration;
    private double distance;
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

    public double getDuration() { return duration; }
    public double getDistance() { return distance; }
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

    public void setDuration(double duration) { this.duration = duration; }
    public void setDistance(double distance) { this.distance = distance; }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        if (supportsDistance && distance > 0) {
            return String.format(
                    "%s | %s, %s - %.1f miles in %.1f minutes",
                    date, exerciseName, bodyPart, distance, duration
            );
        } else {
            return String.format(
                    "%s | %s, %s - %.1f minutes",
                    date, exerciseName, bodyPart, duration
            );
        }
    }
}
