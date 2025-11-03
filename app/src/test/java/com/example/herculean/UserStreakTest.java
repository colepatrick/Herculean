package com.example.herculean;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the UserStreak class.
 */
public class UserStreakTest {

    private UserStreak userStreak;
    private List<LocalDate> workoutDates;
    private final int REQUIRED_WORKOUTS = 3;

    @Before
    public void setUp() {
        userStreak = new UserStreak();
        workoutDates = new ArrayList<>();
    }

    @Test
    public void new_user_streak_is_initially_zero() {
        assertEquals(0, userStreak.getCurrentStreak());
    }

    @Test
    public void streak_increases_after_completing_one_week() {
        // Workouts from last week
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        workoutDates.add(lastWeek.plusDays(1));
        workoutDates.add(lastWeek.plusDays(2));
        workoutDates.add(lastWeek.plusDays(3));

        userStreak.updateStreak(workoutDates, REQUIRED_WORKOUTS, LocalDate.now());

        assertEquals(1, userStreak.getCurrentStreak());
    }

    @Test
    public void streak_resets_after_missed_week() {
        // Week 1: Success (3 weeks ago)
        LocalDate threeWeeksAgo = LocalDate.now().minusWeeks(3);
        workoutDates.add(threeWeeksAgo.plusDays(1));
        workoutDates.add(threeWeeksAgo.plusDays(2));
        workoutDates.add(threeWeeksAgo.plusDays(3));

        // Week 2: Failure (2 weeks ago) - no workouts

        // Week 3: Success (last week)
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        workoutDates.add(lastWeek.plusDays(1));
        workoutDates.add(lastWeek.plusDays(2));
        workoutDates.add(lastWeek.plusDays(3));

        userStreak.updateStreak(workoutDates, REQUIRED_WORKOUTS, LocalDate.now());

        // Streak should be 1, because the chain was broken 2 weeks ago.
        assertEquals(1, userStreak.getCurrentStreak());
    }

    @Test
    public void streak_handles_multiple_consecutive_weeks() {
        // Week 1 (2 weeks ago)
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);
        workoutDates.add(twoWeeksAgo.plusDays(1));
        workoutDates.add(twoWeeksAgo.plusDays(2));
        workoutDates.add(twoWeeksAgo.plusDays(3));

        // Week 2 (last week)
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        workoutDates.add(lastWeek.plusDays(1));
        workoutDates.add(lastWeek.plusDays(2));
        workoutDates.add(lastWeek.plusDays(3));

        userStreak.updateStreak(workoutDates, REQUIRED_WORKOUTS, LocalDate.now());

        assertEquals(2, userStreak.getCurrentStreak());
    }

    @Test
    public void updateStreak_is_idempotent_within_same_week() {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        workoutDates.add(lastWeek.plusDays(1));
        workoutDates.add(lastWeek.plusDays(2));
        workoutDates.add(lastWeek.plusDays(3));

        userStreak.updateStreak(workoutDates, REQUIRED_WORKOUTS, LocalDate.now());
        userStreak.updateStreak(workoutDates, REQUIRED_WORKOUTS, LocalDate.now());

        assertEquals(1, userStreak.getCurrentStreak());
    }

    @Test
    public void current_week_workouts_do_not_affect_streak_yet() {
        LocalDate today = LocalDate.now();
        workoutDates.add(today);
        workoutDates.add(today.minusDays(1));
        workoutDates.add(today.minusDays(2));

        userStreak.updateStreak(workoutDates, REQUIRED_WORKOUTS, LocalDate.now());

        assertEquals(0, userStreak.getCurrentStreak());
    }
}
