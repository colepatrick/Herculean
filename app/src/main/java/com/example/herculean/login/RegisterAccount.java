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

            // Use the method from GlobalData
            Log.d("REGISTER", "Checking username on server...");
            GlobalData.usernameExists(username, usernameExists -> {
                if (usernameExists) {
                    // Username is taken. Stop here.
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
                        usernameInput.requestFocus();
                    });
                } else {
                    // Username is available
                    Log.d("REGISTER", "Username available. Checking email on server...");

                    // NOW check email
                    GlobalData.emailExists(email, emailExists -> {
                        if (emailExists) {
                            // Email is taken. Stop here.
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                                emailInput.requestFocus();
                            });
                        } else {
                            // 3. Both username and email are available. Create the account.
                            Log.d("REGISTER", "Email available. Creating account...");
                            UserAccount newUser = new UserAccount(username, password, email);
                            GlobalData.svc.createAccount(newUser).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        runOnUiThread(() -> {
                                            GlobalData.accounts.add(newUser);
                                            GlobalData.saveAccounts(RegisterAccount.this);
                                            Toast.makeText(RegisterAccount.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        });
                                    } else {
                                        runOnUiThread(() -> Toast.makeText(RegisterAccount.this, "Server error on creation: " + response.code(), Toast.LENGTH_SHORT).show());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    runOnUiThread(() -> Toast.makeText(RegisterAccount.this, "Network failure: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            });
                        }
                    });
                }
            });
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}