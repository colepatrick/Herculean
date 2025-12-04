package com.example.herculean;

import com.example.herculean.datahandling.UserAccount;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserApproximationTest {

    private static final double DELTA = 1e-3; // For floating-point comparisons
    private static final double LBS_TO_KG = 0.453592;
    private static final double IN_TO_M = 0.0254;

    private UserAccount createUser(double heightInches, double weightLbs, int age, String gender) {
        UserAccount user = new UserAccount("testUser", "password", "email@test.com");
        user.setHeight(heightInches * IN_TO_M); // Convert to meters
        user.setWeight(weightLbs * LBS_TO_KG); // Convert to kg
        user.setAge(age);
        user.setGender(gender);
        return user;
    }

    @Test
    public void testBmiCalculation_validData() {
        // Height: 68.9 in (1.75m), Weight: 154.3 lbs (70kg)
        UserAccount user = createUser(68.9, 154.3, 30, "Male");
        assertEquals(22.857, user.calculateBmi(), DELTA);
    }

    @Test
    public void testBmrCalculation_validMale() {
        // Height: 68.9 in (1.75m), Weight: 154.3 lbs (70kg)
        UserAccount user = createUser(68.9, 154.3, 30, "Male");
        assertEquals(1685.247, user.calculateBmr(), DELTA);
    }

    @Test
    public void testBmrCalculation_validFemale() {
        // Height: 63.0 in (1.60m), Weight: 121.3 lbs (55kg)
        UserAccount user = createUser(63.0, 121.3, 25, "Female");
        assertEquals(1253.943, user.calculateBmr(), DELTA);
    }

    @Test
    public void testCalculations_zeroValues() {
        UserAccount user = createUser(0, 0, 0, null);
        assertEquals(0, user.calculateBmi(), DELTA);
        assertEquals(0, user.calculateBmr(), DELTA);
    }

    @Test
    public void testCalculations_incompleteData() {
        // Height: 68.9 in (1.75m), Weight: 154.3 lbs (70kg)
        UserAccount user = createUser(68.9, 154.3, 0, "Male"); // Age is zero
        assertEquals(0, user.calculateBmr(), DELTA);

        user.setAge(30); // Valid age
        user.setGender(null); // Null gender
        assertEquals(0, user.calculateBmr(), DELTA);
    }
}
