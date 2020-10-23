package com.example.mybus.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mybus.R;
import com.example.mybus.activities.MainActivity;

public class SearchLocation extends Fragment {

    private ImageButton back;
    private EditText search;
    MainActivity ma;
    private String str_search = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_location, container, false);
        back = view.findViewById(R.id.back_2);
        search = view.findViewById(R.id.search_location);
        ma = (MainActivity) getActivity();
        ma.collapseState();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.onBackPressed();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                str_search = search.getText().toString().toLowerCase().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                str_search = search.getText().toString().toLowerCase().trim();
            }
        });

    }
}