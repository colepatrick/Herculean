package com.example.herculean.ui.profile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.ui.profile.notification.NotificationReceiver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileSettingsFragmentTest {

    // This MUST match the request code in ProfileSettingsFragment.java
    private static final int ALARM_REQUEST_CODE = 1001;

    private Context context;
    private FragmentScenario<ProfileSettingsFragment> fragmentScenario;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        GlobalData.currentUser = new UserAccount("testuser", "password", "test@email.com");

        // --- DEFINITIVE FIX for NoActivityResumedException ---
        // The simple `launch` method is the most robust way to start the fragment.
        // It creates a dedicated activity for the fragment and brings it to the RESUMED state,
        // ensuring the UI is ready before any Espresso actions are performed.
        // This avoids issues with container views and complex lifecycles.
        fragmentScenario = FragmentScenario.launch(ProfileSettingsFragment.class);
    }

    @After
    public void tearDown() {
        if (fragmentScenario != null) {
            fragmentScenario.close();
        }
        GlobalData.currentUser = null;
    }

    @Test
    public void testNotificationSwitch_enablesAndDisablesTimePicker() {
        // Turn ON notifications and check that the time text is displayed
        onView(withId(R.id.notification_switch)).perform(click());
        onView(withId(R.id.notification_time_text)).check(matches(isDisplayed()));

        // Turn OFF notifications and check that the time text is hidden
        onView(withId(R.id.notification_switch)).perform(click());
        onView(withId(R.id.notification_time_text)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testNotificationSwitch_schedulesAndCancelsAlarm() {
        // Pre-condition: Ensure no alarm is scheduled before the test runs
        assertFalse("Alarm should not be scheduled initially", isAlarmScheduled());

        // Action 1: Enable notifications and set a time
        onView(withId(R.id.notification_switch)).perform(click());
        onView(withId(R.id.notification_time_text)).perform(click());
        onView(withText("OK")).inRoot(isDialog()).perform(click());

        // VERIFY 1: The alarm should now be scheduled
        assertTrue("Alarm should be scheduled after enabling", isAlarmScheduled());

        // Action 2: Disable notifications
        onView(withId(R.id.notification_switch)).perform(click());

        // VERIFY 2: The alarm should now be canceled
        assertFalse("Alarm should be canceled after disabling", isAlarmScheduled());
    }

    /**
     * Helper method to check for the PendingIntent's existence.
     */
    private boolean isAlarmScheduled() {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE, // CRITICAL: Use the correct request code
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );
        return pendingIntent != null;
    }
}
