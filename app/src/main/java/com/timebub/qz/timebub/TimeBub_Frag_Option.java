package com.timebub.qz.timebub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Aphasia on 15/9/24.
 */
public class TimeBub_Frag_Option extends Fragment {
    LinearLayout changepsw;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout22_setting, container, false);
        changepsw=(LinearLayout)view.findViewById(R.id.changepswly);
        changepsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity().getApplicationContext(),TimeBubChangePsw.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }
}


