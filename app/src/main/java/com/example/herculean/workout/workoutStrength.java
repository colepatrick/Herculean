package com.example.herculean.workout;

public class workoutStrength extends Workout {
    public workoutStrength(String exerciseName, String bodyPart, int sets, int reps, double weight) {
        super(exerciseName, bodyPart, sets, reps, weight);
    }

    @Override
    public String toString() {
        return super.toString(); // uses strength toString from base
    }
}
