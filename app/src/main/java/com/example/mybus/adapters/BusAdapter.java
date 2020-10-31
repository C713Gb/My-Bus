package com.example.mybus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybus.R;
import com.example.mybus.activities.MainActivity;
import com.example.mybus.activities.PickupActivity;
import com.example.mybus.models.Bus;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<Bus> busArrayList;
    MainActivity ma;

    public BusAdapter(Context mContext, ArrayList<Bus> busArrayList) {
        this.mContext = mContext;
        this.busArrayList = busArrayList;
    }


    @NonNull
    @Override
    public BusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusAdapter.MyViewHolder holder, int position) {

        holder.busTxt.setText(busArrayList.get(position).getId());

        holder.busTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.showDirection(busArrayList.get(position).getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return busArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView busTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ma = (MainActivity) mContext;
            busTxt = itemView.findViewById(R.id.bus_id_txt_1);
        }
    }
}
