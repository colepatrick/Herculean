package com.example.herculean.ui.settings.theme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.herculean.R;

public class ThemesSettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_themes_settings, container, false);

        Button lightBtn = view.findViewById(R.id.button_light_theme);
        Button darkBtn = view.findViewById(R.id.button_dark_theme);
        Button systemBtn = view.findViewById(R.id.button_system_theme);

        lightBtn.setOnClickListener(v ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        );

        darkBtn.setOnClickListener(v ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        );

        systemBtn.setOnClickListener(v ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        );

        return view;
    }
}
