package com.example.herculean;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterAccount extends AppCompatActivity {

    private EditText usernameInput, passwordInput, emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        GlobalData.loadAccounts(this);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        Button registerButton = findViewById(R.id.register_button);
        Button backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> { finish(); });

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

            // Check if username already exists
            if (usernameExists(username)) {
                Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
                usernameInput.requestFocus();
                return;
            }

            // Check if email already exists
            if (emailExists(email)) {
                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                emailInput.requestFocus();
                return;
            }

            Log.d("REGISTER", "Before saving, accounts size: " + GlobalData.accounts.size());

            // Create a new user and save it globally
            UserAccount newUser = new UserAccount(username, password, email);
            GlobalData.accounts.add(newUser);
            GlobalData.saveAccounts(this);

            Log.d("REGISTER", "Account saved. Total accounts: " + GlobalData.accounts.size());

            // Confirmation message
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

            // Close the registration activity
            finish();
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean usernameExists(String username) {
        for (UserAccount account : GlobalData.accounts) {
            if (account.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
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