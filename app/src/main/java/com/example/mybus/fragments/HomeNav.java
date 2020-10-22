package com.example.mybus.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mybus.R;
import com.example.mybus.activities.MainActivity;

public class HomeNav extends Fragment {

    ImageButton bus, pickup, menu;
    TextView busTxt, pickupTxt, search;
    RelativeLayout searchLayout;
    MainActivity ma;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_nav, container, false);

        bus = view.findViewById(R.id.bus_btn);
        busTxt = view.findViewById(R.id.bus_txt);
        searchLayout = view.findViewById(R.id.search_layout);
        menu = view.findViewById(R.id.open_side_nav);
        search = view.findViewById(R.id.search_btn);
        pickup = view.findViewById(R.id.pickup_btn);
        pickupTxt = view.findViewById(R.id.pickup_txt);
        ma = (MainActivity) getActivity();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Code

    }
}