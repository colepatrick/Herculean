package com.example.herculean.goals;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.R;

/**
 * Activity for setting user goals and weekly workout schedule.
 */
public class GoalAndScheduleActivity extends Activity {

    // UI elements
    private EditText editTextGoal;
    private EditText editTextMon;
    private EditText editTextTue;
    private EditText editTextWed;
    private EditText editTextThur;
    private EditText editTextFri;
    private EditText editTextSat;
    private EditText editTextSun;
    private Spinner spinnerWeekWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_and_goals);

        // Initialize UI elements from the layout
        editTextGoal = findViewById(R.id.editTextGoal);
        spinnerWeekWorkouts = findViewById(R.id.spinnerWeekWorkouts);
        editTextMon = findViewById(R.id.editTextMon);
        editTextTue = findViewById(R.id.editTextTue);
        editTextWed = findViewById(R.id.editTextWed);
        editTextThur = findViewById(R.id.editTextThur);
        editTextFri = findViewById(R.id.editTextFri);
        editTextSat = findViewById(R.id.editTextSat);
        editTextSun = findViewById(R.id.editTextSun);
        Button saveButton = findViewById(R.id.saveButton);
        Button homeButton = findViewById(R.id.homeButton);

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Validate that all fields are filled out
            if (TextUtils.isEmpty(editTextGoal.getText()) ||
                    TextUtils.isEmpty(editTextMon.getText()) ||
                    TextUtils.isEmpty(editTextTue.getText()) ||
                    TextUtils.isEmpty(editTextWed.getText()) ||
                    TextUtils.isEmpty(editTextThur.getText()) ||
                    TextUtils.isEmpty(editTextFri.getText()) ||
                    TextUtils.isEmpty(editTextSat.getText()) ||
                    TextUtils.isEmpty(editTextSun.getText())) {
                Toast.makeText(GoalAndScheduleActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            String goal = editTextGoal.getText().toString();
            int workouts;
            try {
                workouts = Integer.parseInt(spinnerWeekWorkouts.getSelectedItem().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(GoalAndScheduleActivity.this, "Please select a valid number of workouts", Toast.LENGTH_SHORT).show();
                return;
            }

            String mon = editTextMon.getText().toString();
            String tue = editTextTue.getText().toString();
            String wed = editTextWed.getText().toString();
            String thur = editTextThur.getText().toString();
            String fri = editTextFri.getText().toString();
            String sat = editTextSat.getText().toString();
            String sun = editTextSun.getText().toString();

            UserGoal userGoal = new UserGoal(goal, workouts);
            UserSchedule userSchedule = new UserSchedule(mon, tue, wed, thur, fri, sat, sun);

            if (GlobalData.currentUser != null) {
                GlobalData.currentUser.setUserGoal(userGoal);
                GlobalData.currentUser.setUserSchedule(userSchedule);
                GlobalData.saveAccounts(getApplicationContext());
                Toast.makeText(this, "Goal and schedule saved successfully!", Toast.LENGTH_SHORT).show();
                resetFields();
            } else {
                Toast.makeText(this, "Error: No current user found.", Toast.LENGTH_SHORT).show();
            }

        });

        // Home button click listener to return to the previous screen
        homeButton.setOnClickListener(v -> finish());
    }

    /**
     * Resets all input fields to their default state.
     */
    public void resetFields() {
        // Clear all EditTexts
        editTextGoal.setText("");
        editTextMon.setText("");
        editTextTue.setText("");
        editTextWed.setText("");
        editTextThur.setText("");
        editTextFri.setText("");
        editTextSat.setText("");
        editTextSun.setText("");

        // Reset Spinner
        spinnerWeekWorkouts.setSelection(0);

    }


}
