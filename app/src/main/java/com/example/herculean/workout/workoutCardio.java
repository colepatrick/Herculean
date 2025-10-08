package com.example.herculean.workout;

public class workoutCardio extends Workout {
    private double duration;
    private double distance;

    public workoutCardio(String exerciseName, String bodyPart, int duration, double distance) {
        super(exerciseName, bodyPart, 0, 0, 0.0);
        this.duration = duration;
        this.distance = distance;
    }

    public double getDuration() { return duration; }
    public void setDuration(int durationMinutes) { this.duration = durationMinutes; }
    public double getDistance() { return distance; }
    public void setDistance(double distanceKm) { this.distance = distanceKm; }

    @Override
    public String toString() {
        return String.format("%s | %s (%s) - %d min, %.2f km",
                getDate(), duration, distance
        );
    }
}
