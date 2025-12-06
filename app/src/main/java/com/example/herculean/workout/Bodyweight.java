package com.example.herculean.workout;

import android.annotation.SuppressLint;

public class Bodyweight extends Workout {
    public Bodyweight(String exerciseName, String bodyPart, int sets, int reps) {
        super(exerciseName, bodyPart);
        this.sets = sets;
        this.reps = reps;
    }
    @Override
    public double getScore() {
        return sets * reps;
    }

}