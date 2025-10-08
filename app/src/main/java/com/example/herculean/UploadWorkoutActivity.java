package com.example.herculean;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import java.util.ArrayList;

// Import your model classes from the workout folder
import com.example.herculean.workout.Workout;
import com.example.herculean.workout.WorkoutLog;
import com.example.herculean.workout.workoutCardio;
import com.example.herculean.workout.workoutStrength;
import com.example.herculean.workout.workoutCustom;


public class UploadWorkoutActivity extends AppCompatActivity {
    private EditText editTextWeight, editTextSets, editTextReps;
    private Button buttonUploadWorkout, backButton;
    private WorkoutLog workoutLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reflow);

        editTextWeight = findViewById(R.id.editTextWeight);
        editTextSets   = findViewById(R.id.editTextSets);
        editTextReps   = findViewById(R.id.editTextReps);
        buttonUploadWorkout = findViewById(R.id.buttonUploadWorkout);
        backButton = findViewById(R.id.backButton);

        // now you can use the views, e.g. set listeners, read text, etc.
    }
}



