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
        // Height: 68.9 in, Weight: 154.3 lbs
        double heightIn = 68.9;
        double weightLbs = 154.3;
        UserAccount user = createUser(heightIn, weightLbs, 30, "Male");

        // Calculate expected value directly in the test to avoid floating point errors
        double expectedBmi = (weightLbs * LBS_TO_KG) / Math.pow(heightIn * IN_TO_M, 2);
        assertEquals(expectedBmi, user.calculateBmi(), DELTA);
    }

    @Test
    public void testBmrCalculation_validMale() {
        // Height: 68.9 in, Weight: 154.3 lbs, Age: 30
        double heightIn = 68.9;
        double weightLbs = 154.3;
        int age = 30;
        UserAccount user = createUser(heightIn, weightLbs, age, "Male");

        // Calculate expected value directly in the test to avoid floating point errors
        double weightKg = weightLbs * LBS_TO_KG;
        double heightCm = (heightIn * IN_TO_M) * 100;
        double expectedBmr = 88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age);
        assertEquals(expectedBmr, user.calculateBmr(), DELTA);
    }

    @Test
    public void testBmrCalculation_validFemale() {
        // Height: 63.0 in, Weight: 121.3 lbs, Age: 25
        double heightIn = 63.0;
        double weightLbs = 121.3;
        int age = 25;
        UserAccount user = createUser(heightIn, weightLbs, age, "Female");

        // Calculate expected value directly in the test to avoid floating point errors
        double weightKg = weightLbs * LBS_TO_KG;
        double heightCm = (heightIn * IN_TO_M) * 100;
        double expectedBmr = 447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age);
        assertEquals(expectedBmr, user.calculateBmr(), DELTA);
    }

    @Test
    public void testCalculations_zeroValues() {
        UserAccount user = createUser(0, 0, 0, null);
        assertEquals(0, user.calculateBmi(), DELTA);
        assertEquals(0, user.calculateBmr(), DELTA);
    }

    @Test
    public void testCalculations_incompleteData() {
        // Height: 68.9 in, Weight: 154.3 lbs
        UserAccount user = createUser(68.9, 154.3, 0, "Male"); // Age is zero
        assertEquals(0, user.calculateBmr(), DELTA);

        user.setAge(30); // Valid age
        user.setGender(null); // Null gender
        assertEquals(0, user.calculateBmr(), DELTA);
    }
}
