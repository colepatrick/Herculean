package com.example.herculean.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.databinding.FragmentProfileBinding;
import com.example.herculean.workout.Workout;

public class FriendsProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        UserAccount user = GlobalData.viewedUser;

        if (user == null) {
            binding.profileUsername.setText("Error loading user");
            return binding.getRoot();
        }

        // Basic info
        binding.profileUsername.setText(user.getUsername());
        binding.profileUserEmail.setText(user.getEmail());

        Glide.with(this)
                .load(user.getProfileImageUri())
                .placeholder(R.drawable.avatar_filler)
                .into(binding.profilePicture);

        // Hide buttons
        binding.customizeButton.setVisibility(View.GONE);
        binding.friendsButton.setVisibility(View.GONE);

        // ---------- Stats With Labels ----------
        if (!user.getWorkoutLog().getWorkouts().isEmpty()) {

            Workout best = user.getBestWorkout();
            String bestText = (best != null) ? best.toString() : "No best workout";

            String favType = user.getFavoriteWorkoutType();
            if (favType == null) favType = "None";

            String favMuscle = user.getFavoriteMuscleGroup();
            if (favMuscle == null) favMuscle = "None";

            binding.bestWorkoutDetails.setText("Best Workout:\n" + bestText);
            binding.favoriteWorkoutType.setText("Favorite Workout Type:\n" + favType);
            binding.favoriteMuscleGroup.setText("Favorite Muscle Group:\n" + favMuscle);

        } else {
            binding.bestWorkoutDetails.setText("Best Workout:\nNo workouts yet");
            binding.favoriteWorkoutType.setText("Favorite Workout Type:\nNone");
            binding.favoriteMuscleGroup.setText("Favorite Muscle Group:\nNone");
        }

        // Graphs
        binding.workoutDaysGraph.removeAllSeries();
        binding.workoutDaysGraph.addSeries(
                new com.jjoe64.graphview.series.LineGraphSeries<>(
                        user.getDayDataPoints(14)
                )
        );

        binding.workoutMonthGraph.removeAllSeries();
        binding.workoutMonthGraph.addSeries(
                new com.jjoe64.graphview.series.LineGraphSeries<>(
                        user.getMonthDataPoints(12)
                )
        );

        binding.streakText.setText("");

        return binding.getRoot();
    }
}
