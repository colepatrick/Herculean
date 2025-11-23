package com.example.herculean.ui.profile.notification;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

public class SystemNotifier implements Notifier {
    private final Context context;
    private final NotificationManagerCompat notificationManager;

    public SystemNotifier(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = NotificationManagerCompat.from(this.context);
    }

    @Override
    public void notify(int id, NotificationCompat.Builder builder) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(id, builder.build());
        }
    }
}
