package com.timebub.qz.timebub;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zxqso on 2015/10/29.
 */
public class TimeBubAppLocking extends Activity {
    public static TimeBubAppLocking appLocking=null;
    TimeBubSharePreference sharePreference;
    TimeBubTools tools;
    String timeHour;
    String timeMin;
    String timeStart;
    SimpleDateFormat sdf;
    Date startTime;
    Date endTime;
    Timer timer = new Timer();
    TimerTask task;
    TextView txtLearnTime;

    double between;
    final GregorianCalendar date1 = new GregorianCalendar();
    final GregorianCalendar date2 = new GregorianCalendar();
    int baseTime;
    Button btnlearn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout15_locking);
        appLocking=this;
        sharePreference = new TimeBubSharePreference(this);
        btnlearn = (Button) findViewById(R.id.learn);
        tools = new TimeBubTools(this);
        txtLearnTime = (TextView) findViewById(R.id.learn_time);
        sdf = new SimpleDateFormat("yyyymmddhhmm");
//        tools.makeToast("a new activity");
        if (sharePreference.getData("timeLimt").equals("Not Found") || sharePreference.getData("startDate").equals("Not Found")) {
            tools.makeToast("未成功进入学习模式，请重试");
            return;
        } else {
            timeHour = sharePreference.getData("timeLimt").split(":")[0];
            timeMin = sharePreference.getData("timeLimt").split(":")[1];
            timeStart = sharePreference.getData("startDate");
            txtLearnTime.setText(timeHour + " : " + timeMin + " : " + "00");
        }
        baseTime = (Integer.parseInt(timeHour)*60+Integer.parseInt(timeMin))*60*1000;
        startTime = new Date(Long.parseLong(timeStart));

        btnlearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimeBubAppLocking.this,TimeBubUnLocking.class);
                startActivity(intent);
            }
        });
//        Thread sta=new Thread(new runTimeService());
//        sta.start();;


//        try {
//
//        }catch (ParseException pe){
//            pe.printStackTrace();
//        }
        Thread runTime = new Thread(new runTimeCount());
        runTime.start();
    }

//    class runTimeService extends Thread{
//        @Override
//        public void run() {
//            iService.setStudyStatus(true);
//            super.run();
//        }
//    }

    class runTimeCount extends Thread {
        @Override
        public void run() {
            date1.setTime(startTime);
            task = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    Bundle bdl = new Bundle();
                    endTime = new Date(System.currentTimeMillis());
                    date2.setTime(endTime);
                    between = (date2.getTimeInMillis() - date1.getTimeInMillis());
//                tools.makeToast("时间还有"+String.valueOf(between));
                    bdl.putString("status", String.valueOf(between));
                    msg.setData(bdl);
                    handler.sendMessage(msg);
                    if (between >= (Double.parseDouble(String.valueOf(baseTime)) * 60)) {
                        bdl.putString("status", "timedone");
                        msg.setData(bdl);
                        handler.sendMessage(msg);
                    }
                }
            };
            timer.schedule(task, 0, 500);
            super.run();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("status");
            if (result.equals("timedone")) {
//                tools.makeToast("时间到");
                timer.cancel();
//                onDestroy();
//                Thread exit=new Thread(new exitActivity());
//                exit.start();
            } else {
//                tools.makeToast(result);
                double totalmill=(Integer.parseInt(timeHour)*60+Integer.parseInt(timeMin))*60*1000;
                double left=totalmill-between;
                int lSec=(int)(left/1000)%60;
                int lMin=((int)(left/1000)/60)%60;
                int lHour=((int)(left/1000)/60)/60;
                String strHour = "", strMin = "", strSec = "";
                if (lHour < 10) {
                    strHour = "0" + String.valueOf(lHour);
                } else strHour = String.valueOf(lHour);
                if (lMin < 10) {
                    strMin = "0" + String.valueOf(lMin);
                } else strMin = String.valueOf(lMin);
                if (lSec < 10) {
                    strSec = "0" + String.valueOf(lSec);
                } else strSec = String.valueOf(lSec);
//                int dblSec = (int) (60 - between % 60);
//                int dblMin = (int) (Integer.parseInt(timeMin) - (int)((between / 60) % 60));
//                int dblHour = (int) (Integer.parseInt(timeHour) - (int)(between / 3600));
//                if (dblSec == 60) {
//                    dblSec = 0;
//                    dblMin++;
//                }
//                if (dblMin == 60) {
//                    dblMin = 0;
//                    dblHour++;
//                }
//                String strHour = "", strMin = "", strSec = "";
//                if (dblHour < 10) {
//                    strHour = "0" + String.valueOf(dblHour);
//                } else strHour = String.valueOf(dblHour);
//                if (dblMin < 10) {
//                    strMin = "0" + String.valueOf(dblMin);
//                } else strMin = String.valueOf(dblMin);
//                if (dblSec < 10) {
//                    strSec = "0" + String.valueOf(dblSec);
//                } else strSec = String.valueOf(dblSec);
                txtLearnTime.setText(strHour + " : " + strMin + " : " + strSec);
//                if(dblHour==0&&dblSec==1&&dblMin==0){
//                    timer.cancel();
//                }
                if(lHour==0&&lSec==0&&lMin==0){
                    timer.cancel();
                }
            }
            super.handleMessage(msg);
        }
    };

    class exitActivity extends Thread{
        @Override
        public void run() {
            finish();
            super.run();
        }
    }
//
//    @Override
//    public void onBackPressed() {
//        finish();
//        super.onBackPressed();
//    }

    @Override
    protected void onDestroy() {
        timer = null;
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo =
                pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
            startActivitySafely(startIntent);
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "null",
                    Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "null",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
