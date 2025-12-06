package com.example.herculean.workout;

public class Cardio extends Workout {

    private boolean supportsDistance;

    public Cardio(String exerciseName, String bodyPart, boolean supportsDistance) {
        super(exerciseName, bodyPart);
        this.supportsDistance = supportsDistance;
    }

    public Cardio(String exerciseName, String bodyPart, double duration) {
        super(exerciseName, bodyPart);
        this.duration = duration;
        this.supportsDistance = false;
    }

    public Cardio(String exerciseName, String bodyPart, double duration, double distance) {
        super(exerciseName, bodyPart);
        this.duration = duration;
        this.distance = distance;
        this.supportsDistance = true;
    }

    public boolean isDistanceBased() { return supportsDistance; }

    @Override
    public double getScore() {
        // Use distance scoring when available
        if (supportsDistance && distance > 0) {
            return distance / Math.max(duration, 1); // prevents divide by zero

        }

        // Fallback to duration-only workouts
        return duration > 0 ? duration : 0;
    }
}
