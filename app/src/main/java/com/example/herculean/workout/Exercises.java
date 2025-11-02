package com.example.herculean.workout;

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

    private OnCategorySelectedListener listener;
    private String[] workouts = {
            "Weightlifting",
            "Plyometrics",
            "Calisthenics",
            "Cardio",
            "Yoga",
            "Stretching",
            "CrossFit",
            "Powerlifting",
            "Olympic Lifting",
            "Bodybuilding",
            "HIIT"
    };

    public interface OnCategorySelectedListener {
        void onCategorySelected(String workout);
    }

    public Exercises(@NonNull Context context, OnCategorySelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
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
        for (String exercise : workouts) {
            Button categoryButton = getButton(exercise);

            buttonContainer.addView(categoryButton);
        }

        cancelButton.setOnClickListener(v -> dismiss());
    }

    @NonNull
    private Button getButton(String workout) {
        Button categoryButton = new Button(getContext());
        categoryButton.setText(workout);
        categoryButton.setTextSize(16);
        categoryButton.setPadding(32, 24, 32, 24);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        categoryButton.setLayoutParams(params);

        categoryButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategorySelected(workout);
            }
            dismiss();
        });
        return categoryButton;
    }
}