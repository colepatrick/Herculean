package com.example.herculean;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import java.util.ArrayList;

// Import your model classes from the workout folder
import com.example.herculean.workout.Workout;
import com.example.herculean.workout.WorkoutLog;

public class UploadWorkoutActivity extends AppCompatActivity {

    // UI components
    private EditText editTextExercise, editTextSets, editTextReps, editTextWeight;
    private Button buttonUploadWorkout, backButton;
    private ListView listWorkouts;

    // Data
    private WorkoutLog workoutLog;
    private ArrayList<String> workoutDisplayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_workout);

        // Link XML elements to Java
        editTextExercise = findViewById(R.id.editTextExercise);
        editTextSets = findViewById(R.id.editTextSets);
        editTextReps = findViewById(R.id.editTextReps);
        editTextWeight = findViewById(R.id.editTextWeight);
        buttonUploadWorkout = findViewById(R.id.buttonUploadWorkout);
        backButton = findViewById(R.id.backButton);

        // Initialize data structures
        workoutLog = new WorkoutLog();
        workoutDisplayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workoutDisplayList);

        // Create a ListView dynamically (since not in XML)
        listWorkouts = new ListView(this);
        ((LinearLayout) findViewById(android.R.id.content).getRootView()
                .findViewById(R.id.backButton).getParent()). // get the LinearLayout from your ScrollView
                addView(listWorkouts);

        listWorkouts.setAdapter(adapter);

        // When Upload button is pressed
        buttonUploadWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        // Back button just closes the activity
        backButton.setOnClickListener(v -> finish());
    }

    private void saveWorkout() {
        String exerciseName = editTextExercise.getText().toString().trim();
        String setsStr = editTextSets.getText().toString().trim();
        String repsStr = editTextReps.getText().toString().trim();
        String weightStr = editTextWeight.getText().toString().trim();

        // Validate input
        if (exerciseName.isEmpty() || setsStr.isEmpty() || repsStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int sets = Integer.parseInt(setsStr);
        int reps = Integer.parseInt(repsStr);
        double weight = Double.parseDouble(weightStr);

        // Create new workout object and add to log
        Workout newWorkout = new Workout(exerciseName, sets, reps, weight);
        workoutLog.addWorkout(newWorkout);

        // Add to list view for display
        workoutDisplayList.add(newWorkout.toString());
        adapter.notifyDataSetChanged();

        // Clear inputs for the next entry
        editTextExercise.setText("");
        editTextSets.setText("");
        editTextReps.setText("");
        editTextWeight.setText("");

        Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show();
    }
}
