package com.example.herculean.ui.pastworkouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.herculean.ui.past_workouts.WorkoutDummy;
import java.util.ArrayList;
import java.util.List;

//Im thinking something like login -> create workout -> past workouts (among other options)


public class PastWorkoutsViewModel extends ViewModel {

    private final MutableLiveData<List<WorkoutDummy>> workouts;
    //What even is a MutableLiveData

    public PastWorkoutsViewModel() {
        workouts = new MutableLiveData<>();
        //this next bit of code is just testing out my dummies
        //when it comes time to merge this should be changed to something
        //referncing the (global data??) or whatever has the user specefic
        // workout list
        List<WorkoutDummy> list = new ArrayList<>();
        list.add(new WorkoutDummy("Chest Day", "2025-10-01"));
        list.add(new WorkoutDummy("Legs & Core", "2025-10-03"));
        list.add(new WorkoutDummy("Arms Pump", "2025-10-06"));
        list.add(new WorkoutDummy("Cardio Blast", "2025-10-08"));
        list.add(new WorkoutDummy("Cardio Burst", "2025-10-09"));
        list.add(new WorkoutDummy("Left", "2025-10-21"));
        list.add(new WorkoutDummy("Right ", "2026-09-08"));
        list.add(new WorkoutDummy("Neck", "2032-12-08"));
        list.add(new WorkoutDummy("Brain", "2033-10-28"));
        list.add(new WorkoutDummy("Talking to women", "2034-01-23"));
        list.add(new WorkoutDummy("Getting a Job", "2043-32-44"));
        list.add(new WorkoutDummy("W*rk", "2054-21-53"));
        list.add(new WorkoutDummy("Bulgrian Split Squats", "2065-02-14"));
        list.add(new WorkoutDummy("Rear delts", "2076-06-30"));
        list.add(new WorkoutDummy("Bobby Shmurda", "2076-11-28"));
        workouts.setValue(list);

    }

    public LiveData<List<WorkoutDummy>> getWorkouts() { return workouts; }
}
