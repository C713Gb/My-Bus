package com.example.mybus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybus.R;
import com.example.mybus.activities.PickupActivity;
import com.example.mybus.models.Pickup;

import java.util.ArrayList;

public class PickupAdapter extends RecyclerView.Adapter<PickupAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<Pickup> pickupArrayList;
    PickupActivity pa;
    public String checked = "";

    public PickupAdapter(Context mContext, ArrayList<Pickup> pickupArrayList) {
        this.mContext = mContext;
        this.pickupArrayList = pickupArrayList;
    }


    @NonNull
    @Override
    public PickupAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickup_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupAdapter.MyViewHolder holder, int position) {
        holder.text.setText(pickupArrayList.get(position).getPlaceName());
        String status = pickupArrayList.get(position).getStatus();
        if (status.equals("true")) {
            holder.aSwitch.setChecked(true);
        } else {
            holder.aSwitch.setChecked(false);
        }

        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.aSwitch.isChecked()){
                    // checked
                    checked = "true";
                } else {
                    // not checked
                    checked = "false";
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pickupArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        Switch aSwitch;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pa = (PickupActivity) mContext;
            text = itemView.findViewById(R.id.location_text);
            aSwitch = itemView.findViewById(R.id.location_switch);
            cardView = itemView.findViewById(R.id.location_card);

        }
    }
}
