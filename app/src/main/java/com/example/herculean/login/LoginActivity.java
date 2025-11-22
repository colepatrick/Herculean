package com.example.herculean.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
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
    private CheckBox rememberMeCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GlobalData.loadAccounts(this);

        // Auto-login if lastLoggedInUser is set
        if (GlobalData.getLastLoggedInUser() != null) {
            UserAccount user = findUser(GlobalData.getLastLoggedInUser());
            if (user != null) {
                GlobalData.currentUser = user;
                navigateToMain();
                return;
            }
        }

        usernameInput = findViewById(R.id.login_username_input);
        passwordInput = findViewById(R.id.login_password_input);
        rememberMeCheckbox = findViewById(R.id.remember_me_checkbox);
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
        Log.d("LOGIN", "Total accounts: " + GlobalData.jsonData.accounts.size());
        for (UserAccount account : GlobalData.jsonData.accounts) {
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

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        AccountRepository repo = new AccountRepository(GlobalData.BASE_URL);

        // Try to fetch the account from the server
        repo.getAccount(username, new AccountRepository.ResultCallback<UserAccount>() {
            @Override
            public void onSuccess(UserAccount remoteUser) {
                // User was found on the server, proceed with normal password check.
                runOnUiThread(() -> {
                    if (!remoteUser.getPassword().equals(password)) {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d("LOGIN", "Login successful for user (remote): " + username);
                    loginSuccess(remoteUser, true); // Login with the remote user data
                });
            }

            @Override
            public void onError(Throwable t) {
                // The server request failed. We need to figure out why.
                if (t.getMessage() != null && t.getMessage().contains("HTTP 404")) {
                    // Server said "Not Found". Check if we have this user locally.
                    Log.d("LOGIN", "User not found on server. Checking local storage...");
                    UserAccount localUser = findUser(username);

                    if (localUser != null && localUser.getPassword().equals(password)) {
                        // Found user locally and password is correct.
                        // Let's upload them to the server.
                        Log.d("LOGIN", "Found local user '" + username + "'. Uploading to server...");
                        repo.createAccount(localUser, new AccountRepository.ResultCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                // User was successfully created on the server.
                                runOnUiThread(() -> {
                                    Log.d("LOGIN", "Local user successfully uploaded. Logging in.");
                                    loginSuccess(localUser, false); // Login with local data
                                });
                            }

                            @Override
                            public void onError(Throwable uploadError) {
                                // The upload failed (e.g., username already exists race condition)
                                runOnUiThread(() -> {
                                    Log.e("LOGIN", "Failed to upload local user: " + uploadError.getMessage());
                                    Toast.makeText(LoginActivity.this, "Sync failed. Please try again.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    } else {
                        // User not found locally either, or password was wrong.
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // This was a different network error (e.g., no connection).
                    // Fallback to offline-only login.
                    Log.d("LOGIN", "Remote fetch failed, falling back to local. Err: " + t.getMessage());
                    UserAccount localUser = findUser(username);
                    if (localUser != null && localUser.getPassword().equals(password)) {
                        runOnUiThread(() -> {
                            Log.d("LOGIN", "Login successful (offline mode): " + username);
                            loginSuccess(localUser, false); // Login with local data
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed. Check connection or credentials.", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void loginSuccess(UserAccount user, boolean isRemote) {
        // Prefer local copy over remote copy if available
        if (isRemote) {
            UserAccount localCopy = findUser(user.getUsername());
            if (localCopy != null) {
                GlobalData.jsonData.accounts.remove(localCopy);
            }
            GlobalData.jsonData.accounts.add(user);
        }

        if (rememberMeCheckbox.isChecked()) {
            GlobalData.setLastLoggedInUser(user.getUsername());
        } else {
            GlobalData.clearLastLoggedInUser();
        }
        GlobalData.currentUser = user;
        Log.d("LOGIN", "Current user set: " + GlobalData.currentUser.getUsername());

        // Update user streak before navigating
        UserStreak userStreak = user.getUserStreak();
        Logger logger = user.getWorkoutLog();
        if (userStreak != null && logger != null) {
            List<LocalDate> workoutDates = logger.getWorkouts().stream()
                    .map(Workout::getDate)
                    .collect(Collectors.toList());
            int requiredDays = user.getUserGoal().getDaysPerWeek();
            userStreak.updateStreak(workoutDates, requiredDays);
        }

        navigateToMain();
    }
    private void navigateToMain() {
        GlobalData.saveAccounts(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private UserAccount findUser(String username) {
        for (UserAccount account : GlobalData.jsonData.accounts) {
            if (account.getUsername().equalsIgnoreCase(username)) {
                return account;
            }
        }
        return null;
    }
}
