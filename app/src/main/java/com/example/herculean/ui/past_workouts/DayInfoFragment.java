package com.example.herculean.ui.past_workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;import androidx.fragment.app.Fragment;
import com.example.herculean.databinding.FragmentDayInfoBinding;
import com.example.herculean.workout.Workout;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DayInfoFragment extends Fragment {

    private FragmentDayInfoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDayInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            List<Workout> workoutsForDay = (List<Workout>) getArguments().getSerializable("workoutsForDay");

            if (workoutsForDay != null && !workoutsForDay.isEmpty()) {
                // Set the date title from the first workout
                binding.textDayDate.setText(workoutsForDay.get(0).getDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

                // Dynamically add a TextView for each workout
                for (Workout workout : workoutsForDay) {
                    TextView workoutTextView = new TextView(getContext());
                    workoutTextView.setText(workout.toString()); // Uses your existing workout.toString()
                    workoutTextView.setTextSize(16f);
                    workoutTextView.setPadding(0, 8, 0, 8);
                    binding.layoutDayWorkouts.addView(workoutTextView);
                }
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
