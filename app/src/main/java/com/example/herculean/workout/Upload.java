package com.example.herculean.workout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

// Import your model classes from the workout folder
import com.example.herculean.GlobalData;
import com.example.herculean.MainActivity;
import com.example.herculean.R;


public class Upload extends AppCompatActivity {
    private EditText editTextWeight, editTextSets, editTextReps;
    private ListView workoutList;
    private Button buttonUploadWorkout, homeButton;
    private Spinner spinner;
    private ArrayAdapter<Workout> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_workout);

        editTextWeight = findViewById(R.id.editTextWeight);
        editTextSets = findViewById(R.id.editTextSets);
        editTextReps = findViewById(R.id.editTextReps);
        buttonUploadWorkout = findViewById(R.id.buttonUploadWorkout);
        spinner = findViewById(R.id.spinner);
        homeButton = findViewById(R.id.homeButton);
        workoutList = findViewById(R.id.workoutList);

//        buttonUploadWorkout.setClickable(true);
//        buttonUploadWorkout.setEnabled(true);

        buttonUploadWorkout.setOnClickListener(v -> {
            uploadWorkout();
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Upload.this, MainActivity.class);
            startActivity(intent);
        });


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, GlobalData.currentUser.workoutLog.getWorkouts());
        workoutList.setAdapter(adapter);

        workoutList.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete workout?").setMessage(GlobalData.currentUser.workoutLog.getWorkouts().get(position).toString())
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
        String exercise = spinner.getSelectedItem().toString();

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
        }
        catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter NUMBERS ONLY!!!", Toast.LENGTH_SHORT).show();
            return;
        }


        Workout newWorkout;
        switch (exercise) {
            case "Bicep Curls": {
                newWorkout = new Workout("Bicep Curls", "Biceps", setValue, repValue, weightValue);
                break;
            }
            case "Tricep Pushdown": {
                newWorkout = new Workout("Tricep Pushdown", "Triceps", setValue, repValue, weightValue);
                break;
            }
            case "Squat": {
                newWorkout = new Workout("Squat", "Legs", setValue, repValue, weightValue);
                break;
            }
            case "Deadlift": {
                newWorkout = new Workout("Deadlift", "Back", setValue, repValue, weightValue);
                break;
            }
            case "Bench Press": {
                newWorkout = new Workout("Bench Press", "Chest", setValue, repValue, weightValue);
                break;
            }
            case "Lat Pulldown": {
                newWorkout = new Workout("Lat Pulldown", "Back", setValue, repValue, weightValue);
                break;
            }
            default: {
                Toast.makeText(this, "Unknown exercise: " + exercise, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        GlobalData.currentUser.workoutLog.addWorkout(newWorkout);
        adapter.notifyDataSetChanged();
        GlobalData.saveAccounts(this);

        Toast.makeText(this, "Workout uploaded successfully", Toast.LENGTH_SHORT).show();
        editTextWeight.setText("");
        editTextSets.setText("");
        editTextReps.setText("");
        spinner.setSelection(0);
    }
}