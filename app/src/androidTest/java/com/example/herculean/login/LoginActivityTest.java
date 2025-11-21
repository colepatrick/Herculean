package com.example.herculean.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.JsonData;
import com.example.herculean.datahandling.UserAccount;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private Context context;
    private ActivityScenario<LoginActivity> scenario;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Clear SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();

        // Reset global data
        GlobalData.jsonData = new JsonData();
        GlobalData.currentUser = null;
        GlobalData.clearLastLoggedInUser();
        GlobalData.jsonData.accounts = new ArrayList<>();

        // Add test account BEFORE launching activity
        GlobalData.jsonData.accounts.add(
                new UserAccount("testuser", "password", "test@email.com")
        );

        // Save accounts BEFORE launching
        GlobalData.saveAccounts(context);

        // Launch LoginActivity
        scenario = ActivityScenario.launch(LoginActivity.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
        GlobalData.currentUser = null;
        GlobalData.clearLastLoggedInUser();
        GlobalData.jsonData = new JsonData();
        GlobalData.saveAccounts(context);
    }

    // ---------------- BASIC UI TESTS ----------------

    @Test
    public void testLoginButtonVisible() {
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterLinkVisible() {
        onView(withId(R.id.register_link)).check(matches(isDisplayed()));
    }

    // ---------------- LOGIN LOGIC (NO TOASTS) ----------------

    @Test
    public void testUsernameNotFoundDoesNotSetCurrentUser() {
        onView(withId(R.id.login_username_input))
                .perform(typeText("wronguser"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        scenario.onActivity(a -> {
            // Current user should still be null
            assert GlobalData.currentUser == null;
        });
    }

    @Test
    public void testIncorrectPasswordDoesNotSetCurrentUser() {
        onView(withId(R.id.login_username_input))
                .perform(typeText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input))
                .perform(typeText("wrongpassword"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        scenario.onActivity(a -> {
            // Password is wrong → no login
            assert GlobalData.currentUser == null;
        });
    }

    @Test
    public void testSuccessfulLoginSetsCurrentUser() {
        onView(withId(R.id.login_username_input))
                .perform(typeText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        scenario.onActivity(a -> {
            assert GlobalData.currentUser != null;
            assert GlobalData.currentUser.getUsername().equals("testuser");
        });
    }

    @Test
    public void testRememberMeStoresLastLoggedInUser() {
        onView(withId(R.id.login_username_input))
                .perform(typeText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.remember_me_checkbox)).perform(click());
        onView(withId(R.id.login_button)).perform(click());

        scenario.onActivity(a -> {
            String last = GlobalData.getLastLoggedInUser();
            assert last != null;
            assert last.equals("testuser");
        });
    }
}
