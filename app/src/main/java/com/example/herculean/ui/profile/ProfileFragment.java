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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.customizeButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_nav_profile_settings);
        });

        binding.moreGraphsButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_nav_profile_graphs);
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

        refreshPastDaysGraph(14); // 14 day history
        refreshPastMonthsGraph(12); // 12 month history

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

    private void refreshPastDaysGraph(int days) {
        DataPoint[] points = GlobalData.currentUser.getDayDataPoints(days);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        binding.workoutDaysGraph.setTitle("Last " + days + " days of workout scores");
        binding.workoutDaysGraph.addSeries(series);

        binding.workoutDaysGraph.getViewport().setXAxisBoundsManual(true);
        binding.workoutDaysGraph.getViewport().setMinX(0);
        binding.workoutDaysGraph.getViewport().setMaxX(days-1);
        binding.workoutDaysGraph.setTitleTextSize(50);
        binding.workoutDaysGraph.getGridLabelRenderer().setNumHorizontalLabels(days);
        binding.workoutDaysGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) { // For x values
                    // show inverted day numbers, 0 days is today
                    return super.formatLabel(days-value-1, isValueX);
                } else { // For y values
                    // show regular y numbers
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    private void refreshPastMonthsGraph(int months) {
        DataPoint[] points = GlobalData.currentUser.getMonthDataPoints(months);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        binding.workoutMonthGraph.setTitle("Last " + months + " months of workout scores");
        binding.workoutMonthGraph.addSeries(series);

        binding.workoutMonthGraph.getViewport().setXAxisBoundsManual(true);
        binding.workoutMonthGraph.getViewport().setMinX(0);
        binding.workoutMonthGraph.getViewport().setMaxX(months-1);
        binding.workoutMonthGraph.setTitleTextSize(50);
        binding.workoutMonthGraph.getGridLabelRenderer().setNumHorizontalLabels(months);
        binding.workoutMonthGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) { // For x values
                    // show inverted month numbers 0 months is this month
                    return super.formatLabel(months-value-1, isValueX);
                } else { // For y values
                    // show regular y numbers
                    return super.formatLabel(value, isValueX);
                }
            }
        });
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