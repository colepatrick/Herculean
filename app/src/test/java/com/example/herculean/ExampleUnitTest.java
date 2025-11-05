package com.example.herculean;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.herculean.workout.Logger;
import com.example.herculean.workout.Workout;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
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
        Workout best = new Workout("testWorkout1", "testBodyPart1", 5, 5, 100);

        testLog.addWorkout(best);
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 5, 4, 100)); // Less weight

        testAccount.setWorkoutLog(testLog);
        assertEquals(best, testAccount.getBestWorkout());
    }

    @Test
    public void getBestWorkoutOpposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        Workout best = new Workout("testWorkout1", "testBodyPart1", 5, 5, 100);

        testLog.addWorkout(best);
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 5, 4, 100));
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 1, 2, 100));
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 1, 9, 100));
        testLog.addWorkout(new Workout("testWorkout3", "testBodyPart3", 6, 3, 100));
        testLog.addWorkout(new Workout("testWorkout3", "testBodyPart3", 6, 3, 100));

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
        testLog.addWorkout(new Workout("testWorkout1", "testBodyPart1", 5, 5, 100));
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 5, 4, 100)); // Less weight

        testAccount.setWorkoutLog(testLog);
        assertEquals("testWorkout1", testAccount.getFavoriteWorkoutType());
    }

    @Test
    public void getFavoriteTypeOpposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Workout("testWorkout1", "testBodyPart1", 5, 5, 100));
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 5, 4, 100)); // Less weight
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 5, 2, 100)); // Now more weight

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
        testLog.addWorkout(new Workout("testWorkout1", "testBodyPart1", 5, 5, 100));
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 5, 4, 100)); // Less weight

        testAccount.setWorkoutLog(testLog);
        assertEquals("testBodyPart1", testAccount.getFavoriteMuscleGroup());
    }

    @Test
    public void getFavoriteMuscleOpposed() {
        UserAccount testAccount = new UserAccount();

        Logger testLog = new Logger();
        testLog.addWorkout(new Workout("testWorkout1", "testBodyPart1", 5, 5, 100));
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 5, 4, 100)); // Less weight
        testLog.addWorkout(new Workout("testWorkout2", "testBodyPart2", 5, 2, 100)); // Now more weight

        testAccount.setWorkoutLog(testLog);
        assertEquals("testBodyPart2", testAccount.getFavoriteMuscleGroup());
    }
}