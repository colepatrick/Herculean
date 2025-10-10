package com.example.herculean.ui.past_workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.herculean.GlobalData;
import com.example.herculean.R;
import com.example.herculean.databinding.FragmentPastWorkoutsBinding;
import com.example.herculean.workout.Workout;

import java.util.ArrayList;
import java.util.List;

// This file basically controls the past workouts screen,
// what shows up on said screen, how the buttons work,
// search bar!! type shit

public class PastWorkoutsFragment extends Fragment {

    private FragmentPastWorkoutsBinding binding;
    private List<Workout> allWorkouts;

    @Override //god bless chatgpt inshallah
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        PastWorkoutsViewModel viewModel = new ViewModelProvider(this).get(PastWorkoutsViewModel.class);

        binding = FragmentPastWorkoutsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //OBSERVER WAHOO
        viewModel.getWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            allWorkouts = GlobalData.currentUser.workoutLog.getWorkouts();
            displayRecentWorkouts(workouts); // show 7 most recent workouts
        });

        // basically sets up how the search bar works
        // (you have to hit the search sympole to open the text input
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterWorkouts(query); //calls the function we're using to search by date
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterWorkouts(newText); //This should make it so whenever the text input changes
                //the list is updated to refelct the new input
                return false;
            }
        });

        return root;
    }

    // This makes sure that there are only the 7 most recent workouts displayed at a time
    private void displayRecentWorkouts(List<Workout> Dummies) {
        binding.layoutPastWorkouts.removeAllViews();

        if (Dummies == null || Dummies.isEmpty()) return;

        // sorts to make sure of most recent
        Dummies.sort((w1, w2) -> w2.getDate().compareTo(w1.getDate()));

        // selecting only seven
        List<Workout> rec = Dummies.size() > 7 ? Dummies.subList(0, 7) : Dummies;

        int count = rec.size();

        for (Workout workout : rec) { //cycles through each workout object
            Button btn = new Button(requireContext()); // creates a button for each workout up to 7
            btn.setText(workout.toString());
            //The line above is what is shown ontop
            // the button I.E the workout name and below it, date
            btn.setAllCaps(false);

            // makes the buttons are same size
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
            );
            params.setMargins(0, 8, 0, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> { // this sets up what happens when the button is pressed
                Bundle args = new Bundle();
                args.putSerializable("workout", workout);
                Navigation.findNavController(v).navigate(R.id.nav_past_workouts, args);
            });

            binding.layoutPastWorkouts.addView(btn);
        }
    }


    // This is our filter for the search
    private void filterWorkouts(String in) {
        if (allWorkouts == null) return;

        List<Workout> filt = new ArrayList<>();
        for (Workout w : allWorkouts) {
            if (w.getDate().toString().contains(in)) {
                filt.add(w);
            }
        }

        displayRecentWorkouts(filt);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
