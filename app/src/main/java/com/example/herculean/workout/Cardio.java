package com.example.herculean.workout;

import android.annotation.SuppressLint;

import java.time.LocalDate;

public class Cardio extends Workout {
    private double duration;
    private double distance;

    public Cardio() {
        super();
        this.duration = 0;
        this.distance = 0;
    }

    public Cardio(String exerciseName, String bodyPart, double duration, double distance) {
        super(exerciseName, bodyPart);
        this.duration = duration;
        this.distance = distance;
    }

    public double getDuration() { return duration; }
    public double getDistance() { return distance; }

    @Override
    public double getScore() {
        return distance / duration;
    }

    public void setDuration(double duration) { this.duration = duration; }
    public void setDistance(double distance) { this.distance = distance; }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format(
                "%s | %s, %s - %.1f miles in %.1f minutes",
                date, exerciseName, bodyPart, distance, duration
        );
    }
}