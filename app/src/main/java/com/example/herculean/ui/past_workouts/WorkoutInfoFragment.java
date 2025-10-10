package com.example.herculean.ui.past_workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.herculean.databinding.FragmentWorkoutInfoBinding;
import com.example.herculean.workout.Workout;

//This code basically loads the information in a workout dummy object and
// is used to load the workout info when it's assigned button is pressed
public class WorkoutInfoFragment extends Fragment {

    private FragmentWorkoutInfoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorkoutInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            Workout workout = (Workout) getArguments().getSerializable("workout");
            if (workout != null) {
                binding.textWorkoutName.setText(workout.getExerciseName());
                binding.textWorkoutDate.setText(workout.toString());
            }
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
