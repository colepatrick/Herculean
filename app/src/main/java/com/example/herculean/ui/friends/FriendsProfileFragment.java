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
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        UserAccount user = GlobalData.viewedUser; // ⭐ THE IMPORTANT PART

        if (user == null) {
            binding.profileUsername.setText("Error: No user loaded");
            return binding.getRoot();
        }

        // Basic info
        binding.profileUsername.setText(user.getUsername());
        binding.profileUserEmail.setText(user.getEmail());

        // Show profile picture
        Glide.with(this)
                .load(user.getProfileImageUri())
                .centerCrop()
                .placeholder(R.drawable.avatar_filler)
                .error(R.drawable.avatar_filler)
                .into(binding.profilePicture);

        // Workout stats
        if (!user.getWorkoutLog().getWorkouts().isEmpty()) {
            Workout best = user.getBestWorkout();
            binding.bestWorkoutDetails.setText(best.toString());
            binding.favoriteWorkoutType.setText(user.getFavoriteWorkoutType());
            binding.favoriteMuscleGroup.setText(user.getFavoriteMuscleGroup());
        } else {
            binding.bestWorkoutDetails.setText(R.string.no_workouts);
            binding.favoriteWorkoutType.setText(R.string.no_workouts);
            binding.favoriteMuscleGroup.setText(R.string.no_workouts);
        }

        // Streak
        int streak = user.getUserStreak().getCurrentStreak();
        binding.streakText.setText("🔥 " + streak + " Week Streak");

        // Hide customize button + friends button
        binding.customizeButton.setVisibility(View.GONE);
        binding.friendsButton.setVisibility(View.GONE);

        return binding.getRoot();
    }
}
