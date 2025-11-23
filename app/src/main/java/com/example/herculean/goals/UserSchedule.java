package com.example.herculean.goals;
import java.io.Serializable;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSchedule implements Serializable {

    // Fields
    private String mon, tue, wed, thur, fri, sat, sun;

    // Constructor
    public UserSchedule(String mon, String tue, String wed, String thur, String fri, String sat, String sun) {
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thur = thur;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("mon", mon);
        json.put("tue", tue);
        json.put("wed", wed);
        json.put("thur", thur);
        json.put("fri", fri);
        json.put("sat", sat);
        json.put("sun", sun);
        return json;
    }

    public static UserSchedule fromJSON(JSONObject json) throws JSONException {
        return new UserSchedule(
                json.getString("mon"),
                json.getString("tue"),
                json.getString("wed"),
                json.getString("thur"),
                json.getString("fri"),
                json.getString("sat"),
                json.getString("sun")
        );
    }

    // Change all days at once
    public void changeSchedule(String mon, String tue, String wed, String thur, String fri, String sat, String sun) {
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thur = thur;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    // Getters
    public String getMon() {
        return mon;
    }

    public String getTue() {
        return tue;
    }

    public String getWed() {
        return wed;
    }

    public String getThur() {
        return thur;
    }

    public String getFri() {
        return fri;
    }

    public String getSat() {
        return sat;
    }

    public String getSun() {
        return sun;
    }

    public String getWorkoutForDay(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return mon;
            case Calendar.TUESDAY:
                return tue;
            case Calendar.WEDNESDAY:
                return wed;
            case Calendar.THURSDAY:
                return thur;
            case Calendar.FRIDAY:
                return fri;
            case Calendar.SATURDAY:
                return sat;
            case Calendar.SUNDAY:
                return sun;
            default:
                return "Rest";
        }
    }

    // Setters
    public void setMon(String mon) {
        this.mon = mon;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public void setThur(String thur) {
        this.thur = thur;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    /**
     * Returns a human-readable string representation of the weekly schedule.
     */
    @Override
    public String toString() {
        return String.format(
            "Mon: %s, Tue: %s, Wed: %s, Thu: %s, Fri: %s, Sat: %s, Sun: %s",
            mon, tue, wed, thur, fri, sat, sun
        );
    }
}
