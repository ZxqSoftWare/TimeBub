package com.timebub.qz.timebubtools;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by zxqso on 2015/10/22.
 */
public class GetServiceStatus {

    public boolean getServiceStatus(Context context,String serviceClassName){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos=am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo serviceInfo:serviceInfos){
            if(serviceClassName.equals(serviceInfo.service.getClassName()))
                return true;
        }
        return false;
    }

}
