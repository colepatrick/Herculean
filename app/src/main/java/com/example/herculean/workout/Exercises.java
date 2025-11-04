package com.example.herculean.workout;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.herculean.R;

public class Exercises extends Dialog {

    private OnExerciseSelectedListener listener;
    private OnWorkoutSelectedListener workoutListener;
    private List<Workout> allExercises = ExerciseDatabase.getAllExercises();

    public interface OnExerciseSelectedListener {
        void onExerciseSelected(String workout);
    }

    public interface OnWorkoutSelectedListener {
        void onExerciseSelected(Workout workout);
    }

    public Exercises(Context context, OnExerciseSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    public Exercises(Context context, OnWorkoutSelectedListener workoutListener) {
        super(context);
        this.workoutListener = workoutListener;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exercise_popup);

        TextView title = findViewById(R.id.dialog_title);
        LinearLayout buttonContainer = findViewById(R.id.button_container);
        Button cancelButton = findViewById(R.id.cancel_button);

        title.setText("Select Exercise");

        // Create buttons for each workout
        for (Workout exercise : allExercises) {
            Button exerciseButton = getButton(exercise);
            buttonContainer.addView(exerciseButton);
        }

        cancelButton.setOnClickListener(v -> dismiss());
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
        params.setMargins(0, 0, 0, 16);
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