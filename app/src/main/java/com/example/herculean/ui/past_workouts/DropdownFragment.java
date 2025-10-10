package com.example.herculean;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DropdownFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout XML file
        View view = inflater.inflate(R.layout.fragment_dropdown, container, false);

        // Reference the Spinner
        Spinner spinner = view.findViewById(R.id.dropdown_spinner);

        // Example dropdown options
        String[] items = {"Option 1", "Option 2", "Option 3"};

        // Create an adapter to bind data to the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
        );

        spinner.setAdapter(adapter);

        return view;
    }
}
