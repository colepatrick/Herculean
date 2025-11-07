package com.example.herculean;

import org.junit.Before;
import org.junit.Test;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

import com.example.herculean.goals.UserStreak;

/**
 * Unit tests for the UserStreak class, including optimization logic.
 */
public class UserStreakTest {

    private UserStreak userStreak;
    private List<LocalDate> workoutDates;
    private final int REQUIRED_DAYS = 3;
    // Using a fixed date ensures tests are stable.
    private final LocalDate TODAY = LocalDate.of(2024, 5, 26); // A Sunday

    @Before
    public void setUp() {
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
        addWorkoutsToWeek(1, 3);
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);
        assertEquals("Streak should be 1 after one successful week.", 1, userStreak.getCurrentStreak());
    }

    @Test
    public void streak_is_two_after_two_consecutive_successful_weeks() {
        addWorkoutsToWeek(1, 3);
        addWorkoutsToWeek(2, 4);
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);
        assertEquals("Streak should be 2 after two consecutive successful weeks.", 2, userStreak.getCurrentStreak());
    }

    @Test
    public void streak_resets_after_a_missed_week() {
        addWorkoutsToWeek(1, 3); // Last week: Success
        addWorkoutsToWeek(2, 2); // Week 2 ago: Failure
        addWorkoutsToWeek(3, 4); // Week 3 ago: Success
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);
        assertEquals("Streak should reset to 1 after a missed week.", 1, userStreak.getCurrentStreak());
    }

    @Test
    public void multiple_workouts_on_same_day_count_as_one() {
        LocalDate lastWeekStart = getWeekStart(1);
        workoutDates.add(lastWeekStart);
        workoutDates.add(lastWeekStart);
        workoutDates.add(lastWeekStart.plusDays(1));
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);
        assertEquals("Streak should be 0 if unique workout days are less than required.", 0, userStreak.getCurrentStreak());
    }

    // === New Tests for the Optimization Logic ===

    @Test
    public void optimization_path_correctly_increments_streak() {
        // Step 1: Establish an initial streak of 1 by running the calculation one week ago.
        addWorkoutsToWeek(2, 4); // Successful week (2 weeks before TODAY)
        LocalDate firstUpdateDate = TODAY.minusWeeks(1);
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, firstUpdateDate);
        assertEquals("Pre-condition failed: Initial streak should be 1.", 1, userStreak.getCurrentStreak());

        // Step 2: Add another successful week in the time since the last update.
        addWorkoutsToWeek(1, 3); // Successful week (1 week before TODAY)

        // Step 3: Run the update again. This should use the fast, optimized path.
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);

        assertEquals("Streak should increment to 2 on the optimization path.", 2, userStreak.getCurrentStreak());
    }

    @Test
    public void optimization_path_falls_back_to_full_recalculation_on_missed_week() {
        // Step 1: Establish a long streak of 2.
        addWorkoutsToWeek(3, 5);
        addWorkoutsToWeek(4, 5);
        LocalDate firstUpdateDate = TODAY.minusWeeks(2);
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, firstUpdateDate);
        assertEquals("Pre-condition failed: Initial streak should be 2.", 2, userStreak.getCurrentStreak());

        // Step 2: Add a FAILED week and a successful week since the last update.
        addWorkoutsToWeek(2, 1); // This week was MISSED.
        addWorkoutsToWeek(1, 4); // This week was successful.

        // Step 3: Run update again. The optimization should detect the missed week and trigger a full recalculation.
        userStreak.updateStreak(workoutDates, REQUIRED_DAYS, TODAY);

        // The streak should be 1 (only for last week), not 3.
        assertEquals("Streak should reset to 1 when a week is missed in the optimization path.", 1, userStreak.getCurrentStreak());
    }

    private LocalDate getWeekStart(int weeksAgo) {
        return TODAY.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(weeksAgo);
    }

    private void addWorkoutsToWeek(int weeksAgo, int count) {
        LocalDate weekStart = getWeekStart(weeksAgo);
        for (int i = 0; i < count; i++) {
            workoutDates.add(weekStart.plusDays(i));
        }
    }
}
