package com.timebub.qz.applock;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.timebub.qz.timebub.HTTPProcess;
import com.timebub.qz.timebub.TimeBubAppLocking;
import com.timebub.qz.timebub.TimeBubStudyFinish;
import com.timebub.qz.timebubtools.IService;
import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zxqso on 2015/10/15.
 */
public class AppLockService extends Service {

    private MyBinder binder;
    private AppLockDao lockDao;
    private MyObserver observer;
    boolean flag;
    private LockScreenReceiver receiver;
    private Intent intent;
    boolean studyStatus;
    boolean isStudying;
    boolean isLocking;
    String timeHour;
    String timeMin;
    String timeStart;
    SimpleDateFormat sdf;
    Date startTime;
    Date endTime;
    Timer timer = new Timer();
    TimerTask task;
    double between;
    final GregorianCalendar date1 = new GregorianCalendar();
    final GregorianCalendar date2 = new GregorianCalendar();
    TimeBubSharePreference sharePreference;
    TimeBubTools tools;
    int baseTime;

    List<String> lockPackages;
    List<String> tempProtectPackages;
    Thread startcount=null;
    String a = "";

    @Override
    public IBinder onBind(Intent intent) {
        binder = new MyBinder();
        return binder;
    }

    private class MyBinder extends Binder implements IService {
        public void callTempStopProtect(String packname) {
            tempStopProtect(packname);
        }

        @Override
        public void setStudyStatus(boolean flag) {
            studyStatus = flag;
        }

        @Override
        public void setLockingStatus(boolean lockflag) {
            isLocking = lockflag;
        }

        @Override
        public boolean isLocking() {
            return isLocking;
        }

        @Override
        public boolean isStudy() {
            return studyStatus;
        }
    }

    public void tempStopProtect(String packName) {
        tempProtectPackages.add(packName);
    }

    @Override
    public void onCreate() {
        Uri uri = Uri.parse("content://com.timebub.qz.applock/");
        observer = new MyObserver(new Handler());
        getContentResolver().registerContentObserver(uri, true, observer);
        sharePreference = new TimeBubSharePreference(this);
        tools = new TimeBubTools(this);
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, filter);
        super.onCreate();
        lockDao = new AppLockDao(this);
        lockPackages = lockDao.findAll();
        studyStatus = false;
        isStudying = false;
        flag = true;
        tempProtectPackages = new ArrayList<String>();
        intent = new Intent(this, TimeBubAppLocking.class);//change the class here to start activity
        if(TimeBubAppLocking.appLocking==null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        new Thread() {
            @Override
            public void run() {
                    while (flag) {
                        if (isLocking) {
                            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            if (Build.VERSION.SDK_INT <= 20) {
                                ActivityManager.RunningTaskInfo taskInfo = activityManager.getRunningTasks(1).get(0);
                                String packageName = taskInfo.topActivity.getPackageName();
                                Log.i("getActivity", packageName);
                                if (tempProtectPackages.contains(packageName)) {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    continue;
                                }
                                intent.putExtra("packageName", packageName);
                                if (lockPackages.contains(packageName)) {
                                    startActivity(intent);
                                }
                            } else {
                                String packageName = activityManager.getRunningAppProcesses().get(0).processName;
                                String a = "123";
                                Log.i("getActivity", packageName);
                                if (tempProtectPackages.contains(packageName)) {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    continue;
                                }
                                intent.putExtra("packageName", packageName);
                                if (lockPackages.contains(packageName)) {
                                    startActivity(intent);
                                }
                            }
                            if (studyStatus && !isStudying) {
                                if (sharePreference.getData("timeLimt").equals("Not Found") || sharePreference.getData("startDate").equals("Not Found")) {
//                            tools.makeToast("未成功进入学习模式，请重试");
                                    return;
                                } else {
                                    timeHour = sharePreference.getData("timeLimt").split(":")[0];
                                    timeMin = sharePreference.getData("timeLimt").split(":")[1];
                                    timeStart = sharePreference.getData("startDate");
                                    isStudying = true;
                                    baseTime = Integer.parseInt(timeHour) * 60 + Integer.parseInt(timeMin);
                                    startTime = new Date(Long.parseLong(timeStart));
                                    startcount = new Thread(new studing());
                                    startcount.start();
                                }
                            }
                        }
                    }
                super.run();
            }
        }.start();
    }

    class studing extends Thread {
        @Override
        public void run() {

            task = new TimerTask() {
                @Override
                public void run() {
                    if (isStudying) {
                        Message msg = new Message();
                        Message msg2 = new Message();
                        Bundle bdl = new Bundle();
                        Bundle bdl2 = new Bundle();
                        date1.setTime(startTime);
                        endTime = new Date(System.currentTimeMillis());
                        date2.setTime(endTime);
                        between = (date2.getTimeInMillis() - date1.getTimeInMillis()) / 1000 / 60;
//                tools.makeToast("时间还有"+String.valueOf(between));
                        bdl.putString("status", String.valueOf(between));
                        msg.setData(bdl);
                        handler.sendMessage(msg);
                        if (between >= Double.parseDouble(String.valueOf(baseTime))) {
                            bdl2.putString("status", "timedone");
                            msg2.setData(bdl2);
                            handler.sendMessage(msg2);
                        }
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
                Intent intent = new Intent(AppLockService.this, TimeBubStudyFinish.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String appID=sharePreference.getData("lockID");
                if(TimeBubAppLocking.appLocking!=null){
                    TimeBubAppLocking.appLocking.finish();
                }
                if(TimeBubStudyFinish.studyFinish!=null){
                    TimeBubStudyFinish.studyFinish.finish();
                }
                studySuccess(appID);
                isLocking=false;
                isStudying=false;
                studyStatus=false;
                sharePreference.saveData("lastState","normal");
                startActivity(intent);

//                timer=null;
//                if(startcount.isAlive()){
//                    startcount=null;
//                }
            } else {
//                tools.makeToast(result);
            }
            super.handleMessage(msg);
        }
    };

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("status");
//            tools.makeToast(result);
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        flag = false;
        super.onDestroy();
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        unregisterReceiver(receiver);//cancel the service
        binder = null;
    }

    public void studySuccess(final String appID){
        Runnable studyFailed=new Runnable() {
            @Override
            public void run() {
                HTTPProcess httpProcess=new HTTPProcess(AppLockService.this);
                String result=httpProcess.httpProcessTemp.upLoadUnlockInfo(appID,"1",String.valueOf(baseTime),"");
                Message msg=new Message();
                Bundle bundle=new Bundle();
                bundle.putString("result",result);
                msg.setData(bundle);
                mhandler.sendMessage(msg);
            }
        };
        Thread studyFailprocess=new Thread(studyFailed);
        studyFailprocess.start();
    }


    private class MyObserver extends ContentObserver {
        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            lockPackages = lockDao.findAll();
            super.onChange(selfChange);
        }
    }

    private class LockScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("AppLock", "锁屏了");
            tempProtectPackages.clear();
        }
    }
}
