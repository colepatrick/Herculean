package com.example.herculean.workout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;

import java.util.ArrayList;
import java.util.List;

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

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                filterExercises(currentSearchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        displayExercises();

        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void filterExercises(String query) {
        filteredExercises.clear();

        if (query.isEmpty()) {
            filteredExercises.addAll(allExercises);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Workout workout : allExercises) {
                if (workout.getExerciseName().toLowerCase().contains(lowerQuery) ||
                        workout.getBodyPart().toLowerCase().contains(lowerQuery)) {
                    filteredExercises.add(workout);
                }
            }
        }

        displayExercises();
    }

    private void displayExercises() {
        buttonContainer.removeAllViews();

        if (filteredExercises.isEmpty() && !currentSearchQuery.isEmpty()) {
            TextView noResults = new TextView(getContext());
            noResults.setText("No exercises found");
            noResults.setTextSize(16);
            noResults.setPadding(32, 16, 32, 16);
            noResults.setGravity(android.view.Gravity.CENTER);
            buttonContainer.addView(noResults);

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
            TextView noResults = new TextView(getContext());
            noResults.setText("No exercises available");
            noResults.setTextSize(16);
            noResults.setPadding(32, 32, 32, 32);
            noResults.setGravity(android.view.Gravity.CENTER);
            buttonContainer.addView(noResults);
            return;
        }

        for (Workout exercise : filteredExercises) {
            Button exerciseButton = getButton(exercise);
            buttonContainer.addView(exerciseButton);
        }
    }

    private void showCreateExerciseDialog(String exerciseName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create Custom Exercise");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        TextView nameLabel = new TextView(getContext());
        nameLabel.setText("Exercise Name:");
        nameLabel.setPadding(0, 0, 0, 8);
        layout.addView(nameLabel);

        EditText nameInput = new EditText(getContext());
        nameInput.setText(exerciseName);
        nameInput.setHint("e.g., Cable Flies");
        layout.addView(nameInput);

        TextView bodyPartLabel = new TextView(getContext());
        bodyPartLabel.setText("Body Part:");
        bodyPartLabel.setPadding(0, 20, 0, 8);
        layout.addView(bodyPartLabel);

        EditText bodyPartInput = new EditText(getContext());
        bodyPartInput.setHint("e.g., Chest, Arms, Legs");
        layout.addView(bodyPartInput);

        TextView workoutTypeLabel = new TextView(getContext());
        workoutTypeLabel.setText("Workout Type:");
        workoutTypeLabel.setPadding(0, 20, 0, 8);
        layout.addView(workoutTypeLabel);

        Spinner workoutTypeSpinner = new Spinner(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"Strength", "Bodyweight", "Cardio"});
        workoutTypeSpinner.setAdapter(adapter);
        layout.addView(workoutTypeSpinner);

        builder.setView(layout);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String newExerciseName = nameInput.getText().toString().trim();
            String bodyPart = bodyPartInput.getText().toString().trim();
            String workoutType = (String) workoutTypeSpinner.getSelectedItem();

            if (newExerciseName.isEmpty() || bodyPart.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Workout newWorkout;
            switch (workoutType) {
                case "Strength":
                    newWorkout = new Strength(newExerciseName, bodyPart, 0, 0, 0);
                    break;
                case "Bodyweight":
                    newWorkout = new Bodyweight(newExerciseName, bodyPart, 0, 0);
                    break;
                case "Cardio":
                    newWorkout = new Cardio(newExerciseName, bodyPart, 0, 0);
                    break;
                default:
                    return;
            }

            ExerciseDatabase.addCustomExercise(newWorkout);
            if (GlobalData.currentUser != null) {
                GlobalData.currentUser.addCustomExercise(newWorkout);
            }

            allExercises = ExerciseDatabase.getAllExercises();

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