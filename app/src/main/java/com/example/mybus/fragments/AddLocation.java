package com.example.mybus.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybus.R;
import com.example.mybus.activities.MainActivity;

public class AddLocation extends Fragment {

    public TextView location;
    Button add, change;
    MainActivity ma;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);
        location = view.findViewById(R.id.search_result_txt);
        add = view.findViewById(R.id.add_btn);
        change = view.findViewById(R.id.change_btn);
        ma = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ma.expandState();

        if (ma.placeName != "" && ma.placeName != null) {
            location.setText(ma.placeName);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), location.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.searchLocation();
            }
        });
    }
}