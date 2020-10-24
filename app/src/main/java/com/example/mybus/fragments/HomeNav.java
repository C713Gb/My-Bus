package com.example.mybus.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mybus.R;
import com.example.mybus.activities.MainActivity;
import com.example.mybus.activities.PickupActivity;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;

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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.searchLocation();
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.searchLocation();
            }
        });

        pickupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPickup();
            }
        });

        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPickup();
            }
        });

    }

    private void goToPickup() {
        Intent intent = new Intent(getActivity(), PickupActivity.class);
        startActivity(intent);
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {

        ma.currentFrame = "search2";
        ma.collapseState();

        Handler h2 = new Handler();
        h2.postDelayed(() -> {
            FragmentManager manager2 = getActivity().getSupportFragmentManager();
            FragmentTransaction ft2 = manager2.beginTransaction();
            ft2.addToBackStack(null);
            ft2.replace(R.id.fragment_container_search, fragment, tag);
            ft2.commitAllowingStateLoss();
        }, 250);

    }
}