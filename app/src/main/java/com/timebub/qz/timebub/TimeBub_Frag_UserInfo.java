package com.timebub.qz.timebub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Aphasia on 15/9/24.
 */
public class TimeBub_Frag_UserInfo extends Fragment {
    TextView level;
    TextView point;
    TimeBubTools tools;
    Button btnSignIn;
    Button record;
    TimeBubSharePreference sharePreference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout18_person, container, false);
        level = (TextView) view.findViewById(R.id.textView2);
        point = (TextView) view.findViewById(R.id.textView4);
        tools=new TimeBubTools(getActivity());
        btnSignIn=(Button)view.findViewById(R.id.clockin);
        sharePreference=new TimeBubSharePreference(getActivity());
        record=(Button)view.findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity().getApplicationContext(),TimeBubUserRecord.class);
                getActivity().startActivity(intent);
            }
        });
//        btnSignIn.setEnabled(false);
//        btnSignIn.setVisibility(View.INVISIBLE);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread sign = new Thread(new signIn());
                sign.start();
            }
        });
        if(sharePreference.getData("userLevel").equals("Not Found")){
            level.setText("0级");
        }else{
            level.setText(sharePreference.getData("userLevel")+"级");
        }
        if(sharePreference.getData("userPoints").equals("Not Found")){
            point.setText("0分");
        }else{
            point.setText(sharePreference.getData("userPoints")+"分");
        }
        Thread getUserInfo=new Thread(new getUserInfo());
        getUserInfo.start();
        new Thread(new getSignState()).start();
        return view;

    }

    class getUserInfo extends Thread {
        @Override
        public void run() {
            HTTPProcess httpProcess = new HTTPProcess(getActivity().getApplicationContext());
            String userInfo = httpProcess.getUserEvents();
            Message msg = new Message();
            Bundle bdl = new Bundle();
            bdl.putString("status", userInfo);
            msg.setData(bdl);
            msg.what=1;
            handler.sendMessage(msg);
            super.run();
        }
    }
    class signIn extends Thread{
        @Override
        public void run() {
            HTTPProcess httpProcess=new HTTPProcess(getActivity().getApplicationContext());
            String result=httpProcess.signUp();
            Message msg = new Message();
            Bundle bdl = new Bundle();
            bdl.putString("status", result);
            msg.what=1;
            msg.setData(bdl);
            mhandler.sendMessage(msg);
            super.run();
        }
    }
    class getSignState extends Thread{
        @Override
        public void run() {
            HTTPProcess httpProcess=new HTTPProcess(getActivity());
            String result=httpProcess.getSignState();
            Message msg = new Message();
            Bundle bdl = new Bundle();
            bdl.putString("status", result);
            msg.what=2;
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("status");
            if (result.equals("timeout")) {
                tools.makeToast(Public_Enum.timeoutMsg);
                return;
            }
            try {

                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = (JSONObject) tokener.nextValue();
                String status=object.getString("status");
                if(status.equals("0")){
                    if(msg.what==1) {
                        JSONObject data=object.getJSONObject("data");
                        level.setText(data.getString("userLevel") + "级");
                        point.setText(data.getString("totalPoints") + "分");
//                        tools.makeToast("用户等级信息已获取");
//                        tools.makeToast(object.getString("data"));
                        sharePreference.saveData("userLevel", data.getString("userLevel"));
                        sharePreference.saveData("userPoints", data.getString("totalPoints"));
                    }
                    if(msg.what==2){
//                        tools.makeToast(result);
                        String signState=object.getString("data");
                        if(signState.equals("0")){
                            btnSignIn.setText("签到");
                        }else{
                            btnSignIn.setText("已签到");
                            btnSignIn.setEnabled(false);
                        }
                    }
                }
                super.handleMessage(msg);
            }catch (JSONException jE){
                jE.printStackTrace();
            }
        }
    };
    Handler mhandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("status");
            if (result.equals("timeout")) {
                tools.makeToast(Public_Enum.timeoutMsg);
                return;
            }
            else{
//                tools.makeToast("签到成功,签到数据为："+result);
                tools.makeToast("签到成功");
                btnSignIn.setText("已签到");
                btnSignIn.setEnabled(false);
            }
            super.handleMessage(msg);
        }
    };
}


