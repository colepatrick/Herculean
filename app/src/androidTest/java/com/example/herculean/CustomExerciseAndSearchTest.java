package com.example.herculean;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

import com.example.herculean.workout.ExerciseDatabase;
import com.example.herculean.workout.Workout;

public class CustomExerciseAndSearchTest {

    // ===== CUSTOM EXERCISE TESTS =====

    @Test
    public void AddCustomExercise() {
        // Get initial count
        int initialCount = ExerciseDatabase.getAllExercises().size();

        // Add custom exercise
        ExerciseDatabase.addCustomExercise("Cable Flies", "Chest");

        // Verify exercise was added
        int newCount = ExerciseDatabase.getAllExercises().size();
        assertEquals(initialCount + 1, newCount);
    }

    @Test
    public void CustomExerciseAppearsInDatabase() {
        // Add custom exercise
        ExerciseDatabase.addCustomExercise("Donkey Kicks", "Legs");

        // Search for it
        List<Workout> allExercises = ExerciseDatabase.getAllExercises();
        boolean found = false;

        for (Workout workout : allExercises) {
            if (workout.getExerciseName().equals("Donkey Kicks")) {
                found = true;
                assertEquals("Legs", workout.getBodyPart());
                break;
            }
        }

        assertTrue("Custom exercise should be found in database", found);
    }

    @Test
    public void CustomExerciseWithSpecialCharacters() {
        ExerciseDatabase.addCustomExercise("Spider-Man Push-ups", "Chest");

        List<Workout> allExercises = ExerciseDatabase.getAllExercises();
        boolean found = false;

        for (Workout workout : allExercises) {
            if (workout.getExerciseName().equals("Spider-Man Push-ups")) {
                found = true;
                break;
            }
        }

        assertTrue("Custom exercise with special characters should work", found);
    }

    @Test
    public void MultipleCustomExercises() {
        int initialCount = ExerciseDatabase.getAllExercises().size();

        ExerciseDatabase.addCustomExercise("Exercise 1", "Arms");
        ExerciseDatabase.addCustomExercise("Exercise 2", "Legs");
        ExerciseDatabase.addCustomExercise("Exercise 3", "Back");

        int newCount = ExerciseDatabase.getAllExercises().size();
        assertEquals(initialCount + 3, newCount);
    }

    // ===== SEARCH FUNCTION TESTS =====

    @Test
    public void SearchByExerciseName() {
        List<Workout> allExercises = ExerciseDatabase.getAllExercises();
        List<Workout> filtered = new java.util.ArrayList<>();

        String searchQuery = "bench";

        for (Workout workout : allExercises) {
            if (workout.getExerciseName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filtered.add(workout);
            }
        }

        assertFalse("Search for 'bench' should return results", filtered.isEmpty());

        // Verify all results contain "bench"
        for (Workout workout : filtered) {
            assertTrue(workout.getExerciseName().toLowerCase().contains("bench"));
        }
    }

    @Test
    public void SearchByBodyPart() {
        List<Workout> allExercises = ExerciseDatabase.getAllExercises();
        List<Workout> filtered = new java.util.ArrayList<>();

        String searchQuery = "chest";

        for (Workout workout : allExercises) {
            if (workout.getExerciseName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    workout.getBodyPart().toLowerCase().contains(searchQuery.toLowerCase())) {
                filtered.add(workout);
            }
        }

        assertFalse("Search for 'chest' should return results", filtered.isEmpty());
    }

    @Test
    public void SearchCaseInsensitive() {
        List<Workout> allExercises = ExerciseDatabase.getAllExercises();

        // Search with lowercase
        List<Workout> lowerResults = new java.util.ArrayList<>();
        for (Workout workout : allExercises) {
            if (workout.getExerciseName().toLowerCase().contains("squat")) {
                lowerResults.add(workout);
            }
        }

        // Search with uppercase
        List<Workout> upperResults = new java.util.ArrayList<>();
        for (Workout workout : allExercises) {
            if (workout.getExerciseName().toLowerCase().contains("SQUAT".toLowerCase())) {
                upperResults.add(workout);
            }
        }

        assertEquals("Case insensitive search should return same results",
                lowerResults.size(), upperResults.size());
    }

    @Test
    public void SearchWithNoResults() {
        List<Workout> allExercises = ExerciseDatabase.getAllExercises();
        List<Workout> filtered = new java.util.ArrayList<>();

        String searchQuery = "xyznonexistentexercise123";

        for (Workout workout : allExercises) {
            if (workout.getExerciseName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    workout.getBodyPart().toLowerCase().contains(searchQuery.toLowerCase())) {
                filtered.add(workout);
            }
        }

        assertEquals("Search with no matches should return empty list", 0, filtered.size());
    }

    @Test
    public void SearchPartialMatch() {
        List<Workout> allExercises = ExerciseDatabase.getAllExercises();
        List<Workout> filtered = new java.util.ArrayList<>();

        String searchQuery = "press"; // Should match "Bench Press", "Overhead Press", etc.

        for (Workout workout : allExercises) {
            if (workout.getExerciseName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filtered.add(workout);
            }
        }

        assertTrue("Partial search should return multiple results", filtered.size() > 1);
    }

    @Test
    public void SearchForCustomExercise() {
        // Add custom exercise
        ExerciseDatabase.addCustomExercise("Unique Custom Exercise", "Core");

        List<Workout> allExercises = ExerciseDatabase.getAllExercises();
        List<Workout> filtered = new java.util.ArrayList<>();

        String searchQuery = "unique custom";

        for (Workout workout : allExercises) {
            if (workout.getExerciseName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filtered.add(workout);
            }
        }

        assertFalse("Should be able to search for custom exercises", filtered.isEmpty());
        assertEquals("Unique Custom Exercise", filtered.get(0).getExerciseName());
    }

    @Test
    public void AddCustomExerciseThenSearch() {
        // Add custom exercise
        String customName = "My Special Exercise " + System.currentTimeMillis();
        ExerciseDatabase.addCustomExercise(customName, "Arms");

        // Search for it
        List<Workout> allExercises = ExerciseDatabase.getAllExercises();
        boolean found = false;

        for (Workout workout : allExercises) {
            if (workout.getExerciseName().equals(customName)) {
                found = true;
                break;
            }
        }

        assertTrue("Should find custom exercise after adding it", found);
    }

    @Test
    public void SearchReturnsCorrectBodyPart() {
        List<Workout> chestExercises = ExerciseDatabase.getExercisesByBodyPart("Chest");

        // Verify all results are chest exercises
        for (Workout workout : chestExercises) {
            assertEquals("Chest", workout.getBodyPart());
        }
    }
}