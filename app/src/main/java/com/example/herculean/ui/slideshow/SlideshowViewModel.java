package com.example.herculean.ui.slideshow;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.herculean.database.AccountService;
import com.example.herculean.datahandling.UserAccount;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<List<String>> leaderboard;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<Boolean> isLoading;
    private final AccountService accountService;

    public SlideshowViewModel() {
        leaderboard = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);

        // Use the existing AccountService from GlobalData
        accountService = com.example.herculean.datahandling.GlobalData.svc;

        loadLeaderboard();
    }

    public LiveData<List<String>> getLeaderboard() {
        return leaderboard;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadLeaderboard() {
        isLoading.setValue(true);

        accountService.listAccounts().enqueue(new Callback<List<UserAccount>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserAccount>> call, @NonNull Response<List<UserAccount>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<UserAccount> accounts = response.body();
                    displayLeaderboard(accounts);
                } else {
                    errorMessage.setValue("Failed to load leaderboard: HTTP " + response.code());
                    // Fallback to local data
                    loadLocalLeaderboard();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserAccount>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
                // Fallback to local data when network fails
                loadLocalLeaderboard();
            }
        });
    }

    private void loadLocalLeaderboard() {
        // Load from GlobalData as fallback
        List<UserAccount> accounts = com.example.herculean.datahandling.GlobalData.jsonData.accounts;
        if (accounts != null && !accounts.isEmpty()) {
            displayLeaderboard(new ArrayList<>(accounts));
        } else {
            List<String> emptyList = new ArrayList<>();
            emptyList.add("No users available");
            leaderboard.setValue(emptyList);
        }
    }

    private void displayLeaderboard(List<UserAccount> accounts) {
        // Sort by Streak in descending order
        accounts.sort((u1, u2) -> Integer.compare(
                u2.getUserStreak().getCurrentStreak(),
                u1.getUserStreak().getCurrentStreak()
        ));

        // Create display strings
        List<String> leaderboardEntries = new ArrayList<>();
        int rank = 1;

        for (UserAccount user : accounts) {
            String entry = rank + ". " + user.getUsername() +
                    " - Streak " + user.getUserStreak().getCurrentStreak();
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