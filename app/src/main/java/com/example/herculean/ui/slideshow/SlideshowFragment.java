package com.example.herculean.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.herculean.databinding.FragmentLeaderboardBinding;

public class SlideshowFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private SlideshowViewModel slideshowViewModel;
    private ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView leaderboardList = binding.leaderboardList;

        // Observe leaderboard data from ViewModel
        slideshowViewModel.getLeaderboard().observe(getViewLifecycleOwner(), leaderboardEntries -> {
            if (leaderboardEntries != null && !leaderboardEntries.isEmpty()) {
                adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        leaderboardEntries
                );
                leaderboardList.setAdapter(adapter);
            }
        });

        // Observe error messages
        slideshowViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}