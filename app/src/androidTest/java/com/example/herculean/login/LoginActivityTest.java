package com.example.herculean.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.herculean.R;
import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.JsonData;
import com.example.herculean.datahandling.UserAccount;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public MockWebServer server = new MockWebServer();

    private Context context;
    private ActivityScenario<LoginActivity> scenario;
    private UserAccount testUser = new UserAccount("testuser", "password", "test@email.com");
    private String testUserJson;
    private String originalBaseUrl;

    @Before
    public void setUp() throws IOException {
        context = ApplicationProvider.getApplicationContext();

        // Store the original BASE_URL and set up the mock server
        originalBaseUrl = GlobalData.BASE_URL;
        GlobalData.BASE_URL = server.url("/").toString();
        testUserJson = new Gson().toJson(testUser);

        // Clear SharedPreferences and global data
        SharedPreferences prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        GlobalData.jsonData = new JsonData();
        GlobalData.currentUser = null;
        GlobalData.clearLastLoggedInUser();
        GlobalData.jsonData.accounts = new ArrayList<>();

        // Add a test account to local data (for offline fallback tests)
        GlobalData.jsonData.accounts.add(testUser);
        GlobalData.saveAccounts(context);

        // Launch the activity
        scenario = ActivityScenario.launch(LoginActivity.class);
    }

    @After
    public void tearDown() throws IOException {
        if (scenario != null) {
            scenario.close();
        }
        server.shutdown();

        // Restore the original BASE_URL
        GlobalData.BASE_URL = originalBaseUrl;

        // Reset global data after test
        GlobalData.currentUser = null;
        GlobalData.clearLastLoggedInUser();
        GlobalData.jsonData = new JsonData();
        GlobalData.saveAccounts(context);
    }

    @Test
    public void testLoginButtonIsDisplayed() {
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterLinkIsDisplayed() {
        onView(withId(R.id.register_link)).check(matches(isDisplayed()));
    }

    @Test
    public void testUsernameNotFound() {
        // Mock a 404 response from the server
        server.enqueue(new MockResponse().setResponseCode(404));

        onView(withId(R.id.login_username_input)).perform(typeText("unknownuser"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Verify that the user is not logged in
        assert GlobalData.currentUser == null;
    }

    @Test
    public void testIncorrectPassword() {
        // Mock a successful response, but the password will be wrong
        server.enqueue(new MockResponse().setBody(testUserJson));

        onView(withId(R.id.login_username_input)).perform(typeText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input)).perform(typeText("wrongpassword"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Verify that the user is not logged in
        assert GlobalData.currentUser == null;
    }

    @Test
    public void testSuccessfulLogin() {
        // Mock a successful login response
        server.enqueue(new MockResponse().setBody(testUserJson));

        onView(withId(R.id.login_username_input)).perform(typeText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Wait for the activity to finish
        onView(withId(R.id.login_button)).check(doesNotExist());

        // Verify that the correct user is set
        assert GlobalData.currentUser != null;
        assert "testuser".equals(GlobalData.currentUser.getUsername());
    }

    @Test
    public void testRememberMeStoresUser() {
        // Mock a successful login response
        server.enqueue(new MockResponse().setBody(testUserJson));

        onView(withId(R.id.login_username_input)).perform(typeText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.remember_me_checkbox)).perform(click());
        onView(withId(R.id.login_button)).perform(click());

        // Wait for the activity to finish
        onView(withId(R.id.login_button)).check(doesNotExist());

        // Verify that the last logged-in user is stored
        assert "testuser".equals(GlobalData.getLastLoggedInUser());
    }
}
