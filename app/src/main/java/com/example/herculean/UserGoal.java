package com.example.herculean;
import java.io.Serializable;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;

public class UserGoal implements Serializable {

    // Fields
    private String goalType;     // Example: "Strength", "Weight Loss", "Endurance"
    private int daysPerWeek;     // Between 1 and 7

    // Constructor
    public UserGoal(String goalType, int daysPerWeek) {
        setGoalType(goalType);
        setDaysPerWeek(daysPerWeek);
    }

    // --- Setters with Validation ---
    public void setGoalType(String goalType) {
        if (goalType == null || goalType.trim().isEmpty()) {
            this.goalType = "General Fitness"; // default
        } else {
            this.goalType = goalType.trim();
        }
    }

    public void setDaysPerWeek(int daysPerWeek) {
        if (daysPerWeek < 1) {
            this.daysPerWeek = 1;
        } else if (daysPerWeek > 7) {
            this.daysPerWeek = 7;
        } else {
            this.daysPerWeek = daysPerWeek;
        }
    }

    // --- Getters ---
    public String getGoalType() { return goalType; }
    public int getDaysPerWeek() { return daysPerWeek; }

    /**
     * Returns a human-readable string representation of the user's goal.
     */
    @Override
    public String toString() {
        return String.format("%s (%d days/week)", goalType, daysPerWeek);
    }
}
