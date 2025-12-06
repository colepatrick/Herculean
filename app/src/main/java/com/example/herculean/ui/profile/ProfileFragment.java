package com.example.herculean.ui.profile;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.R;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.databinding.FragmentProfileBinding;
import com.example.herculean.workout.Workout;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.customizeButton.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_nav_profile_to_nav_profile_settings)
        );

        // Friends button navigation
        binding.friendsButton.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.nav_friends)
        );

        UserAccount displayUser = GlobalData.viewedUser != null
                ? GlobalData.viewedUser
                : GlobalData.currentUser;

        // Username + email
        binding.profileUsername.setText(displayUser.getUsername());
        binding.profileUserEmail.setText(displayUser.getEmail());

        // Profile picture
        Glide.with(this)
                .load(displayUser.getProfileImageUri())
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

        if (displayUser == GlobalData.currentUser) {
            binding.bmi.setVisibility(View.VISIBLE);
            binding.bmr.setVisibility(View.VISIBLE);
            // Calculate and display BMI and BMR
            double bmi = displayUser.calculateBmi();
            double bmr = displayUser.calculateBmr();
            binding.bmi.setText(String.format("BMI: %.2f kg/m^2", bmi));
            binding.bmr.setText(String.format("BMR: %.2f calories/day", bmr));
        } else {
            binding.bmi.setVisibility(View.GONE);
            binding.bmr.setVisibility(View.GONE);
        }

        refreshPastDaysGraph(14); // 14 day history
        refreshPastMonthsGraph(12); // 12 month history

        updateWeeklyProgressDisplay();
        return binding.getRoot();
    }

    private void updateWeeklyProgressDisplay() {
        UserAccount currentUser = GlobalData.currentUser;
        if (currentUser == null || currentUser.getUserGoal() == null) {
            binding.weekProgressText.setText("No goal set");
            binding.weekProgressBar.setProgress(0);
            binding.weekProgressBar.setMax(7);
            binding.currentStreakText.setText("🔥 0 Week Streak");
            return;
        }

        // Get required days per week from user goal
        int requiredDays = currentUser.getUserGoal().getDaysPerWeek();

        // Get current week's workout count
        int workoutDaysThisWeek = getWorkoutDaysThisWeek(currentUser);

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
        int currentStreak = currentUser.getUserStreak() != null
                ? currentUser.getUserStreak().getCurrentStreak()
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

    private void refreshPastDaysGraph(int days) {

        Log.d("GRAPH_DEBUG", "Day Points: "
                + Arrays.toString(GlobalData.currentUser.getDayDataPoints(days)));

        DataPoint[] points = GlobalData.currentUser.getDayDataPoints(days);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        binding.workoutDaysGraph.setTitle("Last " + days + " days of workout scores");
        binding.workoutDaysGraph.addSeries(series);

        binding.workoutDaysGraph.getViewport().setXAxisBoundsManual(true);
        binding.workoutDaysGraph.getViewport().setMinX(0);
        binding.workoutDaysGraph.getViewport().setMaxX(days-1);
        binding.workoutDaysGraph.setTitleTextSize(50);
        binding.workoutDaysGraph.getGridLabelRenderer().setNumHorizontalLabels(days/2);
        binding.workoutDaysGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return super.formatLabel(days-value-1, isValueX);
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    private void refreshPastMonthsGraph(int months) {
        Log.d("GRAPH_DEBUG", "Month Points: "
                + Arrays.toString(GlobalData.currentUser.getMonthDataPoints(months)));

        DataPoint[] points = GlobalData.currentUser.getMonthDataPoints(months);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        binding.workoutMonthGraph.setTitle("Last " + months + " months of workout scores");
        binding.workoutMonthGraph.addSeries(series);

        binding.workoutMonthGraph.getViewport().setXAxisBoundsManual(true);
        binding.workoutMonthGraph.getViewport().setMinX(0);
        binding.workoutMonthGraph.getViewport().setMaxX(months-1);
        binding.workoutMonthGraph.setTitleTextSize(50);
        binding.workoutMonthGraph.getGridLabelRenderer().setNumHorizontalLabels(months/2);
        binding.workoutMonthGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return super.formatLabel(months-value-1, isValueX);
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWeeklyProgressDisplay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalData.viewedUser = null;
        binding = null;
    }
}