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
    public static UserAccount currentUser = null;

    public static void saveAccounts(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = gson.toJson(GlobalData.accounts);
            prefs.edit().putString("accounts", json).apply();
            Log.d("SAVE", "✓ SAVED " + GlobalData.accounts.size() + " accounts to SharedPreferences");
            Log.d("SAVE", "JSON: " + json);
        } catch (Exception e) {
            Log.e("SAVE", "ERROR saving accounts", e);
        }
    }

    public static void loadAccounts(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("accounts", null);

            Log.d("LOAD", "Retrieved JSON from SharedPreferences: " + json);

            if (json != null && !json.isEmpty()) {
                Type type = new TypeToken<ArrayList<UserAccount>>(){}.getType();
                ArrayList<UserAccount> list = gson.fromJson(json, type);
                if (list != null && !list.isEmpty()) {
                    GlobalData.accounts = list;
                    Log.d("LOAD", "✓ LOADED " + GlobalData.accounts.size() + " accounts from SharedPreferences");
                    for (UserAccount account : GlobalData.accounts) {
                        Log.d("LOAD", "  - " + account.getUsername() + " | " + account.getEmail());
                    }
                } else {
                    Log.d("LOAD", "JSON parsed but list is empty or null");
                    GlobalData.accounts = new ArrayList<>();
                }
            } else {
                Log.d("LOAD", "No JSON found in SharedPreferences (first run or cleared)");
                GlobalData.accounts = new ArrayList<>();
            }
        } catch (Exception e) {
            Log.e("LOAD", "ERROR loading accounts", e);
            GlobalData.accounts = new ArrayList<>();
        }
    }
}