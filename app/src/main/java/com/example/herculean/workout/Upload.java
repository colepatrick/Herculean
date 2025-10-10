package com.example.herculean.workout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

// Import your model classes from the workout folder
import com.example.herculean.MainActivity;
import com.example.herculean.R;


public class Upload extends AppCompatActivity {
    private EditText editTextWeight, editTextSets, editTextReps;
    private TextView workoutView;
    private Button buttonUploadWorkout, homeButton;
    private Spinner spinner;
    private Logger workoutLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_workout);

        editTextWeight = findViewById(R.id.editTextWeight);
        editTextSets = findViewById(R.id.editTextSets);
        editTextReps = findViewById(R.id.editTextReps);
        buttonUploadWorkout = findViewById(R.id.buttonUploadWorkout);
        spinner = findViewById(R.id.spinner);
        workoutView = findViewById(R.id.workoutView);
        homeButton = findViewById(R.id.homeButton);

        workoutLog = new Logger();

        buttonUploadWorkout.setClickable(true);
        buttonUploadWorkout.setEnabled(true);

        buttonUploadWorkout.setOnClickListener(v -> {
            uploadWorkout();
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Upload.this, MainActivity.class);
            startActivity(intent);
        });

    }

    private void uploadWorkout() {
        String weight = editTextWeight.getText().toString();
        String set = editTextSets.getText().toString();
        String rep = editTextReps.getText().toString();
        String exercise = spinner.getSelectedItem().toString();

        if (weight.isEmpty() || set.isEmpty() || rep.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double weightValue;
        int setValue;
        int repValue;

        try {
            weightValue = Double.parseDouble(weight);
            setValue = Integer.parseInt(set);
            repValue = Integer.parseInt(rep);
        }
        catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter NUMBERS ONLY!!!", Toast.LENGTH_SHORT).show();
            return;
        }


        Workout newWorkout;
        switch (exercise) {
            case "Bicep Curls": {
                newWorkout = new Workout("Bicep Curls", "Biceps", setValue, repValue, weightValue);
                break;
            }
            case "Tricep Pulldown": {
                newWorkout = new Workout("Tricep Pulldown", "Triceps", setValue, repValue, weightValue);
                break;
            }
            case "Squat": {
                newWorkout = new Workout("Squat", "Legs", setValue, repValue, weightValue);
                break;
            }
            case "Deadlift": {
                newWorkout = new Workout("Deadlift", "Back", setValue, repValue, weightValue);
                break;
            }
            default: {
                Toast.makeText(this, "Unknown exercise: " + exercise, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        workoutLog.addWorkout(newWorkout);
        renderWorkouts();

        Toast.makeText(this, "Workout uploaded successfully", Toast.LENGTH_SHORT).show();
        editTextWeight.setText("");
        editTextSets.setText("");
        editTextReps.setText("");
        spinner.setSelection(0);
    }

    private void renderWorkouts() {
        if (workoutLog.getWorkouts().isEmpty()) {
            workoutView.setText("Add exercises!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Workout w : workoutLog.getWorkouts()) {
            sb.append(w).append("\n");
        }
        workoutView.setText(sb.toString().trim());
    }
}