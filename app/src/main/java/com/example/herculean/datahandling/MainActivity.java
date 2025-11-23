package com.example.herculean.datahandling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;

import com.example.herculean.R;
import com.example.herculean.ai.ChatBot;
import com.example.herculean.login.LoginActivity;
import com.example.herculean.ui.profile.notification.NotificationManager;
import com.example.herculean.workout.Upload;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.example.herculean.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager.createNotificationChannel(this);

        Log.d("MAIN", "Total accounts loaded: " + GlobalData.jsonData.accounts.size());
        for (UserAccount account : GlobalData.jsonData.accounts) {
            Log.d("MAIN", "Account: " + account.getUsername() + " | " + account.getEmail());
        }

        // Check if user is logged in
        if (GlobalData.currentUser == null) {
            // No user logged in - show login page
            Log.d("MAIN", "No user logged in, showing LoginActivity");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("MAIN", "User logged in: " + GlobalData.currentUser.getUsername());
        Log.d("MAIN", "Total accounts in system: " + GlobalData.jsonData.accounts.size());

        // User is logged in, set up main UI
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        if (binding.appBarMain.fab != null) {
            binding.appBarMain.fab.setOnClickListener(view -> {
             Snackbar.make(view, "Opening new Workout", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).setAnchorView(R.id.fab).show();
            Intent intent = new Intent(MainActivity.this, Upload.class);
            startActivity(intent);
            });
        }

        if (binding.appBarMain.chatFab != null) {
            binding.appBarMain.chatFab.setOnClickListener(view -> {
                Snackbar.make(view, "Opening Gemini Chat", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).setAnchorView(R.id.chatFab).show();
                Intent intent = new Intent(MainActivity.this, ChatBot.class);
                startActivity(intent);
            });
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_past_workouts, R.id.nav_profile, R.id.nav_slideshow, R.id.nav_settings)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
        if (bottomNavigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_past_workouts, R.id.nav_profile, R.id.nav_slideshow)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        NavigationView navView = findViewById(R.id.nav_view);
        if (navView == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SYNC", "MainActivity is stopping. Attempting to sync data...");

        // Always save locally, as a reliable fallback
        GlobalData.saveAccounts(this);

        // Check if there is a user to sync
        if (GlobalData.currentUser == null) {
            Log.d("SYNC", "No current user to sync. Skipping server upload.");
            return;
        }

        // Use the global AccountService to upload the current user's data
        String username = GlobalData.currentUser.getUsername();
        Log.d("SYNC", "Uploading data for user: " + username);

        GlobalData.svc.updateAccount(username, GlobalData.currentUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                // This callback runs when the server responds
                if (response.isSuccessful()) {
                    Log.d("SYNC", "Successfully synced user data to server for " + username);
                } else {
                    // This happens if the server returns an error (e.g., 400, 500)
                    Log.e("SYNC", "Server returned an error during sync: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // This callback runs if the network request itself fails (e.g., no internet)
                Log.e("SYNC", "Network failure during sync: " + t.getMessage());
            }
        });
    }
}
