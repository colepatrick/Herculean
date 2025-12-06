package com.example.herculean.workout;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Workout implements Serializable {

    public String exerciseName, bodyPart;
    public double duration;
    public double distance;
    public int sets;
    public int reps;

    public double weight;

    public LocalDate date;

    public Workout() {
        duration = 0;
        distance = 0;
        sets = 0;
        reps = 0;
        weight = 0;
        this.exerciseName = "";
        this.bodyPart = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now();
        }
    }

    public Workout(String exerciseName, String bodyPart) {
        super();
        this.exerciseName = exerciseName;
        this.bodyPart  = bodyPart;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now();
        }
    }

    // Getters for all possible fields.
    public String getBodyPart() { return this.bodyPart; }
    public LocalDate getDate() { return date; }
    public String getExerciseName() { return this.exerciseName; }
    public double getDuration() { return this.duration; }
    public double getDistance() { return this.distance; }
    public int getSets() { return this.sets; }
    public int getReps() { return this.reps; }
    public double getWeight() { return this.weight; }

    // Subclasses will override this.
    public double getScore() { return 0; }

    // Setters for all possible fields.
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }
    public void setDuration(double duration) { this.duration = duration; }
    public void setDistance(double distance) { this.distance = distance; }
    public void setSets(int sets) { this.sets = sets; }
    public void setReps(int reps) { this.reps = reps; }
    public void setWeight(double weight) { this.weight = weight; }

    public boolean isEmptyWorkout() {
        return sets == 0 && reps == 0 && weight == 0 && duration == 0 && distance == 0;
    }
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        String baseInfo = String.format("%s: %s", date, exerciseName);
        List<String> details = new ArrayList<>();

        // Use getters to ensure polymorphic behavior.
        if (getSets() > 0 && getReps() > 0) {
            details.add(String.format("%d sets x %d reps", getSets(), getReps()));
        }
        if (getWeight() > 0) {
            details.add(String.format("@ %.1f lbs", getWeight()));
        }
        if (getDuration() > 0 && getDistance() > 0) {
            details.add(String.format("%.1f miles in %.1f min", getDistance(), getDuration()));
        } else if (getDuration() > 0) {
            details.add(String.format("%.1f min", getDuration()));
        }

        if (!details.isEmpty()) {
            return baseInfo + " - " + TextUtils.join(" ", details);
        }
        return baseInfo;
    }
}
