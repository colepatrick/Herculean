package com.example.herculean.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.herculean.datahandling.GlobalData;
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