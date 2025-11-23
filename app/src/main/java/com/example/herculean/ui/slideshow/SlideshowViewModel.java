package com.example.herculean.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<List<String>> leaderboard;

    public SlideshowViewModel() {
        leaderboard = new MutableLiveData<>();
        loadLeaderboard();
    }

    public LiveData<List<String>> getLeaderboard() {
        return leaderboard;
    }

    private void loadLeaderboard() {
        // Get all users and sort by streak
        List<UserAccount> sortedUsers = new ArrayList<>(GlobalData.accounts);

        sortedUsers.sort(new Comparator<UserAccount>() {
            @Override
            public int compare(UserAccount u1, UserAccount u2) {
                return Integer.compare(u2.getUserStreak().getCurrentStreak(),
                        u1.getUserStreak().getCurrentStreak());
            }
        });

        // Create display strings
        List<String> leaderboardEntries = new ArrayList<>();
        int rank = 1;

        for (UserAccount user : sortedUsers) {
            String entry = rank + ". " + user.getUsername() + " - Streak " + user.getUserStreak().getCurrentStreak();
            leaderboardEntries.add(entry);
            rank++;
        }

        // If no users, show message
        if (leaderboardEntries.isEmpty()) {
            leaderboardEntries.add("No users yet!");
        }

        leaderboard.setValue(leaderboardEntries);
    }

    // Call this method to refresh the leaderboard
    public void refreshLeaderboard() {
        loadLeaderboard();
    }
}