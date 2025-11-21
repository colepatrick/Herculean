package com.example.herculean.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.herculean.database.AccountRepository;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.MainActivity;
import com.example.herculean.R;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.goals.UserStreak;
import com.example.herculean.workout.Logger;
import com.example.herculean.workout.Workout;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GlobalData.loadAccounts(this);

        usernameInput = findViewById(R.id.login_username_input);
        passwordInput = findViewById(R.id.login_password_input);
        Button loginButton = findViewById(R.id.login_button);
        TextView registerLink = findViewById(R.id.register_link);

        loginButton.setOnClickListener(v -> handleLogin());

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterAccount.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log all accounts whenever this screen is shown
        Log.d("LOGIN", "===== REGISTERED ACCOUNTS =====");
        Log.d("LOGIN", "Total accounts: " + GlobalData.accounts.size());
        for (UserAccount account : GlobalData.accounts) {
            Log.d("LOGIN", "Username: " + account.getUsername() +
                    " | Email: " + account.getEmail() +
                    " | Level: " + account.getLevel());
        }
        Log.d("LOGIN", "===== END ACCOUNTS =====");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STATE", "Saving data");
        GlobalData.saveAccounts(this);
    }

    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }
        // Attempt to fetch the single account from the server first
        AccountRepository repo = new AccountRepository(GlobalData.BASE_URL);
        repo.getAccount(username, new AccountRepository.ResultCallback<UserAccount>() {
            @Override
            public void onSuccess(UserAccount foundUser) {
                runOnUiThread(() -> {
                    if (foundUser == null) {
                        Toast.makeText(LoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                        Log.d("LOGIN", "User not found (remote): " + username);
                        return;
                    }

        // Check password
        if (!foundUser.getPassword().equals(password)) {
            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            Log.d("LOGIN", "Wrong password for user: " + username);
            return;
        }

                    // Login successful
                    Log.d("LOGIN", "Login successful for user (remote): " + username);
                    Toast.makeText(LoginActivity.this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();

                    // Merge or set the account in local cache
                    UserAccount local = findUser(username);
                    if (local == null) {
                        GlobalData.accounts.add(foundUser);
                    } else {
                        GlobalData.accounts.remove(local);
                        GlobalData.accounts.add(foundUser);
                    }

        // Set current user in GlobalData
        GlobalData.currentUser = foundUser;

                    // Update user streak
                    UserStreak userStreak = foundUser.getUserStreak();
                    Logger logger = foundUser.getWorkoutLog();
                    if (userStreak != null && logger != null) {
                        List<LocalDate> workoutDates = logger.getWorkouts().stream()
                                .map(Workout::getDate)
                                .collect(Collectors.toList());
                        int requiredDays = foundUser.getUserGoal().getDaysPerWeek();
                        userStreak.updateStreak(workoutDates, requiredDays);
                    }

                    // Save state before navigating
                    GlobalData.saveAccounts(LoginActivity.this);
                    Log.d("LOGIN", "Current user set and saved: " + GlobalData.currentUser.getUsername());

                    // Navigate to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(Throwable t) {
                // On network error, fallback to local look up
                runOnUiThread(() -> {
                    Log.d("LOGIN", "Remote fetch failed, falling back to local. Err: " + t.getMessage());
                    UserAccount foundUser = findUser(username);
                    if (foundUser == null) {
                        Toast.makeText(LoginActivity.this, "Username not found (network and local)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!foundUser.getPassword().equals(password)) {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Local login success (same flow)
                    GlobalData.currentUser = foundUser;
                    UserStreak userStreak = foundUser.getUserStreak();
                    Logger logger = foundUser.getWorkoutLog();
                    if (userStreak != null && logger != null) {
                        List<LocalDate> workoutDates = logger.getWorkouts().stream()
                                .map(Workout::getDate)
                                .collect(Collectors.toList());
                        int requiredDays = foundUser.getUserGoal().getDaysPerWeek();
                        userStreak.updateStreak(workoutDates, requiredDays);
                    }
                    GlobalData.saveAccounts(LoginActivity.this);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }

    private UserAccount findUser(String username) {
        for (UserAccount account : GlobalData.accounts) {
            if (account.getUsername().equalsIgnoreCase(username)) {
                return account;
            }
        }
        return null;
    }
}
