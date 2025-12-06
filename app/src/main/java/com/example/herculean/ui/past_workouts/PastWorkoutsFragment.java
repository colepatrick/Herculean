package com.example.herculean.ui.past_workouts;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.herculean.R;
import com.example.herculean.databinding.FragmentPastWorkoutsBinding;
import com.example.herculean.workout.Workout;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PastWorkoutsFragment extends Fragment {

    private FragmentPastWorkoutsBinding binding;
    private PastWorkoutsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(PastWorkoutsViewModel.class);
        binding = FragmentPastWorkoutsBinding.inflate(inflater, container, false);

        // Use TextInputEditText instead of SearchView
        TextInputEditText searchBar = binding.searchBar;

        // 💬 Search listener (live updating)
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterAndGroupWorkouts(s.toString().trim());
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        // Observe LiveData
        viewModel.getGroupedWorkouts().observe(getViewLifecycleOwner(), this::displayWorkoutsByDay);

        return binding.getRoot();
    }

    private void displayWorkoutsByDay(Map<LocalDate, List<Workout>> workoutsByDay) {
        binding.layoutPastWorkouts.removeAllViews();

        if (workoutsByDay == null || workoutsByDay.isEmpty()) {
            return;
        }

        // Create one button per date group
        for (Map.Entry<LocalDate, List<Workout>> entry : workoutsByDay.entrySet()) {
            LocalDate date = entry.getKey();
            List<Workout> workoutsForDay = entry.getValue();

            Button btn = new Button(requireContext());

            String buttonText = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")) +
                    "\n(" + workoutsForDay.size() + " workouts)";
            btn.setText(buttonText);

            // Style (Material color system)
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

            // Navigation to day's workout info
            btn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putSerializable("workoutsForDay", (Serializable) workoutsForDay);
                Navigation.findNavController(v).navigate(R.id.action_pastWorkouts_to_dayInfo, args);
            });

            binding.layoutPastWorkouts.addView(btn);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
