package com.example.herculean.workout;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.R;

public class Exercises extends Dialog {

    private OnExerciseSelectedListener listener;
    private OnWorkoutSelectedListener workoutListener;
    private List<Workout> allExercises = ExerciseDatabase.getAllExercises();
    private List<Workout> filteredExercises;
    private LinearLayout buttonContainer;
    private String currentSearchQuery = "";

    public interface OnExerciseSelectedListener {
        void onExerciseSelected(String workout);
    }

    public interface OnWorkoutSelectedListener {
        void onExerciseSelected(Workout workout);
    }

    public Exercises(Context context, OnExerciseSelectedListener listener) {
        super(context);
        this.listener = listener;
        this.filteredExercises = new ArrayList<>(allExercises);
    }

    public Exercises(Context context, OnWorkoutSelectedListener workoutListener) {
        super(context);
        this.workoutListener = workoutListener;
        this.filteredExercises = new ArrayList<>(allExercises);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exercise_popup);

        TextView title = findViewById(R.id.dialog_title);
        buttonContainer = findViewById(R.id.button_container);
        Button cancelButton = findViewById(R.id.cancel_button);
        EditText editSearch = findViewById(R.id.editSearch);

        title.setText("Select Exercise");

        // Set up search functionality
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                filterExercises(currentSearchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        // Display all exercises initially
        displayExercises();

        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void filterExercises(String query) {
        filteredExercises.clear();

        if (query.isEmpty()) {
            // If search is empty, show all exercises
            filteredExercises.addAll(allExercises);
        } else {
            String lowerQuery = query.toLowerCase();
            // Filter exercises by name or body part
            for (Workout workout : allExercises) {
                if (workout.getExerciseName().toLowerCase().contains(lowerQuery) ||
                        workout.getBodyPart().toLowerCase().contains(lowerQuery)) {
                    filteredExercises.add(workout);
                }
            }
        }

        // Refresh the displayed exercises
        displayExercises();
    }

    private void displayExercises() {
        buttonContainer.removeAllViews();

        if (filteredExercises.isEmpty() && !currentSearchQuery.isEmpty()) {
            // Show "Create Exercise" button when search has no results
            TextView noResults = new TextView(getContext());
            noResults.setText("No exercises found");
            noResults.setTextSize(16);
            noResults.setPadding(32, 16, 32, 16);
            noResults.setGravity(android.view.Gravity.CENTER);
            buttonContainer.addView(noResults);

            // Create Exercise Button
            Button createButton = new Button(getContext());
            createButton.setText("Create Exercise: \"" + currentSearchQuery + "\"");
            createButton.setTextSize(16);
            createButton.setPadding(32, 24, 32, 24);
            createButton.setAllCaps(false);
            createButton.setBackgroundColor(0xFF4CAF50); // Green color
            createButton.setTextColor(0xFFFFFFFF); // White text

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 8);
            createButton.setLayoutParams(params);

            createButton.setOnClickListener(v -> showCreateExerciseDialog(currentSearchQuery));

            buttonContainer.addView(createButton);
            return;
        }

        if (filteredExercises.isEmpty()) {
            // No exercises at all (shouldn't happen with database)
            TextView noResults = new TextView(getContext());
            noResults.setText("No exercises available");
            noResults.setTextSize(16);
            noResults.setPadding(32, 32, 32, 32);
            noResults.setGravity(android.view.Gravity.CENTER);
            buttonContainer.addView(noResults);
            return;
        }

        // Create buttons for filtered exercises
        for (Workout exercise : filteredExercises) {
            Button exerciseButton = getButton(exercise);
            buttonContainer.addView(exerciseButton);
        }
    }

    private void showCreateExerciseDialog(String exerciseName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create Custom Exercise");

        // Create a layout for the dialog
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // Exercise name field (pre-filled with search query)
        TextView nameLabel = new TextView(getContext());
        nameLabel.setText("Exercise Name:");
        nameLabel.setPadding(0, 0, 0, 8);
        layout.addView(nameLabel);

        EditText nameInput = new EditText(getContext());
        nameInput.setText(exerciseName);
        nameInput.setHint("e.g., Cable Flies");
        layout.addView(nameInput);

        // Body part field
        TextView bodyPartLabel = new TextView(getContext());
        bodyPartLabel.setText("Body Part:");
        bodyPartLabel.setPadding(0, 20, 0, 8);
        layout.addView(bodyPartLabel);

        EditText bodyPartInput = new EditText(getContext());
        bodyPartInput.setHint("e.g., Chest, Arms, Legs");
        layout.addView(bodyPartInput);

        builder.setView(layout);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String newExerciseName = nameInput.getText().toString().trim();
            String bodyPart = bodyPartInput.getText().toString().trim();

            if (newExerciseName.isEmpty() || bodyPart.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // adds new exercise to database
            ExerciseDatabase.addCustomExercise(newExerciseName, bodyPart);
            GlobalData.currentUser.addCustomExercise(new Workout(exerciseName, bodyPart));

            allExercises = ExerciseDatabase.getAllExercises();

            // Create workout object and return to caller
            Workout newWorkout = new Workout(newExerciseName, bodyPart);

            if (listener != null) {
                listener.onExerciseSelected(newWorkout.getExerciseName());
            }
            if (workoutListener != null) {
                workoutListener.onExerciseSelected(newWorkout);
            }

            Toast.makeText(getContext(), "Exercise created: " + newExerciseName, Toast.LENGTH_SHORT).show();
            dismiss();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @NonNull
    private Button getButton(Workout workout) {
        Button exerciseButton = new Button(getContext());
        exerciseButton.setText(workout.getExerciseName());
        exerciseButton.setTextSize(16);
        exerciseButton.setPadding(32, 24, 32, 24);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        exerciseButton.setLayoutParams(params);

        exerciseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseSelected(workout.getExerciseName());
            }
            if (workoutListener != null) {
                workoutListener.onExerciseSelected(workout);
            }
            dismiss();
        });
        return exerciseButton;
    }
}