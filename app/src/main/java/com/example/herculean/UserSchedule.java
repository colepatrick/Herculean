package com.example.herculean;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;


public class UserSchedule {

    // Fields
    private String mon, tue, wen, thur, fri, sat, sun;

    // Constructor
    public UserSchedule(String mon, String tue, String wen, String thur, String fri, String sat, String sun) {
        this.mon = mon;
        this.tue = tue;
        this.wen = wen;
        this.thur = thur;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    // Change all days at once
    public void changeSchedule(String mon, String tue, String wen, String thur, String fri, String sat, String sun) {
        this.mon = mon;
        this.tue = tue;
        this.wen = wen;
        this.thur = thur;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    // Getters
    public String getMon() { return mon; }
    public String getTue() { return tue; }
    public String getWen() { return wen; }
    public String getThur() { return thur; }
    public String getFri() { return fri; }
    public String getSat() { return sat; }
    public String getSun() { return sun; }

    // Setters
    public void setMon(String mon) { this.mon = mon; }
    public void setTue(String tue) { this.tue = tue; }
    public void setWen(String wen) { this.wen = wen; }
    public void setThur(String thur) { this.thur = thur; }
    public void setFri(String fri) { this.fri = fri; }
    public void setSat(String sat) { this.sat = sat; }
    public void setSun(String sun) { this.sun = sun; }

    // --- Convert to JSON ---
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("mon", mon);
        json.put("tue", tue);
        json.put("wen", wen);
        json.put("thur", thur);
        json.put("fri", fri);
        json.put("sat", sat);
        json.put("sun", sun);
        return json;
    }

    // --- Create from JSON ---
    public static UserSchedule fromJSON(JSONObject json) throws JSONException {
        return new UserSchedule(
                json.getString("mon"),
                json.getString("tue"),
                json.getString("wen"),
                json.getString("thur"),
                json.getString("fri"),
                json.getString("sat"),
                json.getString("sun")
        );
    }

    // --- Save to File ---
    public void saveToFile(Context context) {
        try {
            JSONObject json = toJSON();
            String jsonString = json.toString();
            FileOutputStream fos = context.openFileOutput("user_schedule.json", Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // --- Load from File ---
    public static UserSchedule loadFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput("user_schedule.json");
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
