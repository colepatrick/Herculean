package com.example.herculean;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class GlobalData {
    public static ArrayList<UserAccount> accounts = new ArrayList<>();

    public static void saveAccounts(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(GlobalData.accounts);
        prefs.edit().putString("accounts", json).apply();
        Log.d("STATE", "SAVING ACCOUNTS - Count: " + GlobalData.accounts.size());
        Log.d("STATE", "JSON saved: " + json);
    }

    public static void loadAccounts(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        // BUG FIX: Changed from "workouts" to "accounts"
        String json = prefs.getString("accounts", null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<UserAccount>>(){}.getType();
            ArrayList<UserAccount> list = gson.fromJson(json, type);
            if (list != null) {
                GlobalData.accounts = list;
            }
            Log.d("STATE", "LOADING ACCOUNTS - Count: " + GlobalData.accounts.size());
            Log.d("STATE", "JSON loaded: " + json);
        } else {
            Log.d("STATE", "No accounts found in SharedPreferences");
        }
    }
}