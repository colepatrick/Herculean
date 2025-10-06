package com.example.herculean.workout;

import java.time.LocalDate;

public class Workout {
    private String exerciseName;
    private int sets;
    private int reps;
    private double weight;
    private LocalDate date;



    //  constructor
    public Workout(String exerciseName, int sets, int reps, double weight) {
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.date = LocalDate.now(); // Automatically assigns date
    }

    // ---------- Getters ----------
    public String getExerciseName() {
        return exerciseName;
    }

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
    @Override
    public String toString() {
        return String.format(
                "%s | %s - %d sets x %d reps @ %.1f lbs",
                date, exerciseName, sets, reps, weight
        );
    }
}
