package com.example.herculean.login;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.R;
import com.example.herculean.datahandling.UserAccount;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterAccount extends AppCompatActivity {

    private EditText usernameInput, passwordInput, emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        Button registerButton = findViewById(R.id.register_button);
        Button backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> {
            finish();
        });

        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

            // Validate inputs
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate username length
            if (username.length() < 3) {
                Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show();
                usernameInput.requestFocus();
                return;
            }

            // Validate email format
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                emailInput.requestFocus();
                return;
            }

            // Validate password strength
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                passwordInput.requestFocus();
                return;
            }

            // Check if email already exists
            if (emailExists(email)) {
                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                emailInput.requestFocus();
                return;
            }

            usernameExists(username, exists -> {
                // This entire block of code is the "callback" you pass in.
                // It will execute AFTER the network call finishes.

                if (exists) {
                    // The server found the username. Show an error on the UI thread.
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
                        usernameInput.requestFocus();
                    });
                } else {
                    // The username is available. Now we can create the account.
                    Log.d("REGISTER", "Username available. Creating account...");
                    UserAccount newUser = new UserAccount(username, password, email);

                    // Use the global service to create the account on the server
                    GlobalData.svc.createAccount(newUser).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Server creation was successful. Update local data and finish.
                                runOnUiThread(() -> {
                                    GlobalData.accounts.add(newUser);
                                    GlobalData.saveAccounts(RegisterAccount.this);
                                    Toast.makeText(RegisterAccount.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            } else {
                                // The server returned an error during creation (e.g., 500)
                                runOnUiThread(() -> Toast.makeText(RegisterAccount.this, "Server error on creation: " + response.code(), Toast.LENGTH_SHORT).show());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // The network failed during the creation attempt
                            runOnUiThread(() -> Toast.makeText(RegisterAccount.this, "Network failure: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            });
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    interface UsernameCheckCallback {
        void onResult(boolean exists);
    }
    private void usernameExists(String username, UsernameCheckCallback callback) {
        // Use the global service instance
        GlobalData.svc.getAccount(username).enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                if (response.isSuccessful()) {
                    // 200 OK means the user was found.
                    callback.onResult(true);
                } else if (response.code() == 404) {
                    // 404 Not Found means the username is available.
                    callback.onResult(false);
                } else {
                    // Another error occurred (like 500 server error)
                    runOnUiThread(() -> Toast.makeText(RegisterAccount.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<UserAccount> call, Throwable t) {
                // A network failure occurred (no internet, etc.)
                Log.e("REGISTER", "Network error checking username: " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(RegisterAccount.this, "Network error. Check connection.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private boolean emailExists(String email) {
        for (UserAccount account : GlobalData.accounts) {
            if (account.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
}