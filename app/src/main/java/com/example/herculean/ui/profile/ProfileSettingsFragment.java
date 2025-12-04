package com.example.herculean.ui.profile;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.database.AccountRepository;
import com.example.herculean.databinding.FragmentProfileSettingsBinding;
import com.example.herculean.goals.GoalAndScheduleActivity;
import com.example.herculean.login.LoginActivity;
import com.example.herculean.ui.profile.notification.NotificationReceiver;

import java.text.DecimalFormat;
import java.util.Calendar;

public class ProfileSettingsFragment extends Fragment {

    private static final int ALARM_REQUEST_CODE = 1001;
    private FragmentProfileSettingsBinding binding;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private static final double LBS_TO_KG = 0.453592;
    private static final double IN_TO_M = 0.0254;
    private AccountRepository repo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repo = new AccountRepository(GlobalData.BASE_URL);

        // Image picker
        pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        GlobalData.currentUser.setProfileImageUri(uri.toString());
                        GlobalData.saveAccounts(getContext());
                        if (binding != null) {
                            Glide.with(this)
                                    .load(uri)
                                    .centerCrop()
                                    .into(binding.profileImageButton);
                        }
                    }
                }
        );

        // Notification permission
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false);

        setupProfileImage();
        setupSwitches();
        setupButtons();
        setupInputFields();

        return binding.getRoot();
    }

    private void setupInputFields() {
        // Gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.genderSpinner.setAdapter(adapter);

        DecimalFormat df = new DecimalFormat("#.##");

        // Load existing data
        UserAccount currentUser = GlobalData.currentUser;
        if (currentUser.getHeight() > 0) {
            binding.heightInput.setText(df.format(currentUser.getHeight() / IN_TO_M));
        }
        if (currentUser.getWeight() > 0) {
            binding.weightInput.setText(df.format(currentUser.getWeight() / LBS_TO_KG));
        }
        if (currentUser.getAge() > 0) {
            binding.ageInput.setText(String.valueOf(currentUser.getAge()));
        }
        if (currentUser.getGender() != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (currentUser.getGender().equalsIgnoreCase(adapter.getItem(i).toString())) {
                    binding.genderSpinner.setSelection(i);
                    break;
                }
            }
        } else {
            binding.genderSpinner.setSelection(0); // Default to "Select Gender"
        }
    }

    // ──────────────────── Switches ────────────────────

    private void setupSwitches() {
        // Email visibility
        binding.emailSwitch.setChecked(GlobalData.currentUser.isEmailDisplayed());
        binding.emailSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                GlobalData.currentUser.emailDisplayed(isChecked)
        );

        // Workout notifications
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

    // ──────────────────── Notifications ────────────────────

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        // Already granted or not required
        showTimePickerDialog();
    }

    private void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

        String savedTime = GlobalData.currentUser.getNotificationTime();
        if (savedTime != null && !savedTime.isEmpty()) {
            String[] timeParts = savedTime.split(":");
            try {
                currentHour = Integer.parseInt(timeParts[0]);
                currentMinute = Integer.parseInt(timeParts[1]);
            } catch (NumberFormatException ignored) {
            }
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
                currentHour,
                currentMinute,
                false
        ).show();
    }

    private void scheduleAlarm(int hour, int minute) {
        AlarmManager alarmManager =
                (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                        getContext(),
                        "Permission to schedule exact alarms is required.",
                        Toast.LENGTH_LONG
                ).show();

                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                requireContext().startActivity(intent);

                binding.notificationSwitch.setChecked(false);
                return;
            }
        }

        Intent intent = new Intent(requireContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(
                getContext(),
                "Reminder set for " + String.format("%02d:%02d", hour, minute),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void cancelAlarm() {
        AlarmManager alarmManager =
                (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

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

    // ──────────────────── Profile Image ────────────────────

    private void setupProfileImage() {
        Glide.with(this)
                .load(GlobalData.currentUser.getProfileImageUri())
                .centerCrop()
                .placeholder(R.drawable.avatar_filler)
                .error(R.drawable.avatar_filler)
                .into(binding.profileImageButton);

        binding.profileImageButton.setOnClickListener(v ->
                pickMedia.launch(
                        new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                .build()
                )
        );
    }

    // ──────────────────── Buttons ────────────────────

    private void setupButtons() {
        binding.goalAndScheduleButton.setOnClickListener(
                v -> startActivity(new Intent(getActivity(), GoalAndScheduleActivity.class))
        );

        binding.saveButton.setOnClickListener(v -> {
            try {
                UserAccount currentUser = GlobalData.currentUser;

                if (binding.genderSpinner.getSelectedItemPosition() > 0) {
                    String heightText = binding.heightInput.getText().toString();
                    if (!TextUtils.isEmpty(heightText)) {
                        currentUser.setHeight(Double.parseDouble(heightText) * IN_TO_M);
                    }

                    String weightText = binding.weightInput.getText().toString();
                    if (!TextUtils.isEmpty(weightText)) {
                        currentUser.setWeight(Double.parseDouble(weightText) * LBS_TO_KG);
                    }

                    String ageText = binding.ageInput.getText().toString();
                    if (!TextUtils.isEmpty(ageText)) {
                        currentUser.setAge(Integer.parseInt(ageText));
                    }

                    currentUser.setGender(binding.genderSpinner.getSelectedItem().toString());
                } else {
                    // If "Select Gender" is chosen, reset stats
                    currentUser.setHeight(0);
                    currentUser.setWeight(0);
                    currentUser.setAge(0);
                    currentUser.setGender(null);
                }

                repo.updateAccount(currentUser.getUsername(), currentUser, new AccountRepository.ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        GlobalData.saveAccounts(getContext());
                        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(getContext(), "Error saving to backend", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });

        binding.changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        binding.logoutAccountButton.setOnClickListener(v -> showLogoutDialog());
        binding.deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ──────────────────── Dialogs ────────────────────

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.change_password);

        LinearLayout container = createInputContainer();
        final EditText input = createPasswordInput(container);
        builder.setView(container);

        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            String newPassword = input.getText().toString().trim();

            if (!UserAccount.validPassword(newPassword)) {
                Toast.makeText(getContext(), "Invalid length", Toast.LENGTH_SHORT).show();
            } else if (newPassword.equals(GlobalData.currentUser.getPassword())) {
                Toast.makeText(getContext(),
                        "Password cannot be old password",
                        Toast.LENGTH_SHORT).show();
            } else {
                GlobalData.currentUser.setPassword(newPassword);
                GlobalData.saveAccounts(getContext());
                Toast.makeText(getContext(),
                        "Password successfully changed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        builder.show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.logout_account)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.confirm_logout)
                            .setPositiveButton(R.string.ok, (dialog2, id2) -> {
                                GlobalData.currentUser = null;
                                GlobalData.clearLastLoggedInUser();
                                GlobalData.saveAccounts(getContext());
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                requireActivity().finish();
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_account)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.confirm_delete)
                            .setPositiveButton(R.string.ok, (dialog2, id2) -> {
                                GlobalData.jsonData.accounts.remove(GlobalData.currentUser);
                                GlobalData.currentUser = null;
                                GlobalData.clearLastLoggedInUser();
                                GlobalData.saveAccounts(getContext());
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                requireActivity().finish();
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    // ──────────────────── UI Helpers ────────────────────

    private LinearLayout createInputContainer() {
        LinearLayout container = new LinearLayout(getActivity());
        container.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(48, 0, 48, 0);
        container.setLayoutParams(lp);

        return container;
    }

    private EditText createPasswordInput(LinearLayout container) {
        EditText input = new EditText(getActivity());
        input.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        container.addView(input);
        return input;
    }
}
