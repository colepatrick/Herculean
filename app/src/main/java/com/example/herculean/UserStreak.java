package com.example.herculean;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class UserStreak implements Serializable {

    private int currentStreak;

    public UserStreak() {
        this.currentStreak = 0;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void updateStreak(List<LocalDate> workoutDates, int requiredWorkoutsPerWeek) {
        updateStreak(workoutDates, requiredWorkoutsPerWeek, LocalDate.now());
    }

    /**
     * Calculates the user's current streak by counting consecutive successful weeks backward from today.
     * This method is stateless and recalculates the entire streak on each call.
     *
     * @param workoutDates A list of all workout dates.
     * @param requiredWorkoutsPerWeek The number of workouts needed to consider a week successful.
     * @param today The reference date for the calculation (allows for testing).
     */
    public void updateStreak(List<LocalDate> workoutDates, int requiredWorkoutsPerWeek, LocalDate today) {
        int calculatedStreak = 0;
        // A streak is based on *completed* weeks, so we start checking from the week before the current one.
        LocalDate weekToExamineStart = getStartOfWeek(today).minusWeeks(1);

        while (true) {
            final LocalDate weekStart = weekToExamineStart;
            final LocalDate weekEnd = weekStart.plusDays(6);

            long workoutsInWeek = workoutDates.stream()
                    .filter(date -> !date.isBefore(weekStart) && !date.isAfter(weekEnd))
                    .count();

            if (workoutsInWeek >= requiredWorkoutsPerWeek) {
                calculatedStreak++;
                // Move to the previous week to see if the streak continues.
                weekToExamineStart = weekToExamineStart.minusWeeks(1);
            } else {
                // The first week with insufficient workouts breaks the chain, so we stop counting.
                break;
            }
        }
        this.currentStreak = calculatedStreak;
    }

    private LocalDate getStartOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
