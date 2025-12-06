package com.example.herculean.ui.friends;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.databinding.FragmentProfileBinding;
import com.example.herculean.workout.Workout;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

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

        // Hide buttons and BMI/BMR (private info)
        binding.customizeButton.setVisibility(View.GONE);
        binding.friendsButton.setVisibility(View.GONE);
        binding.bmi.setVisibility(View.GONE);
        binding.bmr.setVisibility(View.GONE);

        // Update weekly progress display for friend
        updateWeeklyProgressDisplay(user);

        // ---------- Stats With Labels ----------
        if (!user.getWorkoutLog().getWorkouts().isEmpty()) {

            Workout best = user.getBestWorkout();
            String bestText = (best != null) ? best.toString() : "No best workout";

            String favType = user.getFavoriteWorkoutType();
            if (favType == null) favType = "None";

            String favMuscle = user.getFavoriteMuscleGroup();
            if (favMuscle == null) favMuscle = "None";

            binding.bestWorkoutDetails.setText(bestText);
            binding.favoriteWorkoutType.setText(favType);
            binding.favoriteMuscleGroup.setText(favMuscle);

        } else {
            binding.bestWorkoutDetails.setText("No workouts yet");
            binding.favoriteWorkoutType.setText("None");
            binding.favoriteMuscleGroup.setText("None");
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

        return binding.getRoot();
    }

    private void updateWeeklyProgressDisplay(UserAccount user) {
        if (user == null || user.getUserGoal() == null) {
            binding.weekProgressText.setText("No goal set");
            binding.weekProgressBar.setProgress(0);
            binding.weekProgressBar.setMax(7);
            binding.currentStreakText.setText("🔥 0 Week Streak");
            return;
        }

        // Get required days per week from user goal
        int requiredDays = user.getUserGoal().getDaysPerWeek();

        // Get current week's workout count
        int workoutDaysThisWeek = getWorkoutDaysThisWeek(user);

        // Update progress bar
        binding.weekProgressBar.setMax(requiredDays);
        binding.weekProgressBar.setProgress(workoutDaysThisWeek);

        // Animate progress bar
        ObjectAnimator animation = ObjectAnimator.ofInt(
                binding.weekProgressBar,
                "progress",
                0,
                workoutDaysThisWeek
        );
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        // Update progress text
        String progressText = workoutDaysThisWeek + " / " + requiredDays + " days completed this week";
        binding.weekProgressText.setText(progressText);

        // Update streak display
        int currentStreak = user.getUserStreak() != null
                ? user.getUserStreak().getCurrentStreak()
                : 0;
        binding.currentStreakText.setText("🔥 " + currentStreak + " Week Streak");

        // Change color based on completion
        if (workoutDaysThisWeek >= requiredDays) {
            binding.weekProgressBar.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(0xFF4CAF50) // Green
            );
        } else {
            binding.weekProgressBar.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(0xFF2196F3) // Blue
            );
        }
    }

    private int getWorkoutDaysThisWeek(UserAccount user) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        List<LocalDate> workoutDates = new ArrayList<>();
        for (Workout workout : user.getWorkoutLog().getWorkouts()) {
            LocalDate workoutDate = workout.getDate();
            if (workoutDate != null &&
                    !workoutDate.isBefore(weekStart) &&
                    !workoutDate.isAfter(weekEnd)) {
                workoutDates.add(workoutDate);
            }
        }

        // Count distinct days
        return (int) workoutDates.stream().distinct().count();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}