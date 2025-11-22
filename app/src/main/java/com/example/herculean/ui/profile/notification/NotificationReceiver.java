package com.example.herculean.ui.profile.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * A simple receiver that triggers the workout notification.
 * This class's only responsibility is to receive a broadcast from the system
 * (sent by an AlarmManager) and delegate the work of building and sending
 * the notification to the NotificationManager class.
 */
public class NotificationReceiver extends BroadcastReceiver {

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // The NotificationManager contains all the logic for building and sending
        // the notification. We just need to call it.
        NotificationManager.sendWorkoutNotification(context);
    }
}