package com.timebub.qz.applock;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxqso on 2015/10/15.
 */
public class AppInfoProvider {
    private PackageManager pm;
    Context context;
    public AppInfoProvider(Context context){
        pm=context.getPackageManager();
        this.context=context;
    }

    public List<AppInfo> getAllApplication(){
        ArrayList<AppInfo> appList=new ArrayList<AppInfo>();
        List<PackageInfo> packageInfos=pm.getInstalledPackages(0);
        for(int i=0;i<packageInfos.size();i++){
            PackageInfo packageInfo=packageInfos.get(i);
            if(packageInfo.packageName.equals(context.getPackageName())){
                continue;
            }
            AppInfo packages=new AppInfo();
            packages.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
            packages.setPackageName(packageInfo.packageName);
            packages.setVersionName(packageInfo.versionName);
            packages.setVersionCode(String.valueOf(packageInfo.versionCode));
            packages.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
            if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0){
                appList.add(packages);
            }
        }
        return appList;
    }
}
