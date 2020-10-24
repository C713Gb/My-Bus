package com.example.mybus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mybus.R;
import com.example.mybus.adapters.PickupAdapter;
import com.example.mybus.models.Pickup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PickupActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    DatabaseReference reference;
    ImageButton back;
    ArrayList<Pickup> pickupArrayList;
    PickupAdapter pickupAdapter;
    Button update;
    public HashMap<String,String> pickupMap;
    ProgressDialog pd;

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

        pd = new ProgressDialog(PickupActivity.this);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update status
                pd.setMessage("Updating...");
                pd.show();

                reference = FirebaseDatabase.getInstance().getReference("Pickups");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            final int[] k = {-1};

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Pickup pickup = snapshot.getValue(Pickup.class);
                                String place = pickup.getPlaceName();
                                String id = pickup.getId();
                                String updatedStatus = pickupMap.get(place);

                                reference.child(id).child("status").setValue(updatedStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        if (task.isSuccessful()){
                                            // Updated
                                            k[0] = 1;
                                        } else {
                                            // Update Failed
                                            Toast.makeText(PickupActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                            Log.d("UPDATE", task.getException().getMessage());
                                            k[0] = 0;
                                        }
                                    }
                                });

                                if (k[0] == 0) {
                                    break;
                                }
                            }
                        } catch (Exception e){
                            pd.dismiss();
                            Toast.makeText(PickupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        updatePage();

    }

    private void updatePage() {

        try {

            pickupMap = new HashMap<>();
            reference = FirebaseDatabase.getInstance().getReference("Pickups");

            pickupArrayList = new ArrayList<>();
            pickupArrayList.clear();

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Pickup pickup = dataSnapshot.getValue(Pickup.class);
                        String place = pickup.getPlaceName();
                        String status = pickup.getStatus();
                        pickupMap.put(place,status);
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