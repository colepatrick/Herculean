package com.example.herculean.datahandling;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

public class GlobalData {
    public static JsonData jsonData = new JsonData();
    public static UserAccount currentUser = null;
    public static final String gemini_api_key = "AIzaSyAUPdeQYh8sbVyZ8KDfV3_yO5WczgD00ak";

    // Centralized key for SharedPreferences
    private static final String PREFS_KEY = "herculean_data";

    public static void saveAccounts(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            String json = gson.toJson(GlobalData.jsonData);
            prefs.edit().putString(PREFS_KEY, json).apply();
            Log.d("SAVE", "✓ SAVED data to SharedPreferences");
            Log.d("SAVE", "JSON: " + json);
        } catch (Exception e) {
            Log.e("SAVE", "ERROR saving data", e);
        }
    }

    public static void loadAccounts(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            String json = prefs.getString(PREFS_KEY, null);

            Log.d("LOAD", "Retrieved JSON from SharedPreferences: " + json);

            if (json != null && !json.isEmpty()) {
                GlobalData.jsonData = gson.fromJson(json, JsonData.class);
                if (GlobalData.jsonData == null) {
                    GlobalData.jsonData = new JsonData();
                }
                if (GlobalData.jsonData.accounts == null) {
                    GlobalData.jsonData.accounts = new ArrayList<>();
                }
                Log.d("LOAD", "✓ LOADED " + GlobalData.jsonData.accounts.size() + " accounts from SharedPreferences");
            } else {
                Log.d("LOAD", "No JSON found in SharedPreferences (first run or cleared)");
                GlobalData.jsonData = new JsonData();
            }
        } catch (Exception e) {
            Log.e("LOAD", "ERROR loading accounts", e);
            GlobalData.jsonData = new JsonData();
        }
    }

    public static String getLastLoggedInUser() {
        return jsonData.lastLoggedInUser;
    }

    public static void setLastLoggedInUser(String username) {
        jsonData.lastLoggedInUser = username;
    }

    public static void clearLastLoggedInUser() {
        jsonData.lastLoggedInUser = null;
    }

    public static boolean usernameExists(String username) {
        for (UserAccount account : jsonData.accounts) {
            if (account.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
}
