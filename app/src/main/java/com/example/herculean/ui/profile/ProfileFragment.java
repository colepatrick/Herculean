package com.example.herculean.ui.profile;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.herculean.GlobalData;
import com.example.herculean.GoalAndScheduleActivity;
import com.example.herculean.R;
import com.example.herculean.UserAccount;
import com.example.herculean.databinding.FragmentProfileBinding;
import com.example.herculean.workout.Workout;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.customizeButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_nav_profile_settings);
        });

        binding.profileUsername.setText(GlobalData.currentUser.getUsername());
        binding.profileUserEmail.setText(GlobalData.currentUser.getEmail());
        Glide.with(this)
                .load(GlobalData.currentUser.getProfileImageUri())
                .centerCrop()
                .placeholder(R.drawable.avatar_filler)
                .error(R.drawable.avatar_filler)
                .into(binding.profilePicture);

        if(!GlobalData.currentUser.getWorkoutLog().getWorkouts().isEmpty()) {
            Workout best = GlobalData.currentUser.getBestWorkout();
            binding.bestWorkoutDetails.setText(best.toString());

            String favoriteWorkout = GlobalData.currentUser.getFavoriteWorkoutType();
            binding.favoriteWorkoutType.setText(favoriteWorkout);

            String favoriteMuscle = GlobalData.currentUser.getFavoriteMuscleGroup();
            binding.favoriteMuscleGroup.setText(favoriteMuscle);
        } else {
            binding.bestWorkoutDetails.setText(R.string.no_workouts);
            binding.favoriteWorkoutType.setText(R.string.no_workouts);
            binding.favoriteMuscleGroup.setText(R.string.no_workouts);
        }

        updateStreakDisplay();
        return binding.getRoot();
    }

    private void updateStreakDisplay() {
        UserAccount currentUser = GlobalData.currentUser;
        if (currentUser != null && currentUser.getUserStreak() != null) {
            int streak = currentUser.getUserStreak().getCurrentStreak();
            binding.streakText.setText("🔥 " + streak + " Week Streak");

            if (streak > 0) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(binding.streakText, "alpha", 0f, 1f);
                animator.setDuration(1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.start();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStreakDisplay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}