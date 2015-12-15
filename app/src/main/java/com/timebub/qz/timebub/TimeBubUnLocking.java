package com.timebub.qz.timebub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.timebub.qz.alippay.AliPayInter;
import com.timebub.qz.applock.AppLockService;
import com.timebub.qz.timebubtools.IService;
import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zxqso on 2015/10/19.
 */
public class TimeBubUnLocking extends Activity {
    static public TimeBubUnLocking unLocking=null;

    Button breakBub;
    Button ignore;
    Intent serviceIntent;
    IService iService;
    MyConn conn;
    String packagename;
    TimeBubTools tools;
    TimeBubSharePreference sharePreference;
    Date startTime;
    Date endTime;
    Timer timer = new Timer();
    TimerTask task;
    String timeHour;
    String timeMin;
    String timeStart;
    TextView txtLearnTime;
    double between;
    int baseTime;
    final GregorianCalendar date1 = new GregorianCalendar();
    final GregorianCalendar date2 = new GregorianCalendar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout24_breakbub);
        txtLearnTime = (TextView) findViewById(R.id.learn_time);
        unLocking=this;
        breakBub=(Button)findViewById(R.id.breakbub);
        ignore=(Button)findViewById(R.id.back);
        tools=new TimeBubTools(this);
        sharePreference=new TimeBubSharePreference(this);
        Intent intent=getIntent();
        packagename=intent.getStringExtra("packageName");
        serviceIntent=new Intent(TimeBubUnLocking.this, AppLockService.class);
        conn=new MyConn();
        bindService(serviceIntent, conn, BIND_AUTO_CREATE);
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
        breakBub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tools.makeToast("break the bub");
                String money=sharePreference.getData("money");
                AliPayInter aliPayInter=new AliPayInter(TimeBubUnLocking.this,"时间泡解锁","用于解锁已锁定的应用",money);
                aliPayInter.pay();
            }
        });
        ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tools.makeToast("ignore the operate");
//                Intent intent=new Intent(TimeBubUnLocking.this,TimeBubAppLocking.class);
//                startActivity(intent);
                finish();
            }
        });
        Thread runTime = new Thread(new runTimeCount());
        runTime.start();
    }

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
//                int dblMin = (int) (Double.parseDouble(timeMin) - ((between / 60) % 60));
//                int dblHour = (int) (Double.parseDouble(timeHour) - (between / 3600));
//                if (dblSec == 60) dblSec = 0;
//                if (dblMin == 60) dblMin = 0;
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

    public void stopAppLockingService(){
        iService.setLockingStatus(false);
        iService.setStudyStatus(false);
//        tools.makeToast("服务已停止");
        tools.makeToast("学习失败");
        finish();
    }


    private class MyConn implements ServiceConnection {
        //在操作者在连接一个服务成功时被调用。IBinder对象就是onBind(Intent intent)返回的IBinder对象。
        public void onServiceConnected(ComponentName name, IBinder service) {
            //因为返回的IBinder实现了iService接口（向上转型）
            iService = (IService) service;
        }
        //在服务崩溃或被杀死导致的连接中断时被调用，而如果我们自己解除绑定时则不会被调用
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    public void onBackPressed() {
//        Intent intent=new Intent(TimeBubUnLocking.this,TimeBubAppLocking.class);
//        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定
        unbindService(conn);
    }
}
