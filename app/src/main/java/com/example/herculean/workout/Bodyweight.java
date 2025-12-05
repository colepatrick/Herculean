package com.example.herculean.workout;

import android.annotation.SuppressLint;

public class Bodyweight extends Workout {
    private int sets;
    private int reps;

    public Bodyweight(String exerciseName, String bodyPart, int sets, int reps) {
        super(exerciseName, bodyPart);
        this.sets = sets;
        this.reps = reps;
    }

    public int getSets() { return sets; }
    public int getReps() { return reps; }

    @Override
    public double getScore() {
        return sets * reps;
    }

    public void setSets(int sets) { this.sets = sets; }
    public void setReps(int reps) { this.reps = reps; }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format(
                "%s | %s, %s - %d sets x %d reps",
                date, exerciseName, bodyPart, sets, reps
        );
    }
}