package com.example.herculean.ui.pastworkouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.herculean.ui.past_workouts.WorkoutDummy;
import com.example.herculean.databinding.FragmentWorkoutInfoDummyBinding;

//This code basically loads the information in a workout dummy object and
// is used to load the workout info when it's assigned button is pressed
public class WorkoutInfoDummyFragment extends Fragment {

    private FragmentWorkoutInfoDummyBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorkoutInfoDummyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            WorkoutDummy workout = (WorkoutDummy) getArguments().getSerializable("workout");
            if (workout != null) {
                binding.textWorkoutName.setText(workout.getWorkoutName());
                binding.textWorkoutDate.setText("SHREDDED ON ->" + workout.getDate() + "!!!!!!");
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
