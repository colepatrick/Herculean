package com.example.herculean.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;

import java.util.ArrayList;

public class FriendsRecycle extends RecyclerView.Adapter<FriendsRecycle.ViewHolder> {

    private ArrayList<UserAccount> users;
    private Runnable onFollowChanged;

    public FriendsRecycle(ArrayList<UserAccount> users, Runnable onFollowChanged) {
        this.users = users;
        this.onFollowChanged = onFollowChanged;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friends_recycle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserAccount user = users.get(position);
        UserAccount current = GlobalData.currentUser;

        holder.username.setText(user.getUsername());

        Glide.with(holder.itemView)
                .load(user.getProfileImageUri())
                .centerCrop()
                .placeholder(R.drawable.avatar_filler)
                .error(R.drawable.avatar_filler)
                .into(holder.profilePic);

        // Check if current user follows this user
        boolean isFollowing = current.getFollowing().contains(user.getUsername());
        holder.followButton.setText(isFollowing ? "Unfollow" : "Follow");

        holder.followButton.setOnClickListener(v -> {

            boolean nowFollowing = current.getFollowing().contains(user.getUsername());

            if (nowFollowing) {
                current.unfollowUser(user.getUsername());
                holder.followButton.setText("Follow");
            } else {
                current.followUser(user.getUsername());
                holder.followButton.setText("Unfollow");
            }

            if (onFollowChanged != null) {
                onFollowChanged.run(); // Tell fragment to reload list
            }
        });

        holder.itemView.setOnClickListener(v -> {
            GlobalData.viewedUser = user;
            Navigation.findNavController(v).navigate(R.id.nav_view_friend_profile);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView profilePic;
        Button followButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameText);
            profilePic = itemView.findViewById(R.id.profilePic);
            followButton = itemView.findViewById(R.id.followButton);
        }
    }
}
