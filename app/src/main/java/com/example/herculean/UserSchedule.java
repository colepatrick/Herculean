package com.example.herculean;
import java.io.Serializable;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;


public class UserSchedule implements Serializable {

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
    public String getMon() {
        return mon;
    }

    public String getTue() {
        return tue;
    }

    public String getWen() {
        return wen;
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

    // Setters
    public void setMon(String mon) {
        this.mon = mon;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public void setWen(String wen) {
        this.wen = wen;
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

}