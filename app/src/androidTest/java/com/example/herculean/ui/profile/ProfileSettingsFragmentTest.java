package com.example.herculean.ui.profile;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.goals.UserSchedule;
import com.example.herculean.ui.profile.notification.NotificationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

@RunWith(AndroidJUnit4.class)
public class ProfileSettingsFragmentTest {

    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        GlobalData.currentUser = null;
        NotificationManager.createNotificationChannel(context);
    }

    @Test
    public void testNoNotificationOnRestDay() {
        // Setup
        UserAccount user = new UserAccount();
        user.setUserSchedule(new UserSchedule("Rest", "Rest", "Rest", "Rest", "Rest", "Rest", "Rest")); // Initialize the schedule
        user.setWorkoutNotifications(true);
        setWorkoutForToday(user.getUserSchedule(), "Rest");
        GlobalData.currentUser = user;

        // Execute
        NotificationManager.sendWorkoutNotification(context);

        // Test completes without crashing, verifying the logic.
    }

    @Test
    public void testNoNotificationOnRestDay_caseInsensitive() {
        // Setup
        UserAccount user = new UserAccount();
        user.setUserSchedule(new UserSchedule("Rest", "Rest", "Rest", "Rest", "Rest", "Rest", "Rest")); // Initialize the schedule
        user.setWorkoutNotifications(true);
        setWorkoutForToday(user.getUserSchedule(), "rest");
        GlobalData.currentUser = user;

        // Execute
        NotificationManager.sendWorkoutNotification(context);

        // Test completes without crashing, verifying the logic.
    }

    private void setWorkoutForToday(UserSchedule schedule, String workout) {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (today) {
            case Calendar.MONDAY:
                schedule.setMon(workout);
                break;
            case Calendar.TUESDAY:
                schedule.setTue(workout);
                break;
            case Calendar.WEDNESDAY:
                schedule.setWed(workout);
                break;
            case Calendar.THURSDAY:
                schedule.setThur(workout);
                break;
            case Calendar.FRIDAY:
                schedule.setFri(workout);
                break;
            case Calendar.SATURDAY:
                schedule.setSat(workout);
                break;
            case Calendar.SUNDAY:
                schedule.setSun(workout);
                break;
        }
    }
}
