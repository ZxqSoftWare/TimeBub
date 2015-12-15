package com.timebub.qz.timebub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.timebub.qz.timebubtools.TimeBubSharePreference;

/**
 * Created by zxqso on 2015/10/29.
 */
public class TimeBubStudyFinish extends Activity {
    public static TimeBubStudyFinish studyFinish=null;
    TextView txtTime;
    TextView txtMoney;
    Button btnContinue;
    TimeBubSharePreference sharePreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout26_success);
        studyFinish=this;
        sharePreference=new TimeBubSharePreference(this);
        txtTime=(TextView)findViewById(R.id.textView11);
        txtMoney=(TextView)findViewById(R.id.textView12);
        String money="";
        String timeHour="";
        String timeMin="";
        money=sharePreference.getData("money");
        timeHour = sharePreference.getData("timeLimt").split(":")[0];
        timeMin = sharePreference.getData("timeLimt").split(":")[1];
        txtTime.setText("本次学习 "+timeHour+" 小时 "+timeMin+" 分钟");
        txtMoney.setText("保住了 "+money+" 元");
        btnContinue=(Button)findViewById(R.id.button3);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }
}
