package com.example.herculean.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class FriendsRecycle extends RecyclerView.Adapter<FriendsRecycle.FriendsViewHolder> {

    public interface OnUserClick {
        void onClick(UserAccount user);
    }

    private List<UserAccount> users;
    private final OnUserClick onClick;

    public FriendsRecycle(List<UserAccount> users, OnUserClick onClick) {
        this.users = users;
        this.onClick = onClick;
    }

    public void updateData(List<UserAccount> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friends_recycle, parent, false);
        return new FriendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        UserAccount user = users.get(position);

        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());

        // avatar
        Glide.with(holder.itemView.getContext())
                .load(user.getProfileImageUri())
                .placeholder(R.drawable.avatar_filler)
                .into(holder.avatar);

        // FOLLOW BUTTON
        boolean isFollowing = GlobalData.currentUser.getFollowing().contains(user.getUsername());
        holder.followButton.setText(isFollowing ? "Following" : "Follow");

        holder.followButton.setOnClickListener(v -> {

            if (isFollowing) {
                GlobalData.currentUser.unfollowUser(user.getUsername());
            } else {
                GlobalData.currentUser.followUser(user.getUsername());
            }

            // Save to server
            GlobalData.saveCurrentUserToServer();

            notifyItemChanged(position);
        });

        holder.itemView.setOnClickListener(v -> onClick.onClick(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class FriendsViewHolder extends RecyclerView.ViewHolder {
        TextView username, email;
        ImageView avatar;
        Button followButton;

        FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.friendUsername);
            email = itemView.findViewById(R.id.friendEmail);
            avatar = itemView.findViewById(R.id.friendAvatar);
            followButton = itemView.findViewById(R.id.followBtn);
        }
    }
}
