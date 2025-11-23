package com.example.herculean.ui.profile.notification;

import androidx.core.app.NotificationCompat;

public interface Notifier {
    void notify(int id, NotificationCompat.Builder builder);
}
