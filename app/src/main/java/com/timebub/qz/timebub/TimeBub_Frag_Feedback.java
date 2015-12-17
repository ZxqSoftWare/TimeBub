package com.timebub.qz.timebub;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.timebub.qz.adapter.RecordList;
import com.timebub.qz.timebubtools.RecordInfo;
import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Aphasia on 15/9/24.
 */
public class TimeBub_Frag_Feedback extends Fragment {
    EditText txtFeedBack;
    Button btnSubmit;
    TimeBubTools tools;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback, container, false);
        tools=new TimeBubTools(TimeBub_Frag_Feedback.this.getActivity());
        txtFeedBack=(EditText) view.findViewById(R.id.txtfeedback);
        btnSubmit=(Button)view.findViewById(R.id.button4);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new submitFeedBack()).start();
            }
        });
        return view;
    }

    class submitFeedBack extends Thread{
        @Override
        public void run() {
            HTTPProcess httpProcess = new HTTPProcess(TimeBub_Frag_Feedback.this.getActivity());
            String result=httpProcess.sendFeedBack(txtFeedBack.getText().toString());
            Message msg = new Message();
            Bundle bdl = new Bundle();
            bdl.putString("record", result);
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result=msg.getData().getString("record");
//            tools.makeToast(result);
            if (result.equals("timeout")) {
                tools.makeToast(Public_Enum.timeoutMsg);
                return;
            }
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = (JSONObject) tokener.nextValue();
                String status = object.getString("status");
                if (status.equals("0")) {
                    String array = object.getString("data");
                    if(array.equals("OK")){
                        tools.makeToast("您的反馈我们已经收到，谢谢您的支持");
                    }
                }else{
                    tools.makeToast("反馈发送失败，请重试");
                }
            } catch (JSONException jE) {
                jE.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };
}


