package com.example.herculean.workout;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;

import com.example.herculean.R;

public class Exercises extends Dialog {

    private OnExerciseSelectedListener listener;
    private OnWorkoutSelectedListener workoutListener;
    private List<Workout> allExercises = ExerciseDatabase.getAllExercises();
    private List<Workout> filteredExercises;
    private LinearLayout buttonContainer;

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
                // Why is this needed?
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExercises(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Why is this also needed?
            }
        });

        displayExercises();

        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void filterExercises(String query) {
        filteredExercises.clear();

        if (query.isEmpty()) {
            // If search empty, show all exercises
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

        if (filteredExercises.isEmpty()) {
            // Show "no results" message if nothing
            TextView noResults = new TextView(getContext());
            noResults.setText("No exercises found");
            noResults.setTextSize(16);
            noResults.setPadding(32, 32, 32, 32);
            buttonContainer.addView(noResults);
            return;
        }

        // Create buttons for filtered exercises
        for (Workout exercise : filteredExercises) {
            Button exerciseButton = getButton(exercise);
            buttonContainer.addView(exerciseButton);
        }
    }

    @SuppressLint("SetTextI18n")
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