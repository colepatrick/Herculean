package com.example.herculean;

import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.workout.Bodyweight;
import com.example.herculean.workout.Cardio;
import com.example.herculean.workout.Logger;
import com.example.herculean.workout.Workout;
import com.example.herculean.workout.Strength;
import com.jjoe64.graphview.series.DataPoint;

import static org.junit.Assert.*;
import org.junit.Test;

import java.time.LocalDate;

public class UserProfileGraphTest {
    // Helper functions
    private Workout createStrengthWorkout(LocalDate date, int score) {
        Strength workout = new Strength("Test Strength", "Test Muscle", 1, 1, score);
        workout.setDate(date);
        return workout;
    }

    private Workout createBodyweightWorkout(LocalDate date, int sets, int reps) {
        Bodyweight workout = new Bodyweight("Test Bodyweight", "Test Muscle", sets, reps);
        workout.setDate(date);
        return workout;
    }

    private Workout createCardioWorkout(LocalDate date, double duration, double distance) {
        Cardio workout = new Cardio("Test Cardio", "Test Muscle", duration, distance);
        workout.setDate(date);
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
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now(), 100));
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
        workoutLog.addWorkout(createStrengthWorkout(twoDaysAgo, 50));
        workoutLog.addWorkout(createStrengthWorkout(twoDaysAgo, 75));
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
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now(), 100));
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now().minusDays(1), 80));
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now().minusDays(4), 120));
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
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now().minusDays(8), 500));
        userAccount.setWorkoutLog(workoutLog);
        int days = 7;

        // Execute
        DataPoint[] points = userAccount.getDayDataPoints(days);

        // Assert: All points within the last 7 days should have a score of 0
        for (DataPoint point : points) {
            assertEquals(0, (int) point.getY());
        }
    }

    @Test
    public void getDayDataPoints_withMixedWorkouts_returnsSummedScores() {
        // Setup
        UserAccount userAccount = new UserAccount();
        Logger workoutLog = new Logger();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Add a mix of workouts for today
        workoutLog.addWorkout(createStrengthWorkout(today, 100)); // Score: 100
        workoutLog.addWorkout(createBodyweightWorkout(today, 5, 10)); // Score: 50
        // Total for today: 150

        // Add a mix for yesterday
        workoutLog.addWorkout(createCardioWorkout(yesterday, 30, 3)); // Score: (3/30)*100 = 10
        workoutLog.addWorkout(createStrengthWorkout(yesterday, 40)); // Score: 40
        // Total for yesterday: 50

        userAccount.setWorkoutLog(workoutLog);
        int days = 2;

        // Execute
        DataPoint[] points = userAccount.getDayDataPoints(days);

        // Assert
        assertEquals(50, (int) points[0].getY()); // Yesterday
        assertEquals(150, (int) points[1].getY()); // Today
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
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now().withDayOfMonth(5), 200));
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
        workoutLog.addWorkout(createStrengthWorkout(lastMonth.withDayOfMonth(10), 150));
        workoutLog.addWorkout(createStrengthWorkout(lastMonth.withDayOfMonth(20), 250));
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
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now(), 100)); // This month
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now().minusMonths(2), 300)); // 2 months ago
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
        workoutLog.addWorkout(createStrengthWorkout(LocalDate.now().minusYears(1), 1000));
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