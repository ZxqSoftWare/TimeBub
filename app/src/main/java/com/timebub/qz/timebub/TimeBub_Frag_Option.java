package com.timebub.qz.timebub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

/**
 * Created by Aphasia on 15/9/24.
 */
public class TimeBub_Frag_Option extends Fragment {
    LinearLayout changepsw;
    TimeBubTools tools;
    TimeBubSharePreference sharePreference;
    Button btnClrToken;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout22_setting, container, false);
        tools=new TimeBubTools(TimeBub_Frag_Option.this.getActivity());
        sharePreference=new TimeBubSharePreference(this.getActivity());
        btnClrToken=(Button)view.findViewById(R.id.exitapp);
        changepsw=(LinearLayout)view.findViewById(R.id.changepswly);
        changepsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(getActivity().getApplicationContext(),TimeBubChangePsw.class);
//                getActivity().startActivity(intent);
                tools.makeToast("程序员正在努力施工，稍安勿躁");
            }
        });
        btnClrToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePreference.saveData("userID","");
                sharePreference.saveData("userToken","");
                sharePreference.saveData("timeLimt","0:30");
                sharePreference.saveData("money","2.0");
                sharePreference.saveData("userLevel","0");
                sharePreference.saveData("userPoint","0");
                sharePreference.saveData("lastState","normal");
                if(TimeBubMain.timeBubMain!=null){
                    TimeBubMain.timeBubMain.finish();
                    Intent intent=new Intent(TimeBub_Frag_Option.this.getActivity(),TimeBubLogin.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }
}


