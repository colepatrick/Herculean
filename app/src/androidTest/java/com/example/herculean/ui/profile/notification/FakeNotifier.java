package com.example.herculean.ui.profile.notification;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

public class FakeNotifier implements Notifier {

    public static class Notification {
        public final int id;
        public final NotificationCompat.Builder builder;

        public Notification(int id, NotificationCompat.Builder builder) {
            this.id = id;
            this.builder = builder;
        }
    }

    private final List<Notification> notifications = new ArrayList<>();

    @Override
    public void notify(int id, NotificationCompat.Builder builder) {
        notifications.add(new Notification(id, builder));
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void clear() {
        notifications.clear();
    }
}
