package com.example.herculean.ui.past_workouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.workout.Workout;

import java.util.List;

//Im thinking something like login -> create workout -> past workouts (among other options)


public class PastWorkoutsViewModel extends ViewModel {

    private final MutableLiveData<List<Workout>> workouts;
    //What even is a MutableLiveData

    public PastWorkoutsViewModel() {
        workouts = new MutableLiveData<>();
        workouts.setValue(GlobalData.currentUser.workoutLog.getWorkouts());
    }

    public LiveData<List<Workout>> getWorkouts() { return workouts; }
}
