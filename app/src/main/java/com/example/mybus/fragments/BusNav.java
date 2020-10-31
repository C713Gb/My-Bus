package com.example.mybus.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mybus.R;
import com.example.mybus.activities.MainActivity;
import com.example.mybus.activities.PickupActivity;
import com.example.mybus.adapters.BusAdapter;
import com.example.mybus.adapters.PickupAdapter;

public class BusNav extends Fragment {

    RecyclerView recyclerView;
    MainActivity ma;
    BusAdapter busAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_nav, container, false);

        ma = (MainActivity) getActivity();
        recyclerView = view.findViewById(R.id.buses_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        busAdapter = new BusAdapter(getActivity(), ma.busArrayList);
        recyclerView.setAdapter(busAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new Handler().postDelayed(() -> {
            ma.expandState();
        }, 250);
    }
}