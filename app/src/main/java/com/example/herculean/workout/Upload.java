package com.example.herculean.workout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.MainActivity;
import com.example.herculean.R;

import java.time.LocalDate;

public class Upload extends AppCompatActivity {

    private EditText editTextWeight, editTextSets, editTextReps,
            editTextBodyweightSets, editTextBodyweightReps,
            editTextDuration, editTextDistance;

    private LinearLayout strengthLayout, bodyweightLayout, cardioLayout;
    private ListView workoutList;
    private Button buttonUploadWorkout, homeButton, selectExerciseButton;

    private ArrayAdapter<Workout> adapter;
    private Workout selectedExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_workout);

        // UI linking
        strengthLayout = findViewById(R.id.strengthLayout);
        bodyweightLayout = findViewById(R.id.bodyweightLayout);
        cardioLayout = findViewById(R.id.cardioLayout);

        editTextWeight = findViewById(R.id.editTextWeight);
        editTextSets = findViewById(R.id.editTextSets);
        editTextReps = findViewById(R.id.editTextReps);

        editTextBodyweightSets = findViewById(R.id.editTextBodyweightSets);
        editTextBodyweightReps = findViewById(R.id.editTextBodyweightReps);

        editTextDuration = findViewById(R.id.editTextDuration);
        editTextDistance = findViewById(R.id.editTextDistance);

        buttonUploadWorkout = findViewById(R.id.buttonUploadWorkout);
        homeButton = findViewById(R.id.homeButton);
        selectExerciseButton = findViewById(R.id.selectExerciseButton);

        workoutList = findViewById(R.id.workoutList);

        // Adapter for workout list
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                GlobalData.currentUser.workoutLog.getWorkouts());
        workoutList.setAdapter(adapter);

        buttonUploadWorkout.setOnClickListener(v -> uploadWorkout());

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Upload.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Select exercise popup
        selectExerciseButton.setOnClickListener(v -> {
            Exercises dialog = new Exercises(this, (Exercises.OnWorkoutSelectedListener) exercise -> {

                selectedExercise = exercise;
                Toast.makeText(this, "Selected: " + exercise.getExerciseName(), Toast.LENGTH_SHORT).show();
                selectExerciseButton.setText("Exercise: " + exercise.getExerciseName());

                if (exercise instanceof Strength) {
                    strengthLayout.setVisibility(View.VISIBLE);
                    bodyweightLayout.setVisibility(View.GONE);
                    cardioLayout.setVisibility(View.GONE);
                } else if (exercise instanceof Bodyweight) {
                    strengthLayout.setVisibility(View.GONE);
                    bodyweightLayout.setVisibility(View.VISIBLE);
                    cardioLayout.setVisibility(View.GONE);
                } else if (exercise instanceof Cardio) {
                    strengthLayout.setVisibility(View.GONE);
                    bodyweightLayout.setVisibility(View.GONE);
                    cardioLayout.setVisibility(View.VISIBLE);

                    if (((Cardio) exercise).isDistanceBased()) {
                        editTextDistance.setVisibility(View.VISIBLE);
                    } else {
                        editTextDistance.setVisibility(View.GONE);
                    }
                }
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

    // Main dispatcher
    private void uploadWorkout() {
        if (selectedExercise == null) {
            Toast.makeText(this, "Please select an exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedExercise instanceof Strength) {
            uploadStrengthWorkout();
        } else if (selectedExercise instanceof Bodyweight) {
            uploadBodyweightWorkout();
        } else if (selectedExercise instanceof Cardio) {
            uploadCardioWorkout();
        }
    }

    // ----------------------------
    // Strength Logging
    // ----------------------------
    private void uploadStrengthWorkout() {

        String weight = editTextWeight.getText().toString();
        String set = editTextSets.getText().toString();
        String rep = editTextReps.getText().toString();

        if (weight.isEmpty() || set.isEmpty() || rep.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double weightValue;
        int setValue, repValue;

        try {
            weightValue = Double.parseDouble(weight);
            setValue = Integer.parseInt(set);
            repValue = Integer.parseInt(rep);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter NUMBERS ONLY!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Strength newStrength = new Strength(
                selectedExercise.getExerciseName(),
                selectedExercise.getBodyPart(),
                setValue,
                repValue,
                weightValue
        );
        newStrength.setDate(LocalDate.now());

        // Prevent template/empty workout
        if (newStrength.isEmptyWorkout()) {
            Toast.makeText(this, "Invalid workout — not saved", Toast.LENGTH_SHORT).show();
            return;
        }

        GlobalData.currentUser.workoutLog.addWorkout(newStrength);
        GlobalData.saveAccounts(this);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Workout uploaded successfully", Toast.LENGTH_SHORT).show();

        editTextWeight.setText("");
        editTextSets.setText("");
        editTextReps.setText("");

        selectedExercise = null;
        selectExerciseButton.setText("Select Exercise");
        strengthLayout.setVisibility(View.GONE);
    }

    // ----------------------------
    // Bodyweight Logging
    // ----------------------------
    private void uploadBodyweightWorkout() {

        String set = editTextBodyweightSets.getText().toString();
        String rep = editTextBodyweightReps.getText().toString();

        if (set.isEmpty() || rep.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int setValue, repValue;

        try {
            setValue = Integer.parseInt(set);
            repValue = Integer.parseInt(rep);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter NUMBERS ONLY!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bodyweight newBodyweight = new Bodyweight(
                selectedExercise.getExerciseName(),
                selectedExercise.getBodyPart(),
                setValue,
                repValue
        );
        newBodyweight.setDate(LocalDate.now());

        GlobalData.currentUser.workoutLog.addWorkout(newBodyweight);
        GlobalData.saveAccounts(this);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Workout uploaded successfully", Toast.LENGTH_SHORT).show();

        editTextBodyweightSets.setText("");
        editTextBodyweightReps.setText("");

        selectedExercise = null;
        selectExerciseButton.setText("Select Exercise");
        bodyweightLayout.setVisibility(View.GONE);
    }

    // ----------------------------
    // Cardio Logging
    // ----------------------------
    private void uploadCardioWorkout() {

        String duration = editTextDuration.getText().toString();
        String distance = editTextDistance.getText().toString();

        if (duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double durationValue, distanceValue = 0;

        try {
            durationValue = Double.parseDouble(duration);

            if (((Cardio) selectedExercise).isDistanceBased()) {
                if (distance.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                distanceValue = Double.parseDouble(distance);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter NUMBERS ONLY!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cardio newCardio;

        if (((Cardio) selectedExercise).isDistanceBased()) {
            newCardio = new Cardio(
                    selectedExercise.getExerciseName(),
                    selectedExercise.getBodyPart(),
                    durationValue,
                    distanceValue
            );
        } else {
            newCardio = new Cardio(
                    selectedExercise.getExerciseName(),
                    selectedExercise.getBodyPart(),
                    durationValue
            );
        }

        newCardio.setDate(LocalDate.now());
        GlobalData.currentUser.workoutLog.addWorkout(newCardio);

        GlobalData.saveAccounts(this);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Workout uploaded successfully", Toast.LENGTH_SHORT).show();

        editTextDuration.setText("");
        editTextDistance.setText("");

        selectedExercise = null;
        selectExerciseButton.setText("Select Exercise");
        cardioLayout.setVisibility(View.GONE);
    }
}
