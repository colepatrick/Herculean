package com.example.herculean.ui.friends;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.databinding.FragmentFriendsBinding;
import com.example.herculean.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;
    private FriendsRecycle adapter;
    private List<UserAccount> allUsers = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        binding.friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FriendsRecycle(new ArrayList<>(), user -> {
            GlobalData.viewedUser = user;
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_nav_friends_to_nav_view_friend_profile);
        });

        binding.friendsRecycler.setAdapter(adapter);

        loadFriendsFromServer();
        setupSearch();

        return binding.getRoot();
    }

    private void setupSearch() {
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters(String query) {
        query = query.toLowerCase();

        List<UserAccount> filtered = new ArrayList<>();

        for (UserAccount u : allUsers) {
            if (u.getUsername().toLowerCase().contains(query) ||
                    u.getEmail().toLowerCase().contains(query)) {
                filtered.add(u);
            }
        }

        sortUsers(filtered);
        adapter.updateData(filtered);
    }

    private void loadFriendsFromServer() {

        GlobalData.svc.listAccounts().enqueue(new Callback<List<UserAccount>>() {
            @Override
            public void onResponse(Call<List<UserAccount>> call, Response<List<UserAccount>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                allUsers = response.body();

                allUsers.removeIf(u -> u.getUsername()
                        .equals(GlobalData.currentUser.getUsername()));

                sortUsers(allUsers);
                adapter.updateData(allUsers);
            }

            @Override
            public void onFailure(Call<List<UserAccount>> call, Throwable t) {}
        });
    }

    // FOLLOW SORTING — FOLLOWED FIRST (NEWEST FIRST)
    private void sortUsers(List<UserAccount> list) {

        List<String> following = GlobalData.currentUser.getFollowing();

        list.sort((a, b) -> {

            boolean aFollow = following.contains(a.getUsername());
            boolean bFollow = following.contains(b.getUsername());

            if (aFollow && !bFollow) return -1;
            if (!aFollow && bFollow) return 1;

            // both followed → order by recency
            if (aFollow) {
                int ia = following.indexOf(a.getUsername());
                int ib = following.indexOf(b.getUsername());
                return Integer.compare(ia, ib);
            }

            // both unfollowed → alphabetical
            return a.getUsername().compareToIgnoreCase(b.getUsername());
        });
    }
}
