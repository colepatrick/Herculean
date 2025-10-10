package com.example.herculean;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        // Find user in accounts
        UserAccount foundUser = findUser(username);

        if (foundUser == null) {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            Log.d("LOGIN", "User not found: " + username);
            return;
        }

        // Check password
        if (!foundUser.getPassword().equals(password)) {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            Log.d("LOGIN", "Wrong password for user: " + username);
            return;
        }

        // Login successful
        Log.d("LOGIN", "Login successful for user: " + username);
        Toast.makeText(this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();

        // Set current user in GlobalData
        GlobalData.currentUser = foundUser;

        // Save state before navigating
        GlobalData.saveAccounts(this);
        Log.d("LOGIN", "Current user set and saved: " + GlobalData.currentUser.getUsername());

        // Navigate to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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