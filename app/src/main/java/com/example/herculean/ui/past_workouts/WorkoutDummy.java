package com.example.herculean.ui.past_workouts;

import java.io.Serializable;
//This is my dummy for the workout object in SPE5 just to test
//When it comes time to combine all the branches
//The fragment and layout will need to be changed to accompany
//new information that is present in SPE5 but not my dummy
public class WorkoutDummy implements Serializable {
    private final String workoutName;
    private final String date;

    public WorkoutDummy(String wName, String d) { //Constructor bro...
        this.workoutName = wName;
        this.date = d;
    }

    public String getWorkoutName() { //getter
        return workoutName;
    }
    public String getDate() { //getter
        return date;
    }

    @Override
    public String toString() { // Making it pretty
        return workoutName + " (" + date + ")";
    }
}
