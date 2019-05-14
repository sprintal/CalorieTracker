package com.kang.calorietracker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MapFragment extends Fragment {
    View vMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMap = inflater.inflate(R.layout.fragment_map, container, false);
        final Button button = vMap.findViewById(R.id.button_map);
        final TextView text = vMap.findViewById(R.id.text_map);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("aaaaaaaaa");
            }
        });
        return vMap;
    }
}
