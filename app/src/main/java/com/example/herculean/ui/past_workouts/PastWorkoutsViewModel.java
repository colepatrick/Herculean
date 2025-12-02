package com.example.herculean.ui.past_workouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.herculean.datahandling.GlobalData;import com.example.herculean.workout.Workout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PastWorkoutsViewModel extends ViewModel {

    // holds workouts for the ui
    private final MutableLiveData<Map<LocalDate, List<Workout>>> displayedGroupedWorkouts;
    // keeps all workouts safe
    private List<Workout> allWorkoutsMasterList;

    public PastWorkoutsViewModel() {
        // What even is a MutableLiveData
        displayedGroupedWorkouts = new MutableLiveData<>();
        allWorkoutsMasterList = new ArrayList<>();
        // load workouts on start
        loadInitialWorkouts();
    }

    public LiveData<Map<LocalDate, List<Workout>>> getGroupedWorkouts() {
        return displayedGroupedWorkouts;
    }

    // gets all workouts from globaldata
    private void loadInitialWorkouts() {
        if (GlobalData.currentUser != null && GlobalData.currentUser.getWorkoutLog() != null) {
            allWorkoutsMasterList = GlobalData.currentUser.getWorkoutLog().getWorkouts();
        }
        // show all workouts first
        groupAndDisplayWorkouts(allWorkoutsMasterList);
    }

    // Filtering
    // Filtering
    public void filterAndGroupWorkouts(String query) {
        // if search is empty show all
        if (query == null || query.trim().isEmpty()) {
            groupAndDisplayWorkouts(allWorkoutsMasterList);
            return;
        }

        String lowerCaseQuery = query.toLowerCase();
        List<Workout> filteredList = new ArrayList<>();
        // format for date search
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

        // go thru master list
        for (Workout workout : allWorkoutsMasterList) {
            String formattedDate = workout.getDate().format(formatter).toLowerCase();

            // check if name, bodypart, or date matches
            if (workout.getExerciseName().toLowerCase().contains(lowerCaseQuery) ||
                    workout.getBodyPart().toLowerCase().contains(lowerCaseQuery) ||
                    formattedDate.contains(lowerCaseQuery)) {
                filteredList.add(workout);
            }
        }

        // update ui with filtered list
        groupAndDisplayWorkouts(filteredList);
    }

    // groups a list of workouts by date
    private void groupAndDisplayWorkouts(List<Workout> workoutsToGroup) {
        if (workoutsToGroup == null) {
            displayedGroupedWorkouts.setValue(new LinkedHashMap<>());
            return;
        }

        // group and sort workouts
        Map<LocalDate, List<Workout>> workoutsByDay = workoutsToGroup.stream()
                .collect(Collectors.groupingBy(Workout::getDate))
                .entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<Workout>>comparingByKey().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        // update the ui
        displayedGroupedWorkouts.setValue(workoutsByDay);
    }
}
