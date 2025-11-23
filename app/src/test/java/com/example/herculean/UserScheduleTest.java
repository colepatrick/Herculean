package com.example.herculean;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.herculean.goals.UserSchedule;

import java.util.Calendar;

public class UserScheduleTest {

    private UserSchedule schedule;

    @Before
    public void setUp() {
        schedule = new UserSchedule("Legs", "Rest", "Arms", "Cardio", "Back", "Rest", "Yoga");
    }

    @Test
    public void testGetWorkoutForDay() {
        assertEquals("Legs", schedule.getWorkoutForDay(Calendar.MONDAY));
        assertEquals("Rest", schedule.getWorkoutForDay(Calendar.TUESDAY));
        assertEquals("Arms", schedule.getWorkoutForDay(Calendar.WEDNESDAY));
        assertEquals("Cardio", schedule.getWorkoutForDay(Calendar.THURSDAY));
        assertEquals("Back", schedule.getWorkoutForDay(Calendar.FRIDAY));
        assertEquals("Rest", schedule.getWorkoutForDay(Calendar.SATURDAY));
        assertEquals("Yoga", schedule.getWorkoutForDay(Calendar.SUNDAY));
    }

    @Test
    public void testToJSON_andFromJSON_areConsistent() throws Exception {
        JSONObject json = schedule.toJSON();
        UserSchedule recreated = UserSchedule.fromJSON(json);

        assertEquals(schedule.getMon(), recreated.getMon());
        assertEquals(schedule.getTue(), recreated.getTue());
        assertEquals(schedule.getWed(), recreated.getWed());
        assertEquals(schedule.getThur(), recreated.getThur());
        assertEquals(schedule.getFri(), recreated.getFri());
        assertEquals(schedule.getSat(), recreated.getSat());
        assertEquals(schedule.getSun(), recreated.getSun());
    }

    @Test
    public void testChangeSchedule_updatesAllDays() {
        schedule.changeSchedule("Push", "Pull", "Legs", "Core", "Cardio", "Rest", "Stretch");

        assertEquals("Push", schedule.getMon());
        assertEquals("Pull", schedule.getTue());
        assertEquals("Legs", schedule.getWed());
        assertEquals("Core", schedule.getThur());
        assertEquals("Cardio", schedule.getFri());
        assertEquals("Rest", schedule.getSat());
        assertEquals("Stretch", schedule.getSun());
    }
}
