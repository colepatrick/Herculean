package com.example.herculean;

import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.workout.Logger;
import com.example.herculean.workout.Workout;
import com.jjoe64.graphview.series.DataPoint;

import static org.junit.Assert.*;
import org.junit.Test;

import java.time.LocalDate;

public class UserProfileGraphTest {
    // Helper function
    private Workout createWorkout(LocalDate date, int score) {
        Workout workout = new Workout("Test Exercise", "Test Muscle");
        workout.setDate(date);
        workout.setSets(1);
        workout.setReps(score);
        workout.setWeight(1.0);

        return workout;
    }

    @Test
    public void getDayDataPoints_noWorkouts_returnsAllZeroScores() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        userAccount.setWorkoutLog(new Logger());
        int days = 7;

        // Execute
        DataPoint[] points = userAccount.getDayDataPoints(days);

        // Assert
        assertEquals(days, points.length);
        for (DataPoint point : points) {
            assertEquals(0, (int) point.getY());
        }
    }

    @Test
    public void getDayDataPoints_singleWorkoutToday_returnsCorrectScore() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        workoutLog.addWorkout(createWorkout(LocalDate.now(), 100)); // Workout today
        userAccount.setWorkoutLog(workoutLog);
        int days = 7;

        // Execute
        DataPoint[] points = userAccount.getDayDataPoints(days);

        // Assert: The last point (index days-1) corresponds to today
        assertEquals(100, (int) points[days - 1].getY());
        assertEquals(0, (int) points[0].getY());
    }

    @Test
    public void getDayDataPoints_multipleWorkoutsOnSameDay_returnsSummedScore() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        workoutLog.addWorkout(createWorkout(twoDaysAgo, 50));
        workoutLog.addWorkout(createWorkout(twoDaysAgo, 75));
        userAccount.setWorkoutLog(workoutLog);
        int days = 14;

        // Execute
        DataPoint[] points = userAccount.getDayDataPoints(days);

        // Assert: The point at index (days - 1 - 2) corresponds to 2 days ago
        assertEquals(125, (int) points[days - 3].getY());
        assertEquals(0, (int) points[days - 1].getY()); // Today should be 0
    }

    @Test
    public void getDayDataPoints_workoutsOnMultipleDays_returnsCorrectScores() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        workoutLog.addWorkout(createWorkout(LocalDate.now(), 100));            // Today
        workoutLog.addWorkout(createWorkout(LocalDate.now().minusDays(1), 80)); // Yesterday
        workoutLog.addWorkout(createWorkout(LocalDate.now().minusDays(4), 120)); // 4 days ago
        userAccount.setWorkoutLog(workoutLog);
        int days = 5;

        // Execute
        DataPoint[] points = userAccount.getDayDataPoints(days);

        // Assert: Expected scores: [120, 0, 0, 80, 100]
        assertEquals(120, (int) points[0].getY()); // Day index 0 (4 days ago)
        assertEquals(0,   (int) points[1].getY()); // Day index 1 (3 days ago)
        assertEquals(0,   (int) points[2].getY()); // Day index 2 (2 days ago)
        assertEquals(80,  (int) points[3].getY()); // Day index 3 (yesterday)
        assertEquals(100, (int) points[4].getY()); // Day index 4 (today)
    }

    @Test
    public void getDayDataPoints_workoutOutsideRange_isIgnored() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        workoutLog.addWorkout(createWorkout(LocalDate.now().minusDays(8), 500)); // 8 days ago
        userAccount.setWorkoutLog(workoutLog);
        int days = 7;

        // Execute
        DataPoint[] points = userAccount.getDayDataPoints(days);

        // Assert: All points within the last 7 days should have a score of 0
        for (DataPoint point : points) {
            assertEquals(0, (int) point.getY());
        }
    }


// --- Tests for getMonthDataPoints ---

    @Test
    public void getMonthDataPoints_noWorkouts_returnsAllZeroScores() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        userAccount.setWorkoutLog(new Logger());
        int months = 12;

        // Execute
        DataPoint[] points = userAccount.getMonthDataPoints(months);

        // Assert
        assertEquals(months, points.length);
        for (DataPoint point : points) {
            assertEquals(0, (int) point.getY());
        }
    }

    @Test
    public void getMonthDataPoints_singleWorkoutThisMonth_returnsCorrectScore() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        workoutLog.addWorkout(createWorkout(LocalDate.now().withDayOfMonth(5), 200));
        userAccount.setWorkoutLog(workoutLog);
        int months = 6;

        // Execute
        DataPoint[] points = userAccount.getMonthDataPoints(months);

        // Assert: The last point (index months-1) corresponds to the current month
        assertEquals(200, (int) points[months - 1].getY());
        assertEquals(0, (int) points[0].getY());
    }

    @Test
    public void getMonthDataPoints_multipleWorkoutsSameMonth_returnsSummedScore() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        workoutLog.addWorkout(createWorkout(lastMonth.withDayOfMonth(10), 150));
        workoutLog.addWorkout(createWorkout(lastMonth.withDayOfMonth(20), 250));
        userAccount.setWorkoutLog(workoutLog);
        int months = 4;

        // Execute
        DataPoint[] points = userAccount.getMonthDataPoints(months);

        // Assert: The point at index (months - 1 - 1) corresponds to last month
        assertEquals(400, (int) points[months - 2].getY());
        assertEquals(0, (int) points[months - 1].getY()); // This month should be 0
    }

    @Test
    public void getMonthDataPoints_workoutsOnMultipleMonths_returnsCorrectScores() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        workoutLog.addWorkout(createWorkout(LocalDate.now(), 100)); // This month
        workoutLog.addWorkout(createWorkout(LocalDate.now().minusMonths(2), 300)); // 2 months ago
        userAccount.setWorkoutLog(workoutLog);
        int months = 4;

        // Execute
        DataPoint[] points = userAccount.getMonthDataPoints(months);

        // Assert: Expected scores: [0, 300, 0, 100]
        assertEquals(0,   (int) points[0].getY()); // Month index 0 (3 months ago)
        assertEquals(300, (int) points[1].getY()); // Month index 1 (2 months ago)
        assertEquals(0,   (int) points[2].getY()); // Month index 2 (1 month ago)
        assertEquals(100, (int) points[3].getY()); // Month index 3 (this month)
    }

    @Test
    public void getMonthDataPoints_workoutInPreviousYear_isIgnoredIfOutOfRange() {
        // Setup inside the test
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        workoutLog.addWorkout(createWorkout(LocalDate.now().minusYears(1), 1000));
        userAccount.setWorkoutLog(workoutLog);
        int months = 6;

        // Execute
        DataPoint[] points = userAccount.getMonthDataPoints(months);

        // Assert: All scores should be 0 as the workout is outside the 6-month range
        for (DataPoint point : points) {
            assertEquals(0, (int) point.getY());
        }
    }
}
