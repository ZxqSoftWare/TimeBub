package com.timebub.qz.applock;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by zxqso on 2015/10/15.
 */
public class AppInfo {

    private String appName = "";
    private String packageName = "";
    private String versionName = "";
    private String versionCode = "";
    private Drawable appIcon = null;

    public void setAppName(String appname){
        this.appName=appname;
    }

    public void setPackageName(String packageName){
        this.packageName=packageName;
    }

    public void setVersionName(String versionName){
        this.versionName=versionName;
    }

    public void setVersionCode(String versionCode){
        this.versionCode=versionCode;
    }

    public void setAppIcon(Drawable appIcon){
        this.appIcon=appIcon;
    }

    public String getAppName(){
        return this.appName;
    }

    public String getPackageName(){
        return this.packageName;
    }

    public String getVersionName(){
        return this.versionName;
    }

    public String getVersionCode(){
        return this.versionCode;
    }

    public Drawable getAppIcon(){
        return this.appIcon;
    }
    
    public void print() {
        Log.v("app", "Name:" + appName + " Package:" + packageName);
        Log.v("app", "Name:" + appName + " versionName:" + versionName);
        Log.v("app", "Name:" + appName + " versionCode:" + versionCode);
    }

}
