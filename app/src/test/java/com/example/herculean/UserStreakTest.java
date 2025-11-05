package com.example.herculean;

import org.junit.Before;
import org.junit.Test;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the UserStreak class.
 */
public class UserStreakTest {

    private UserStreak userStreak;
    private List<LocalDate> workoutDates;
    private final int REQUIRED_DAYS = 3;
    // Using a fixed date ensures tests are stable and not affected by the actual current date.
    private final LocalDate TODAY = LocalDate.of(2024, 5, 26); // A Sunday

    @Before
    public void setUp() {
        // A new UserStreak object is created before each test to prevent state leakage.
        userStreak = new UserStreak();
        workoutDates = new ArrayList<>();
    }

    @Test
    public void new_user_has_zero_streak() {
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);
        assertEquals("A new user should have a streak of 0.", 0, userStreak.getCurrentStreak());
    }

    @Test
    public void streak_is_one_after_one_successful_week() {
        // Add 3 distinct workout days for last week.
        addWorkoutsToWeek(1, 3);

        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);

        assertEquals("Streak should be 1 after one successful week.", 1, userStreak.getCurrentStreak());
    }

    @Test
    public void streak_is_two_after_two_consecutive_successful_weeks() {
        addWorkoutsToWeek(1, 3); // Last week
        addWorkoutsToWeek(2, 4); // Two weeks ago

        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);

        assertEquals("Streak should be 2 after two consecutive successful weeks.", 2, userStreak.getCurrentStreak());
    }

    @Test
    public void streak_resets_after_a_missed_week() {
        addWorkoutsToWeek(1, 3); // Last week: Success
        addWorkoutsToWeek(2, 2); // Week 2 ago was missed (only 2 workouts)
        addWorkoutsToWeek(3, 4); // Week 3 ago: Success

        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);

        // The streak should be 1, not 2, because the chain was broken two weeks ago.
        assertEquals("Streak should reset to 1 after a missed week.", 1, userStreak.getCurrentStreak());
    }

    @Test
    public void current_week_workouts_do_not_count_toward_streak() {
        // Add workouts for the current week.
        addWorkoutsToWeek(0, 4);

        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);

        assertEquals("Workouts in the current week should not affect the streak yet.", 0, userStreak.getCurrentStreak());
    }

    @Test
    public void multiple_workouts_on_same_day_count_as_one() {
        // Get the Monday of last week.
        LocalDate lastWeekStart = TODAY.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);

        // Add three workouts, but only on two distinct days.
        workoutDates.add(lastWeekStart);           // Monday
        workoutDates.add(lastWeekStart);           // Same Monday
        workoutDates.add(lastWeekStart.plusDays(1)); // Tuesday

        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);

        // Streak should be 0 because the required *3 distinct days* were not met.
        assertEquals("Streak should be 0 if unique workout days are less than required.", 0, userStreak.getCurrentStreak());
    }

    /**
     * Helper method to add a specified number of distinct workout days to a specific week.
     * This method ensures that the generated dates are all within the same calendar week.
     * @param weeksAgo The number of weeks before the TODAY constant to add workouts to (e.g., 1 is last week).
     * @param count The number of distinct workout days to add.
     */
    private void addWorkoutsToWeek(int weeksAgo, int count) {
        // Get the Monday of the target week.
        LocalDate weekStart = TODAY.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(weeksAgo);
        for (int i = 0; i < count; i++) {
            // Add `count` days from Monday onwards. This is safe because a week has 7 days,
            // and we won't add more than 7 workouts in these tests.
            workoutDates.add(weekStart.plusDays(i));
        }
    }
}
