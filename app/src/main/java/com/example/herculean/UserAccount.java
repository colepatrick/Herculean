package com.example.herculean;

import com.example.herculean.workout.Logger;

import java.io.Serializable;

public class UserAccount implements Serializable {
        public Logger workoutLog;

        public UserAccount() {
            workoutLog = new Logger();
        }

        public Logger getWorkoutLog() {
            return workoutLog;
        }

}