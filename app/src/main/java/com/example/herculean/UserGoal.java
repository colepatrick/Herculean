package com.example.herculean;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;

public class UserGoal {

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

    // --- Convert to JSON ---
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("goalType", goalType);
        json.put("daysPerWeek", daysPerWeek);
        return json;
    }

    // --- Create from JSON ---
    public static UserGoal fromJSON(JSONObject json) throws JSONException {
        return new UserGoal(
                json.getString("goalType"),
                json.getInt("daysPerWeek")
        );
    }

    // --- Save to File ---
    public void saveToFile(Context context) {
        try {
            JSONObject json = toJSON();
            String jsonString = json.toString();
            FileOutputStream fos = context.openFileOutput("user_goal.json", Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // --- Load from File ---
    public static UserGoal loadFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput("user_goal.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            JSONObject json = new JSONObject(builder.toString());
            return fromJSON(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
