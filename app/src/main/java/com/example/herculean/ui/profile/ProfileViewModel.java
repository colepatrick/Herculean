package com.example.herculean.ui.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.herculean.GlobalData;
import com.example.herculean.workout.Workout;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<Workout> workouts;

    public ProfileViewModel() {
        workouts = new MutableLiveData<>();
        workouts.setValue(GlobalData.currentUser.getBestWorkout());
    }

    public LiveData<Workout> getText() {
        return workouts;
    }
}