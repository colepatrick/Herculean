package com.example.herculean.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private ArrayList<UserAccount> visibleUsers = new ArrayList<>();
    private FriendsRecycle adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        RecyclerView recycler = view.findViewById(R.id.allUsersRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadVisibleUsers();

        adapter = new FriendsRecycle(visibleUsers, () -> {
            loadVisibleUsers();
            adapter.notifyDataSetChanged();
        });


        recycler.setAdapter(adapter);

        recycler.setAdapter(adapter);

        return view;
    }

    private void loadVisibleUsers() {
        visibleUsers.clear();

        UserAccount currentUser = GlobalData.currentUser;

        ArrayList<UserAccount> followed = new ArrayList<>();
        ArrayList<UserAccount> notFollowed = new ArrayList<>();

        for (UserAccount account : GlobalData.accounts) {

            // Skip your own account
            if (account.getUsername().equalsIgnoreCase(currentUser.getUsername())) {
                continue;
            }

            if (currentUser.getFollowing().contains(account.getUsername())) {
                followed.add(account);
            } else {
                notFollowed.add(account);
            }


        }

        // Sort: followed users first, in the order user followed them
        visibleUsers.sort((a, b) -> {
            ArrayList<String> following = GlobalData.currentUser.getFollowing();

            int indexA = following.indexOf(a.getUsername());
            int indexB = following.indexOf(b.getUsername());

            // If both are followed, the smaller index comes first
            if (indexA != -1 && indexB != -1) {
                return Integer.compare(indexA, indexB);
            }

            // Followed users come before non-followed users
            if (indexA != -1) return -1;
            if (indexB != -1) return 1;

            // Neither followed → alphabetical order
            return a.getUsername().compareToIgnoreCase(b.getUsername());
        });


        // optional: alphabetize non-followed users
        notFollowed.sort((a, b) ->
                a.getUsername().compareToIgnoreCase(b.getUsername())
        );

        // Sort followed EXACTLY by the order in currentUser.following
        followed.sort((a, b) -> {
            int indexA = currentUser.getFollowing().indexOf(a.getUsername());
            int indexB = currentUser.getFollowing().indexOf(b.getUsername());
            return Integer.compare(indexA, indexB);
        });

        // OPTIONAL: sort non-followed alphabetically
        notFollowed.sort((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()));

        visibleUsers.addAll(followed);
        visibleUsers.addAll(notFollowed);
    }

}
