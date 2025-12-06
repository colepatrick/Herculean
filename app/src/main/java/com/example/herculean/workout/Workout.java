package com.example.herculean.workout;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.Serializable;
import java.time.LocalDate;

public class Workout implements Serializable {

    protected String exerciseName, bodyPart;
    protected LocalDate date;

    public Workout() {
        this.exerciseName = "";
        this.bodyPart = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now();
        }
    }

    public Workout(String exerciseName, String bodyPart) {
        this.exerciseName = exerciseName;
        this.bodyPart  = bodyPart;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now();
        }
    }

    // Getters
    public String getBodyPart() { return this.bodyPart; }
    public LocalDate getDate() { return date; }
    public String getExerciseName() { return this.exerciseName; }

    // Dummy score so Gson works (you can override later)
    public double getScore() { return 0; }

    // Setters
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%s (%s) on %s", exerciseName, bodyPart, date);
    }
}
