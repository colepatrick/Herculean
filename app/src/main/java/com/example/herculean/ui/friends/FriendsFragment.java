package com.example.herculean.ui.friends;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private ArrayList<UserAccount> allUsers = new ArrayList<>();
    private ArrayList<UserAccount> visibleUsers = new ArrayList<>();

    private FriendsRecycle adapter;
    private EditText searchBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        RecyclerView recycler = view.findViewById(R.id.allUsersRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = view.findViewById(R.id.searchBar);

        allUsers.addAll(GlobalData.accounts);

        adapter = new FriendsRecycle(visibleUsers, this::reloadList);
        recycler.setAdapter(adapter);

        reloadList();

        // ------------------------
        // SEARCH LISTENER
        // ------------------------
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reloadList();
            }
        });

        return view;
    }

    // ============================
    // Reload list (sorting + search)
    // ============================
    private void reloadList() {
        visibleUsers.clear();

        String currentUsername = GlobalData.currentUser.getUsername();
        ArrayList<String> following = GlobalData.currentUser.getFollowing();
        String query = searchBar.getText().toString().trim().toLowerCase();

        ArrayList<UserAccount> followedMatches = new ArrayList<>();
        ArrayList<UserAccount> unfollowedMatches = new ArrayList<>();

        for (UserAccount user : allUsers) {
            if (user.getUsername().equalsIgnoreCase(currentUsername)) continue;

            boolean matchesSearch = user.getUsername().toLowerCase().contains(query);
            if (!query.isEmpty() && !matchesSearch) continue;

            // FOLLOWED USERS FIRST
            if (following.contains(user.getUsername())) {
                followedMatches.add(user);
            } else {
                unfollowedMatches.add(user);
            }
        }

        // Sort followed by the order in the "following" list (most recent first)
        followedMatches.sort((a, b) -> {
            int posA = following.indexOf(a.getUsername());
            int posB = following.indexOf(b.getUsername());
            return Integer.compare(posA, posB);
        });

        visibleUsers.addAll(followedMatches);
        visibleUsers.addAll(unfollowedMatches);
        adapter.notifyDataSetChanged();
    }
}
