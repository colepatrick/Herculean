package com.example.herculean.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.herculean.GlobalData;
import com.example.herculean.R;
import com.example.herculean.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.customizeButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_nav_profile_settings);
        });

        binding.profileUsername.setText(GlobalData.currentUser.getUsername());
        binding.profileUserEmail.setText(GlobalData.currentUser.getEmail());
        Glide.with(this)
                .load(GlobalData.currentUser.getProfileImageUri())
                .centerCrop()
                .placeholder(R.drawable.avatar_filler)
                .error(R.drawable.avatar_filler)
                .into(binding.profilePicture);


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}