package com.example.herculean.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.herculean.GlobalData;
import com.example.herculean.GoalAndScheduleActivity;
import com.example.herculean.LoginActivity;
import com.example.herculean.R;
import com.example.herculean.UserAccount;
import com.example.herculean.databinding.FragmentProfileSettingsBinding;
import com.example.herculean.RegisterAccount;

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

        Glide.with(this)
                .load(GlobalData.currentUser.getProfileImageUri())
                .centerCrop()
                .placeholder(R.drawable.avatar_filler)
                .error(R.drawable.avatar_filler)
                .into(binding.profileImageButton);

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

        binding.goalAndScheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GoalAndScheduleActivity.class);
            startActivity(intent);
        });

        binding.changeUsernameButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.change_username);

            LinearLayout container2 = new LinearLayout(getActivity());
            container2.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(48, 0, 48, 0);

            final EditText input = new EditText(getActivity());
            input.setLayoutParams(lp);
            input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);

            container2.addView(input);
            builder.setView(container2);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String newUserName = input.getText().toString().trim();

                    if(!UserAccount.validUsername(newUserName)) {
                        Toast.makeText(getContext(), "Invalid length", Toast.LENGTH_SHORT).show();
                    } else if(newUserName.equals(GlobalData.currentUser.getUsername())) {
                        Toast.makeText(getContext(), "Username cannot be old username", Toast.LENGTH_SHORT).show();
                    } else if(GlobalData.usernameExists(newUserName)) {
                        Toast.makeText(getContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        GlobalData.currentUser.setUsername(newUserName);
                        GlobalData.saveAccounts(getContext());

                        Toast.makeText(getContext(), "Username successfully changed", Toast.LENGTH_SHORT).show();
                        Log.d("PROFILE", "New username: " + newUserName);
                        dialog.dismiss();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        binding.changePasswordButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.change_password);

            LinearLayout container2 = new LinearLayout(getActivity());
            container2.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(48, 0, 48, 0);

            final EditText input = new EditText(getActivity());
            input.setLayoutParams(lp);
            input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

            container2.addView(input);
            builder.setView(container2);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String newPassword = input.getText().toString().trim();

                    if(!UserAccount.validPassword(newPassword)) {
                        Toast.makeText(getContext(), "Invalid length", Toast.LENGTH_SHORT).show();
                    } else if(newPassword.equals(GlobalData.currentUser.getPassword())) {
                        Toast.makeText(getContext(), "Password cannot be old password", Toast.LENGTH_SHORT).show();
                    } else {
                        GlobalData.currentUser.setPassword(newPassword);
                        GlobalData.saveAccounts(getContext());

                        Toast.makeText(getContext(), "Password successfully changed", Toast.LENGTH_SHORT).show();
                        Log.d("PROFILE", "New password set for " + GlobalData.currentUser.getUsername());
                        dialog.dismiss();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        binding.logoutAccountButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.logout_account);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                // Confirmation Window
                public void onClick(DialogInterface dialog, int id) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setTitle(R.string.confirm_logout);

                    // Second Confirmation Window
                    builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog2, int id) {
                            Toast.makeText(getContext(), "Account successfully logged out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            dialog2.dismiss();
                            dialog.dismiss();
                        }
                    });
                    builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog2, int id) {
                            dialog2.cancel();
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        binding.deleteAccountButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.delete_account);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                // Confirmation Window
                public void onClick(DialogInterface dialog, int id) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setTitle(R.string.confirm_delete);

                    // Second Confirmation Window
                    builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog2, int id) {
                            GlobalData.accounts.remove(GlobalData.currentUser);
                            GlobalData.saveAccounts(getContext());
                            Toast.makeText(getContext(), "Account successfully deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            dialog2.dismiss();
                            dialog.dismiss();
                        }
                    });
                    builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog2, int id) {
                            dialog2.cancel();
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}