package com.example.herculean.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public FriendsRecycle(ArrayList<UserAccount> users) {
        this.users = users;
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
        holder.username.setText(user.getUsername());

        Glide.with(holder.itemView)
                .load(user.getProfileImageUri())
                .centerCrop()
                .placeholder(R.drawable.avatar_filler)
                .error(R.drawable.avatar_filler)
                .into(holder.profilePic);

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameText);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }
}
