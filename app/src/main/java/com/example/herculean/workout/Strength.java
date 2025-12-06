package com.example.herculean.workout;

import android.annotation.SuppressLint;

public class Strength extends Workout {
    public Strength(String exerciseName, String bodyPart, int sets, int reps, double weight) {
        super(exerciseName, bodyPart);
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    @Override
    public double getScore() {
        // Example scoring: weight × reps × sets
        return (getWeight() * getReps() * getSets());
    }

}
