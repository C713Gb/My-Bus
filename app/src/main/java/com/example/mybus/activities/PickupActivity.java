package com.example.mybus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mybus.R;
import com.example.mybus.adapters.PickupAdapter;
import com.example.mybus.models.Pickup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PickupActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    DatabaseReference reference;
    ImageButton back;
    ArrayList<Pickup> pickupArrayList;
    PickupAdapter pickupAdapter;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);

        recyclerView = findViewById(R.id.pickup_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.pickup_swipe);
        back = findViewById(R.id.back_3);
        update = findViewById(R.id.update_location);

        refreshLayout.setOnRefreshListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update status
                Toast.makeText(PickupActivity.this, pickupAdapter.checked, Toast.LENGTH_SHORT).show();
            }
        });

        updatePage();

    }

    private void updatePage() {

        try {

            reference = FirebaseDatabase.getInstance().getReference("Pickups");

            pickupArrayList = new ArrayList<>();
            pickupArrayList.clear();

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Pickup pickup = dataSnapshot.getValue(Pickup.class);
                        pickupArrayList.add(pickup);
                    }

                    pickupAdapter = new PickupAdapter(PickupActivity.this, pickupArrayList);
                    recyclerView.setAdapter(pickupAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        updatePage();
        refreshLayout.setRefreshing(false);
    }
}