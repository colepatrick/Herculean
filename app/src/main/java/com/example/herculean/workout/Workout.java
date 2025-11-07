package com.example.herculean.workout;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.Serializable;
import java.time.LocalDate;

public class Workout implements Serializable {
    private String exerciseName, bodyPart;
    private int sets;
    private int reps;
    private double weight;
    private LocalDate date;


    //  constructor
    public Workout() {
        this.exerciseName = "";
        this.bodyPart = "";
        this.sets = 0;
        this.reps = 0;
        this.weight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now(); // Automatically assigns date
        }
    }

    public Workout(String exerciseName, String bodyPart) {
        this.exerciseName = exerciseName;
        this.bodyPart  = bodyPart;
        this.sets = 0;
        this.reps = 0;
        this.weight = 0;
    }

    public Workout(String exerciseName, String bodyPart, int sets, int reps, double weight) {
        this.exerciseName = exerciseName;
        this.bodyPart = bodyPart;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now(); // Automatically assigns date
        }
    }

    // ---------- Getters ----------
    public String getBodyPart(){ return bodyPart;}
    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public double getWeight() {
        return weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getExerciseName() { return this.exerciseName; }

    public String getBodyPart() { return this.bodyPart; }

    public double getScore() { return sets*reps*weight; }

    // ---------- Setters ----------
    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // ---------- Display ----------
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format(
                "%s | %s, %s - %d sets x %d reps @ %.1f lbs",
                date, exerciseName, bodyPart, sets, reps, weight
        );
    }
}
