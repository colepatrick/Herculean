package com.example.herculean.workout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import com.example.herculean.GlobalData;
import com.example.herculean.MainActivity;
import com.example.herculean.R;

import java.time.LocalDate;

public class Upload extends AppCompatActivity {
    private EditText editTextWeight, editTextSets, editTextReps;
    private ListView workoutList;
    private Button buttonUploadWorkout, homeButton;
    private Button selectExerciseButton;
    private ArrayAdapter<Workout> adapter;
    private Workout selectedExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_workout);

        editTextWeight = findViewById(R.id.editTextWeight);
        editTextSets = findViewById(R.id.editTextSets);
        editTextReps = findViewById(R.id.editTextReps);
        buttonUploadWorkout = findViewById(R.id.buttonUploadWorkout);
        selectExerciseButton = findViewById(R.id.selectExerciseButton);
        homeButton = findViewById(R.id.homeButton);
        workoutList = findViewById(R.id.workoutList);

        buttonUploadWorkout.setOnClickListener(v -> uploadWorkout());

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Upload.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, GlobalData.currentUser.workoutLog.getWorkouts());
        workoutList.setAdapter(adapter);

        selectExerciseButton.setOnClickListener(v -> {
            @SuppressLint("SetTextI18n")
            Exercises dialog = new Exercises(this.peekAvailableContext(), (Exercises.OnWorkoutSelectedListener) exercise -> {
                selectedExercise = exercise;
                Toast.makeText(this, "Selected: " + exercise.getExerciseName(), Toast.LENGTH_SHORT).show();

                selectExerciseButton.setText("Exercise: " + exercise.getExerciseName());
            });

            dialog.show();
        });
        workoutList.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete workout?")
                    .setMessage(GlobalData.currentUser.workoutLog.getWorkouts().get(position).toString())
                    .setPositiveButton("Delete", (d, w) -> {
                        GlobalData.currentUser.workoutLog.getWorkouts().remove(position);
                        GlobalData.saveAccounts(this);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null).show();
            return true;
        });
    }

    private void uploadWorkout() {
        String weight = editTextWeight.getText().toString();
        String set = editTextSets.getText().toString();
        String rep = editTextReps.getText().toString();

        if (selectedExercise == null) {
            Toast.makeText(this, "Please select an exercise", Toast.LENGTH_SHORT).show();
            return;
        }

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
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter NUMBERS ONLY!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedExercise.setReps(repValue);
        selectedExercise.setWeight(weightValue);
        selectedExercise.setSets(setValue);
        selectedExercise.setDate(LocalDate.now());
        GlobalData.currentUser.workoutLog.addWorkout(selectedExercise);
        adapter.notifyDataSetChanged();
        GlobalData.saveAccounts(this);

        Toast.makeText(this, "Workout uploaded successfully", Toast.LENGTH_SHORT).show();

        editTextWeight.setText("");
        editTextSets.setText("");
        editTextReps.setText("");
        selectedExercise = null;
        selectExerciseButton.setText("Select Exercise");
    }
}