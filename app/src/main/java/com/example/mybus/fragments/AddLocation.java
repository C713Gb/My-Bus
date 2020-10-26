package com.example.mybus.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybus.R;
import com.example.mybus.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddLocation extends Fragment {

    public TextView location;
    Button add, change;
    MainActivity ma;
    String lat = "", lng = "", placeName = "";
    FirebaseAuth auth;
    DatabaseReference ref;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);
        location = view.findViewById(R.id.search_result_txt);
        add = view.findViewById(R.id.add_btn);
        change = view.findViewById(R.id.change_btn);
        ma = (MainActivity) getActivity();
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ma.expandState();

        if (ma.placeName != "" && ma.placeName != null) {
            location.setText(ma.placeName);
            placeName = ma.placeName;
        }

        lat = ma.lat;
        lng = ma.lng;

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyDetails(placeName, lat, lng)) {
                    addToDatabase(placeName, lat, lng);
                }
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.searchLocation();
            }
        });
    }

    private boolean verifyDetails(String placeName, String lat, String lng) {
        if (placeName == null || placeName == "" || placeName.length()==0){
            Toast.makeText(ma, "Invalid Place Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lat == null || lat == "" || lat.length() == 0 || lng == null || lng == "" || lng.length() == 0){
            Toast.makeText(ma, "Invalid Location", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addToDatabase(String placeName, String lat, String lng) {
        pd.setMessage("Adding Location...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        try {

            ref = FirebaseDatabase.getInstance().getReference("Pickups");
            String id = ref.push().getKey();
            String status = "true";

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", id);
            hashMap.put("latitude", lat);
            hashMap.put("longitude", lng);
            hashMap.put("status", status);
            hashMap.put("ownerId", auth.getCurrentUser().getUid());
            hashMap.put("placeName", placeName);

            ref.child(id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    try {
                        if (task.isSuccessful()) {
                            try {
                                pd.dismiss();
                                Toast.makeText(ma, "Added Successfully", Toast.LENGTH_SHORT).show();
                                ma.mapboxMap.clear();
                                ma.onBackPressed();
                            } catch (Exception e) {
                                Log.d("ADD EXCEPTION1", e.getMessage());
                            }
                        } else {
                            pd.dismiss();
                            Toast.makeText(ma, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        Log.d("ADD EXCEPTION2", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.d("ADD EXCEPTION", e.getMessage());
        }

    }
}