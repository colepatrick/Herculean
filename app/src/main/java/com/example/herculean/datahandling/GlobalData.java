package com.example.herculean.datahandling;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import com.example.herculean.BuildConfig;
import com.example.herculean.database.AccountService;
import com.example.herculean.database.ApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GlobalData {
    public static JsonData jsonData = new JsonData();
    public static UserAccount currentUser = null;
    public static final String gemini_api_key = "AIzaSyDTE4RK9lr1bm6fmwRUFcNdpKHvETD3GEg";

    // Local DB testing. Change LOCAL_TEST_URL and app/src/main/res/xml/network_security_config.xml to your device's IP
    // Also change build.gradle.kts the debug branch to http://10.0.2.2:5000. Then run "docker compose -d --build"
    // You can now login/create account in emulator and it will be stored in local DB
    // You can manually check your account by going to your browser and typing http://localhost:5000/accounts/USERNAME
    // Manually delete in terminal: "curl -X DELETE http://localhost:5000/accounts/USERNAME"

    // private static final String LOCAL_TEST_URL = "http://10.0.0.17:5000/";
    // public static String BASE_URL = LOCAL_TEST_URL;

    public static String BASE_URL = BuildConfig.BASE_URL;

    public static AccountService svc = ApiClient.getClient(BASE_URL).create(AccountService.class);

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

    public interface ServerCallback<T> {
        void onResult(T result);
    }

    public static void usernameExists(String username, ServerCallback<Boolean> callback) {
        // Use the global service instance 'svc' to make the network call
        svc.getAccount(username).enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                if (response.isSuccessful()) {
                    // HTTP 200 OK means the user was found.
                    callback.onResult(true);
                } else if (response.code() == 404) {
                    // HTTP 404 Not Found means the username is available.
                    callback.onResult(false);
                } else {
                    // Another server error occurred (e.g., 500). Treat as non-existent for safety.
                    Log.e("GLOBAL_DATA", "Server error checking username: " + response.code());
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<UserAccount> call, Throwable t) {
                // A network failure occurred (e.g., no internet).
                Log.e("GLOBAL_DATA", "Network failure checking username: " + t.getMessage());
                // Treat as non-existent, as we can't confirm existence.
                callback.onResult(false);
            }
        });
    }

    public static void emailExists(String email, ServerCallback<Boolean> callback) {
        svc.getAccountByEmail(email).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // HTTP 200 OK means the email was found.
                    callback.onResult(true);
                } else if (response.code() == 404) {
                    // HTTP 404 Not Found means the email is available.
                    callback.onResult(false);
                } else {
                    // Another server error occurred.
                    Log.e("GLOBAL_DATA", "Server error checking email: " + response.code());
                    callback.onResult(false); // Treat as non-existent for safety
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // A network failure occurred.
                Log.e("GLOBAL_DATA", "Network failure checking email: " + t.getMessage());
                callback.onResult(false); // Treat as non-existent for safety
            }
        });
    }
}
