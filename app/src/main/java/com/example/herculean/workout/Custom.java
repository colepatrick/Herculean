package com.example.herculean.workout;

import java.util.HashMap;
import java.util.Map;

public class Custom extends Workout {
    // If not known already, a hashMap is equivalent to a dict or unordered_map
    private Map<String, String> customFields = new HashMap<>();

    public Custom(String exerciseName, String bodyPart) {
        super(exerciseName, bodyPart, 0, 0, 0.0);
        this.customFields = new HashMap<>();
    }

    public void addCustomField(String key, String value) {
        customFields.put(key, value);
    }

    public String getCustomField(String key) {
        return customFields.getOrDefault(key, "N/A");
    }

    public Map<String, String> getAllCustomFields() {
        return customFields;
    }
}
