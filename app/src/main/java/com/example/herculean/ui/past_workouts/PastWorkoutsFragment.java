package com.example.herculean.ui.past_workouts;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.herculean.R;
import com.example.herculean.databinding.FragmentPastWorkoutsBinding;
import com.example.herculean.workout.Workout;
import com.google.android.material.color.MaterialColors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PastWorkoutsFragment extends Fragment {

    private FragmentPastWorkoutsBinding binding;
    private PastWorkoutsViewModel viewModel; // Reference to the ViewModel

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(PastWorkoutsViewModel.class);
        binding = FragmentPastWorkoutsBinding.inflate(inflater, container, false);

        // Observe LiveData
        viewModel.getGroupedWorkouts().observe(getViewLifecycleOwner(), this::displayWorkoutsByDay);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // when user hits enter
                viewModel.filterAndGroupWorkouts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // runs as user types
                viewModel.filterAndGroupWorkouts(newText);
                return true;
            }
        });


        return binding.getRoot();
    }

    private void displayWorkoutsByDay(Map<LocalDate, List<Workout>> workoutsByDay) {
        binding.layoutPastWorkouts.removeAllViews();

        if (workoutsByDay == null || workoutsByDay.isEmpty()) {
            // No workouts
            return;
        }

        // Iterate through map, already sorted by date
        for (Map.Entry<LocalDate, List<Workout>> entry : workoutsByDay.entrySet()) {
            LocalDate date = entry.getKey();
            List<Workout> workoutsForDay = entry.getValue();

            Button btn = new Button(requireContext());

            // Format the button text to show the date and number of workouts
            String buttonText = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")) +
                    "\n(" + workoutsForDay.size() + " workouts)";
            btn.setText(buttonText);

            // Color stuff
            btn.setAllCaps(false);
            btn.setPadding(40, 20, 40, 20);
            int bgColor = MaterialColors.getColor(btn, com.google.android.material.R.attr.colorPrimaryContainer);
            int textColor = MaterialColors.getColor(btn, com.google.android.material.R.attr.colorOnPrimaryContainer);
            btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            btn.setTextColor(textColor);
            btn.setElevation(8f);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 16);
            btn.setLayoutParams(params);
            btn.setGravity(Gravity.CENTER);

            // Navigation
            btn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                // We must cast the List to Serializable to pass it
                args.putSerializable("workoutsForDay", (Serializable) workoutsForDay);
                Navigation.findNavController(v).navigate(R.id.action_pastWorkouts_to_dayInfo, args);
            });

            binding.layoutPastWorkouts.addView(btn);

            // in PastWorkoutsFragment.java

            binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // when user hits enter
                    viewModel.filterAndGroupWorkouts(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // runs as user types
                    viewModel.filterAndGroupWorkouts(newText);
                    return true;
                }
            });

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
