package com.example.herculean.ui.profile.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.MainActivity;
import com.example.herculean.datahandling.UserAccount;

import java.util.Calendar;
import java.util.Locale;

/**
 * A helper class for creating and sending workout reminder notifications.
 * This class contains static methods and is not meant to be instantiated in the final app.
 */
public class NotificationManager {

    private static final String CHANNEL_ID = "workout_notification_channel";
    private static final int NOTIFICATION_ID = 101;

    /**
     * Creates the notification channel required for Android 8.0+.
     * This should be called once when the application starts.
     */
    public static void createNotificationChannel(Context context) {
        // This check is important to avoid running this on older Android versions.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Workout Reminders";
            String description = "Channel for daily workout reminders";
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system.
            android.app.NotificationManager systemNotificationManager = context.getSystemService(android.app.NotificationManager.class);
            if (systemNotificationManager != null) {
                systemNotificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Builds and sends a personalized workout notification.
     * This method checks the user's schedule and only sends a notification if it's not a rest day.
     */
    public static void sendWorkoutNotification(Context context) {
        // Guard clause: Do nothing if no user is logged in.
        UserAccount currentUser = GlobalData.currentUser;
        if (currentUser == null || !currentUser.areWorkoutNotificationsEnabled()) {
            return;
        }

        // 1. Get today's workout from the user's schedule.
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String workout = currentUser.getUserSchedule().getWorkoutForDay(day);

        // 2. Check for empty or rest days (case-insensitive).
        if (workout == null || workout.trim().isEmpty() || workout.toLowerCase(Locale.ROOT).equals("rest")) {
            return; // It's a rest day or empty, so do not send a notification.
        }

        // 3. Create an intent to launch the app when the notification is tapped.
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 4. Build the personalized notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // A reliable system icon.
                .setContentTitle("Herculean Workout Reminder")
                .setContentText("Remember to " + workout) // The personalized message.
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // The notification disappears after being tapped.

        // 5. Check for permission before sending.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, we cannot post.
            // The user must grant this from the UI (ProfileSettingsFragment).
            return;
        }

        // 6. Send the notification.
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }
}
