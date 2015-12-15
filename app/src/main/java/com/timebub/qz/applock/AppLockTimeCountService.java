package com.timebub.qz.applock;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.timebub.qz.timebubtools.IService;
import com.timebub.qz.timebubtools.IService_TimeCount;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zxqso on 2015/10/27.
 */
public class AppLockTimeCountService extends Service {

    MyBinder binder;
    boolean flag;
    int timeHour = -1;
    int timeMin = -1;

    public IBinder onBind(Intent intent) {
        binder = new MyBinder();
        return binder;
    }

    private class MyBinder extends Binder implements IService_TimeCount {
        @Override
        public void setTimeCount(int hour, int min) {
            timeHour = hour;
            timeMin = min;
        }

        @Override
        public void setEnabled(boolean enabled) {
            flag = enabled;
        }

        @Override
        public int getHour() {
            return timeHour;
        }

        @Override
        public int getMin() {
            return timeMin;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        flag = false;
        new Thread() {
            @Override
            public void run() {

                while (timeMin == -1 || timeHour == -1||!flag) {
                    continue;
                }
                Timer timer=new Timer();
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        if(timeMin>0){
                            timeMin--;
                        }if(timeMin==0){
                            if(timeHour>0){
                                timeHour--;
                                timeMin=59;
                            }else{
                                onDestroy();
                                return;
                            }
                        }
                    }
                };
                timer.schedule(task,60000);
                super.run();

            }
        }.start();
    }

    @Override
    public void onDestroy() {
        flag = false;
        super.onDestroy();
        binder = null;
    }

}
