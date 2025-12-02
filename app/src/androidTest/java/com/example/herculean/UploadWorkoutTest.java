package com.example.herculean;

import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.workout.Upload;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class UploadWorkoutTest {

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            // Create a temporary user for the tests before any activity is launched
            UserAccount testUser = new UserAccount("testUser", "password", "test@email.com");
            GlobalData.currentUser = testUser;
        }
    };

    @Rule
    public ActivityScenarioRule<Upload> activityRule = new ActivityScenarioRule<>(Upload.class);

    @Test
    public void testUploadStrengthWorkout() {
        // 1. Select a Strength exercise
        onView(withId(R.id.selectExerciseButton)).perform(click());
        onView(withText("Bench Press")).perform(scrollTo(), click());

        // 2. Verify Strength layout is visible and enter details
        onView(withId(R.id.strengthLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextWeight)).perform(typeText("100"), closeSoftKeyboard());
        onView(withId(R.id.editTextSets)).perform(typeText("3"), closeSoftKeyboard());
        onView(withId(R.id.editTextReps)).perform(typeText("10"), closeSoftKeyboard());

        // 3. Upload the workout
        onView(withId(R.id.buttonUploadWorkout)).perform(click());

        // 4. Verify the workout is added to the list
        DataInteraction workoutItem = onData(anything()).inAdapterView(withId(R.id.workoutList)).atPosition(0);
        workoutItem.check(matches(isDisplayed()));
    }

    @Test
    public void testUploadBodyweightWorkout() {
        // 1. Select a Bodyweight exercise
        onView(withId(R.id.selectExerciseButton)).perform(click());
        onView(withText("Push-ups")).perform(scrollTo(), click());

        // 2. Verify Bodyweight layout is visible and enter details
        onView(withId(R.id.bodyweightLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextBodyweightSets)).perform(typeText("4"), closeSoftKeyboard());
        onView(withId(R.id.editTextBodyweightReps)).perform(typeText("20"), closeSoftKeyboard());

        // 3. Upload the workout
        onView(withId(R.id.buttonUploadWorkout)).perform(click());

        // 4. Verify the workout is added to the list
        DataInteraction workoutItem = onData(anything()).inAdapterView(withId(R.id.workoutList)).atPosition(0);
        workoutItem.check(matches(isDisplayed()));
    }

    @Test
    public void testUploadCardioWorkout() {
        // 1. Select a Cardio exercise
        onView(withId(R.id.selectExerciseButton)).perform(click());
        onView(withText("Running")).perform(scrollTo(), click());

        // 2. Verify Cardio layout is visible and enter details
        onView(withId(R.id.cardioLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextDuration)).perform(typeText("30"), closeSoftKeyboard());
        onView(withId(R.id.editTextDistance)).perform(typeText("3"), closeSoftKeyboard());

        // 3. Upload the workout
        onView(withId(R.id.buttonUploadWorkout)).perform(click());

        // 4. Verify the workout is added to the list
        DataInteraction workoutItem = onData(anything()).inAdapterView(withId(R.id.workoutList)).atPosition(0);
        workoutItem.check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyFieldsError() {
        // 1. Select an exercise but leave fields empty
        onView(withId(R.id.selectExerciseButton)).perform(click());
        onView(withText("Bench Press")).perform(scrollTo(), click());

        // 2. Attempt to upload
        onView(withId(R.id.buttonUploadWorkout)).perform(click());

        // 3. Verify the Strength layout is still visible (indicating an error)
        onView(withId(R.id.strengthLayout)).check(matches(isDisplayed()));
    }
}
