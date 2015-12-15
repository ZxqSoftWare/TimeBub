package com.timebub.qz.timebub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

/**
 * Created by zxqso on 2015/10/26.
 */
public class TimeBubSetTime extends Activity {
    NumberPicker numHour;
    NumberPicker numMin;
    TimeBubTools tools;
    Button btnConfirm;
    TimeBubSharePreference sharePreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout13_selecttime);
        sharePreference=new TimeBubSharePreference(this);
        numHour=(NumberPicker)findViewById(R.id.timehour);
        numMin=(NumberPicker)findViewById(R.id.timemin);
        btnConfirm=(Button)findViewById(R.id.confirmtime);
        tools=new TimeBubTools(this);
        numHour.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                String tmpStr = String.valueOf(i);
                if (i < 10) {
                    tmpStr = "0" + tmpStr;
                }
                return tmpStr;
            }
        });
        numHour.setMaxValue(24);
        numHour.setMinValue(0);

        numMin.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                String tmpStr = String.valueOf(i);
                if (i < 10) {
                    tmpStr = "0" + tmpStr;
                }
                return tmpStr;
            }
        });
        numMin.setMaxValue(59);
        numMin.setMinValue(0);
        if(!sharePreference.getData("timeLimt").equals("Not Found")){
            numHour.setValue(Integer.parseInt(sharePreference.getData("timeLimt").split(":")[0]));
            numMin.setValue(Integer.parseInt(sharePreference.getData("timeLimt").split(":")[1]));
        }else {
            numMin.setValue(30);
            numHour.setValue(0);
        }
        final Bundle bundle=new Bundle();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int setHour=numHour.getValue();
                int setMin=numMin.getValue();
                if(setHour==0&&setMin==0) {
                    tools.makeToast("学习时间至少要1分钟吧骚年");
                    return;
                }
                bundle.putString("hour",String.valueOf(setHour));
                bundle.putString("min",String.valueOf(setMin));
                Intent intent=TimeBubSetTime.this.getIntent();
                intent.putExtra("lokingTime",bundle);
                TimeBubSetTime.this.setResult(0, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Bundle bundle=new Bundle();
        int setHour=numHour.getValue();
        int setMin=numMin.getValue();
        bundle.putString("hour",String.valueOf(setHour));
        bundle.putString("min", String.valueOf(setMin));
        Intent intent=TimeBubSetTime.this.getIntent();
        intent.putExtra("lokingTime", bundle);
        if(setHour==0&&setMin==0) {
            tools.makeToast("学习时间至少要1分钟吧骚年");
            return;
        }else{
            TimeBubSetTime.this.setResult(0, intent);
        }
        finish();
        super.onBackPressed();
    }
}
