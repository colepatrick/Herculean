package com.example.herculean;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class UserStreak implements Serializable {

    private int currentStreak;
    // Stores the date of the last successful calculation.
    private LocalDate lastUpdated;

    public UserStreak() {
        this.currentStreak = 0;
        this.lastUpdated = null;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void updateStreak(List<LocalDate> workoutDates, int requiredDaysPerWeek) {
        updateStreak(workoutDates, requiredDaysPerWeek, LocalDate.now());
    }

    /**
     * Calculates the user's streak. This method uses an optimization for recent updates
     * but falls back to a full recalculation if a missed week is detected.
     *
     * @param workoutDates A list of all workout dates.
     * @param requiredDaysPerWeek The number of unique days with workouts needed for a successful week.
     * @param today The reference date for the calculation.
     */
    public void updateStreak(List<LocalDate> workoutDates, int requiredDaysPerWeek, LocalDate today) {
        // No need to recalculate if the streak is already up-to-date.
        if (lastUpdated != null && !today.isAfter(lastUpdated)) {
            return;
        }

        // --- Optimized Path ---
        // If the last update was recent, try to do a quick incremental update.
        if (lastUpdated != null && lastUpdated.isAfter(today.minusWeeks(8))) { // Heuristic: 8 weeks
            int newlyCalculatedWeeks = 0;
            LocalDate weekToExamineStart = getStartOfWeek(today).minusWeeks(1);
            LocalDate lastUpdatedWeekStart = getStartOfWeek(lastUpdated).minusWeeks(1);

            // Check the weeks between now and the last update.
            while (weekToExamineStart.isAfter(lastUpdatedWeekStart)) {
                if (isWeekSuccessful(weekToExamineStart, workoutDates, requiredDaysPerWeek)) {
                    newlyCalculatedWeeks++;
                    weekToExamineStart = weekToExamineStart.minusWeeks(1);
                } else {
                    // A week was missed. The streak is broken. Fall back to a full recalculation for safety.
                    performFullRecalculation(workoutDates, requiredDaysPerWeek, today);
                    return;
                }
            }
            // If the loop completes, the streak was unbroken. Add the new weeks to the saved streak.
            this.currentStreak += newlyCalculatedWeeks;
            this.lastUpdated = today;
            return;
        }

        // --- Full Recalculation Path ---
        // Perform a full, safe recalculation if it's the first run or if the last update was long ago.
        performFullRecalculation(workoutDates, requiredDaysPerWeek, today);
    }

    /**
     * Performs a full, stateless recalculation of the entire streak and updates the object's state.
     */
    private void performFullRecalculation(List<LocalDate> workoutDates, int requiredDaysPerWeek, LocalDate today) {
        int calculatedStreak = 0;
        LocalDate weekToExamineStart = getStartOfWeek(today).minusWeeks(1);

        while (true) {
            if (isWeekSuccessful(weekToExamineStart, workoutDates, requiredDaysPerWeek)) {
                calculatedStreak++;
                weekToExamineStart = weekToExamineStart.minusWeeks(1);
            } else {
                break; // Streak is broken.
            }
            // Safety break to prevent infinite loops on very old data.
            if (calculatedStreak > 500) break; 
        }
        this.currentStreak = calculatedStreak;
        this.lastUpdated = today;
    }

    /**
     * Checks if a single week was successful by counting the distinct workout days within it.
     */
    private boolean isWeekSuccessful(LocalDate weekStart, List<LocalDate> allWorkoutDates, int requiredDays) {
        LocalDate weekEnd = weekStart.plusDays(6);
        long distinctDays = allWorkoutDates.stream()
                .filter(date -> !date.isBefore(weekStart) && !date.isAfter(weekEnd))
                .distinct()
                .count();
        return distinctDays >= requiredDays;
    }

    /** Returns the Monday of the week for the given date. */
    private LocalDate getStartOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
