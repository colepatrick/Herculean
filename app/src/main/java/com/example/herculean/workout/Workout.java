package com.example.herculean.workout;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Workout implements Serializable {
    protected String exerciseName, bodyPart;
    protected LocalDate date;

    //  constructor
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

    // ---------- Getters ----------
    public String getBodyPart() { return this.bodyPart; }
    public LocalDate getDate() { return date; }
    public String getExerciseName() { return this.exerciseName; }
    public abstract double getScore();

    // ---------- Setters ----------
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }


    // ---------- Display ----------
    @SuppressLint("DefaultLocale")
    @Override
    public abstract String toString();
}