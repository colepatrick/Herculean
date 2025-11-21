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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        RecyclerView recycler = view.findViewById(R.id.allUsersRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadVisibleUsers();

        return view;
    }

    private void loadVisibleUsers() {
        visibleUsers.clear();

        String currentUsername = GlobalData.currentUser.getUsername();

        for (UserAccount account : GlobalData.accounts) {
            if (!account.getUsername().equalsIgnoreCase(currentUsername)) {
                visibleUsers.add(account);
            }
        }
    }
}
