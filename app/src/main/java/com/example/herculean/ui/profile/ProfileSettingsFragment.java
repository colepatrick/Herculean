package com.example.herculean.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.herculean.GlobalData;
import com.example.herculean.R;
import com.example.herculean.databinding.FragmentProfileSettingsBinding;

public class ProfileSettingsFragment extends Fragment {

    private FragmentProfileSettingsBinding binding;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Uses the system photo picker to select an image
        // Must be initialized in onCreate before use
        pickMedia = registerForActivityResult(new PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PROFILE", "Selected URI: " + uri);

                GlobalData.currentUser.setProfileImageUri(uri.toString());
                GlobalData.saveAccounts(getContext());

                Glide.with(this)
                        .load(GlobalData.currentUser.getProfileImageUri())
                        .centerCrop()
                        .placeholder(R.drawable.avatar_filler)
                        .error(R.drawable.avatar_filler)
                        .into(binding.profileImageButton);
            } else {
                Log.d("PROFILE", "No media selected");
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileSettingsViewModel profileSettingsViewModel =
                new ViewModelProvider(this).get(ProfileSettingsViewModel.class);

        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false);

        binding.customizeBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_global_nav_profile);
        });

        binding.profileImageButton.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        binding.emailSwitch.setChecked(GlobalData.currentUser.isEmailDisplayed());
        binding.emailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                GlobalData.currentUser.emailDisplayed(isChecked);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}