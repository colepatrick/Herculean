package com.example.herculean;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.herculean.datahandling.UserAccount;
import com.example.herculean.workout.Bodyweight;
import com.example.herculean.workout.Cardio;
import com.example.herculean.workout.Logger;
import com.example.herculean.workout.Strength;
import com.example.herculean.workout.Workout;

public class UserProfileTest {
    @Test
    public void getBestWorkoutEmpty() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();

        testAccount.setWorkoutLog(testLog);
        assertEquals(null, testAccount.getBestWorkout());
    }

    @Test
    public void getBestWorkoutUnopposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        Workout best = new Strength("testWorkout1", "testBodyPart1", 5, 5, 100);

        testLog.addWorkout(best);
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 5, 4, 100));

        testAccount.setWorkoutLog(testLog);
        assertEquals(best, testAccount.getBestWorkout());
    }

    @Test
    public void getBestWorkoutOpposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        Workout best = new Strength("testWorkout1", "testBodyPart1", 5, 5, 100);

        testLog.addWorkout(best);
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 5, 4, 100));
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 1, 2, 100));
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 1, 9, 100));
        testLog.addWorkout(new Strength("testWorkout3", "testBodyPart3", 6, 3, 100));
        testLog.addWorkout(new Strength("testWorkout3", "testBodyPart3", 6, 3, 100));

        testAccount.setWorkoutLog(testLog);
        assertEquals(best, testAccount.getBestWorkout());
    }

    @Test
    public void getFavoriteTypeEmpty() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();

        testAccount.setWorkoutLog(testLog);
        assertEquals("None", testAccount.getFavoriteWorkoutType());
    }

    @Test
    public void getFavoriteTypeUnopposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Strength("testWorkout1", "testBodyPart1", 5, 5, 100));
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 5, 4, 100));

        testAccount.setWorkoutLog(testLog);
        assertEquals("testWorkout1", testAccount.getFavoriteWorkoutType());
    }

    @Test
    public void getFavoriteTypeOpposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Strength("testWorkout1", "testBodyPart1", 5, 5, 100));
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 5, 4, 100));
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 5, 2, 100));

        testAccount.setWorkoutLog(testLog);
        assertEquals("testWorkout2", testAccount.getFavoriteWorkoutType());
    }

    @Test
    public void getFavoriteMuscleEmpty() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();

        testAccount.setWorkoutLog(testLog);
        assertEquals("None", testAccount.getFavoriteMuscleGroup());
    }


    @Test
    public void getFavoriteMuscleUnopposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Strength("testWorkout1", "testBodyPart1", 5, 5, 100));
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 5, 4, 100));

        testAccount.setWorkoutLog(testLog);
        assertEquals("testBodyPart1", testAccount.getFavoriteMuscleGroup());
    }

    @Test
    public void getFavoriteMuscleOpposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Strength("testWorkout1", "testBodyPart1", 5, 5, 100));
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 5, 4, 100));
        testLog.addWorkout(new Strength("testWorkout2", "testBodyPart2", 5, 2, 100));

        testAccount.setWorkoutLog(testLog);
        assertEquals("testBodyPart2", testAccount.getFavoriteMuscleGroup());
    }

    // Cardio Tests
    @Test
    public void getBestWorkoutCardio() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        Workout best = new Cardio("Running", "Legs", 30, 3);

        testLog.addWorkout(best);
        testLog.addWorkout(new Cardio("Cycling", "Legs", 60, 5));

        testAccount.setWorkoutLog(testLog);
        assertEquals(best, testAccount.getBestWorkout());
    }

    @Test
    public void getFavoriteWorkoutTypeCardio() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Cardio("Running", "Legs", 30, 3));
        testLog.addWorkout(new Cardio("Cycling", "Legs", 60, 5));

        testAccount.setWorkoutLog(testLog);
        assertEquals("Running", testAccount.getFavoriteWorkoutType());
    }

    @Test
    public void getFavoriteMuscleGroupCardio() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Cardio("Running", "Legs", 30, 3));
        testLog.addWorkout(new Cardio("Swimming", "Full Body", 60, 1));

        testAccount.setWorkoutLog(testLog);
        assertEquals("Legs", testAccount.getFavoriteMuscleGroup());
    }

    // Bodyweight Tests
    @Test
    public void getBestWorkoutBodyweight() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        Workout best = new Bodyweight("Pushups", "Chest", 3, 20);

        testLog.addWorkout(best);
        testLog.addWorkout(new Bodyweight("Pullups", "Back", 5, 5));

        testAccount.setWorkoutLog(testLog);
        assertEquals(best, testAccount.getBestWorkout());
    }

    @Test
    public void getFavoriteWorkoutTypeBodyweight() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Bodyweight("Pushups", "Chest", 3, 20));
        testLog.addWorkout(new Bodyweight("Situps", "Abs", 5, 25));

        testAccount.setWorkoutLog(testLog);
        assertEquals("Situps", testAccount.getFavoriteWorkoutType());
    }

    @Test
    public void getFavoriteMuscleGroupBodyweight() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Bodyweight("Pushups", "Chest", 3, 20));
        testLog.addWorkout(new Bodyweight("Squats", "Legs", 5, 25));

        testAccount.setWorkoutLog(testLog);
        assertEquals("Legs", testAccount.getFavoriteMuscleGroup());
    }
}
