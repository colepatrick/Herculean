package com.example.herculean.ui.profile;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.databinding.FragmentProfileSettingsBinding;
import com.example.herculean.goals.GoalAndScheduleActivity;
import com.example.herculean.login.LoginActivity;
import com.example.herculean.ui.profile.notification.NotificationReceiver;

import java.util.Calendar;

public class ProfileSettingsFragment extends Fragment {

    private static final int ALARM_REQUEST_CODE = 1001;
    private FragmentProfileSettingsBinding binding;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        GlobalData.currentUser.setProfileImageUri(uri.toString());
                        GlobalData.saveAccounts(getContext());
                        if (binding != null) {
                            Glide.with(this).load(uri).centerCrop().into(binding.profileImageButton);
                        }
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        showTimePickerDialog();
                    } else {
                        Toast.makeText(getContext(), "Notification Permission Denied", Toast.LENGTH_SHORT).show();
                        if (binding != null) {
                            binding.notificationSwitch.setChecked(false);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(ProfileSettingsViewModel.class);
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false);

        setupProfileImage();
        setupSwitches();
        setupButtons();

        return binding.getRoot();
    }

    private void setupSwitches() {
        binding.emailSwitch.setChecked(GlobalData.currentUser.isEmailDisplayed());
        binding.emailSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                GlobalData.currentUser.emailDisplayed(isChecked)
        );

        binding.notificationSwitch.setChecked(GlobalData.currentUser.areWorkoutNotificationsEnabled());
        updateNotificationTimeText();

        binding.notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            GlobalData.currentUser.setWorkoutNotifications(isChecked);
            updateNotificationTimeText();

            if (isChecked) {
                requestNotificationPermission();
            } else {
                cancelAlarm();
            }
        });

        binding.notificationTimeText.setOnClickListener(v -> {
            if (GlobalData.currentUser.areWorkoutNotificationsEnabled()) {
                showTimePickerDialog();
            }
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        // If permission is already granted or not required, proceed to show time picker
        showTimePickerDialog();
    }

    private void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

        String savedTime = GlobalData.currentUser.getNotificationTime();
        if (savedTime != null && !savedTime.isEmpty()) {
            String[] timeParts = savedTime.split(":");
            currentHour = Integer.parseInt(timeParts[0]);
            currentMinute = Integer.parseInt(timeParts[1]);
        }

        new TimePickerDialog(
                getContext(),
                (picker, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    GlobalData.currentUser.setNotificationTime(time);
                    GlobalData.saveAccounts(getContext());
                    updateNotificationTimeText();
                    scheduleAlarm(hourOfDay, minuteOfHour);
                },
                currentHour, currentMinute, false
        ).show();
    }

    private void scheduleAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(getContext(), "Permission to schedule exact alarms is required.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                requireContext().startActivity(intent);
                binding.notificationSwitch.setChecked(false);
                return;
            }
        }

        Intent intent = new Intent(requireContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(getContext(), "Reminder set for " + String.format("%02d:%02d", hour, minute), Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Toast.makeText(getContext(), "Reminder canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNotificationTimeText() {
        boolean isEnabled = GlobalData.currentUser.areWorkoutNotificationsEnabled();
        binding.notificationTimeText.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        String time = GlobalData.currentUser.getNotificationTime();
        if (isEnabled && (time == null || time.isEmpty())) {
            binding.notificationTimeText.setText("Select Time");
        } else {
            binding.notificationTimeText.setText(time);
        }
    }

    private void setupProfileImage() {
        Glide.with(this).load(GlobalData.currentUser.getProfileImageUri()).centerCrop().placeholder(R.drawable.avatar_filler).error(R.drawable.avatar_filler).into(binding.profileImageButton);
        binding.profileImageButton.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));
    }

    private void setupButtons() {
        binding.goalAndScheduleButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), GoalAndScheduleActivity.class)));
        binding.changeUsernameButton.setOnClickListener(v -> showChangeUsernameDialog());
        binding.changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        binding.logoutAccountButton.setOnClickListener(v -> showLogoutDialog());
        binding.deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.change_username);
        LinearLayout container = createInputContainer();
        final EditText input = createTextInput(container);
        builder.setView(container);
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            String newUserName = input.getText().toString().trim();
            if (!UserAccount.validUsername(newUserName)) { Toast.makeText(getContext(), "Invalid length", Toast.LENGTH_SHORT).show();
            } else if (newUserName.equals(GlobalData.currentUser.getUsername())) { Toast.makeText(getContext(), "Username cannot be old username", Toast.LENGTH_SHORT).show();
            } else if (GlobalData.usernameExists(newUserName)) { Toast.makeText(getContext(), "Username already exists", Toast.LENGTH_SHORT).show();
            } else {
                GlobalData.currentUser.setUsername(newUserName);
                GlobalData.saveAccounts(getContext());
                Toast.makeText(getContext(), "Username successfully changed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        builder.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.change_password);
        LinearLayout container = createInputContainer();
        final EditText input = createPasswordInput(container);
        builder.setView(container);
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            String newPassword = input.getText().toString().trim();
            if (!UserAccount.validPassword(newPassword)) { Toast.makeText(getContext(), "Invalid length", Toast.LENGTH_SHORT).show();
            } else if (newPassword.equals(GlobalData.currentUser.getPassword())) { Toast.makeText(getContext(), "Password cannot be old password", Toast.LENGTH_SHORT).show();
            } else {
                GlobalData.currentUser.setPassword(newPassword);
                GlobalData.saveAccounts(getContext());
                Toast.makeText(getContext(), "Password successfully changed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        builder.show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.logout_account).setPositiveButton(R.string.ok, (dialog, id) -> {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.confirm_logout).setPositiveButton(R.string.ok, (dialog2, id2) -> {
                GlobalData.currentUser = null;
                GlobalData.clearLastLoggedInUser();
                GlobalData.saveAccounts(getContext());
                startActivity(new Intent(getContext(), LoginActivity.class));
                requireActivity().finish();
            }).setNegativeButton(R.string.cancel, null).show();
        }).setNegativeButton(R.string.cancel, null).show();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.delete_account).setPositiveButton(R.string.ok, (dialog, id) -> {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.confirm_delete).setPositiveButton(R.string.ok, (dialog2, id2) -> {
                GlobalData.jsonData.accounts.remove(GlobalData.currentUser);
                GlobalData.currentUser = null;
                GlobalData.clearLastLoggedInUser();
                GlobalData.saveAccounts(getContext());
                startActivity(new Intent(getContext(), LoginActivity.class));
                requireActivity().finish();
            }).setNegativeButton(R.string.cancel, null).show();
        }).setNegativeButton(R.string.cancel, null).show();
    }

    private LinearLayout createInputContainer() {
        LinearLayout container = new LinearLayout(getActivity());
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(48, 0, 48, 0);
        container.setLayoutParams(lp);
        return container;
    }

    private EditText createTextInput(LinearLayout container) {
        EditText input = new EditText(getActivity());
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        container.addView(input);
        return input;
    }

    private EditText createPasswordInput(LinearLayout container) {
        EditText input = new EditText(getActivity());
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(input);
        return input;
    }
}
