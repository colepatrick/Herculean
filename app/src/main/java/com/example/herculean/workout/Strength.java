package com.example.herculean.workout;

public class Strength extends Workout {
    public Strength(String exerciseName, String bodyPart, int sets, int reps, double weight) {
        super(exerciseName, bodyPart, sets, reps, weight);
    }

    @Override
    public String toString() {
        return super.toString(); // uses strength toString from base
    }
}
