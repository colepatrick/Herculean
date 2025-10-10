package com.example.herculean;

import android.os.Bundle;
import android.util.Log;
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

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        Button registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

            // Validate inputs
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("REGISTER", "Before saving, accounts size: " + GlobalData.accounts.size());

            // Create a new user and save it globally
            UserAccount newUser = new UserAccount(username, password, email);
            GlobalData.accounts.add(newUser);
            GlobalData.saveAccounts(this);

            // Confirmation message
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        });
    }
}
