package com.example.herculean.workout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

// Import your model classes from the workout folder
import com.example.herculean.R;


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


    }
}



