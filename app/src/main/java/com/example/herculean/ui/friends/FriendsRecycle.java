package com.example.herculean.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herculean.R;
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
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameText);
        }
    }
}
