package com.example.herculean.datahandling;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import com.example.herculean.BuildConfig;
import com.example.herculean.database.AccountService;
import com.example.herculean.database.ApiClient;
import com.google.gson.Gson;
import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;

public class GlobalData {
    public static ArrayList<UserAccount> accounts = new ArrayList<>();
    public static UserAccount currentUser = null;
    public static final String gemini_api_key = "AIzaSyAUPdeQYh8sbVyZ8KDfV3_yO5WczgD00ak";

    // Local DB testing. Change LOCAL_TEST_URL and app/src/main/res/xml/network_security_config.xml to your device's IP
    // Also change build.gradle.kts the debug branch to http://10.0.2.2:5000. Then run "docker compose -d --build"
    // You can now login/create account in emulator and it will be stored in local DB
    // You can manually check your account by going to your browser and typing http://localhost:5000/accounts/USERNAME
    // Manually delete in terminal: "curl -X DELETE http://localhost:5000/accounts/USERNAME"

    // private static final String LOCAL_TEST_URL = "http://10.0.0.17:5000/";
    // public static String BASE_URL = LOCAL_TEST_URL;

    public static String BASE_URL = BuildConfig.BASE_URL;

    public static AccountService svc = ApiClient.getClient(BASE_URL).create(AccountService.class);

    public static void saveAccounts(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
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
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
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

    public static boolean usernameExists(String username) {
        for (UserAccount account : accounts) {
            if (account.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
}