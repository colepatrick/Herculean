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

        UserAccount foundUser = findUser(username);

        if (foundUser == null) {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            Log.d("LOGIN", "User not found: " + username);
            return;
        }

        if (!foundUser.getPassword().equals(password)) {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            Log.d("LOGIN", "Wrong password for user: " + username);
            return;
        }

        Log.d("LOGIN", "Login successful for user: " + username);
        Toast.makeText(this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();

        GlobalData.currentUser = foundUser;

        if (rememberMeCheckbox.isChecked()) {
            GlobalData.setLastLoggedInUser(username);
        } else {
            GlobalData.clearLastLoggedInUser();
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
