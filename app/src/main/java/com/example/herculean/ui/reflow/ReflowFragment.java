package com.example.herculean.ui.reflow;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.herculean.GlobalData;
import com.example.herculean.GoalAndScheduleActivity;
import com.example.herculean.UserAccount;
import com.example.herculean.databinding.FragmentProfileBinding;

public class ReflowFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReflowViewModel reflowViewModel =
                new ViewModelProvider(this).get(ReflowViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.goalAndScheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GoalAndScheduleActivity.class);
            startActivity(intent);
        });

        updateStreakDisplay();
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