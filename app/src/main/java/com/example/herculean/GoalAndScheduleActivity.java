package com.example.herculean;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;


public class GoalAndScheduleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_and_goals);

        // Link UI elements from the XML
        EditText editTextGoal = findViewById(R.id.editTextGoal);
        Spinner spinnerWeekWorkouts = findViewById(R.id.spinnerWeekWorkouts);
        EditText editTextMon = findViewById(R.id.editTextMon);
        EditText editTextTue = findViewById(R.id.editTextTue);
        EditText editTextWed = findViewById(R.id.editTextWed);
        EditText editTextThur = findViewById(R.id.editTextThur);
        EditText editTextFri = findViewById(R.id.editTextFri);
        EditText editTextSat = findViewById(R.id.editTextSat);
        EditText editTextSun = findViewById(R.id.editTextSun);
        Button saveButton = findViewById(R.id.saveButton);
        Button homeButton = findViewById(R.id.homeButton);

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String goal = editTextGoal.getText().toString();
            String workouts = spinnerWeekWorkouts.getSelectedItem().toString();

            //create the objects in order to save goal and schedule!!!!!


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


            Toast.makeText(this, "Goal and schedule saved successfully!", Toast.LENGTH_SHORT).show();

        });

        homeButton.setOnClickListener(v -> finish());
    }


}
