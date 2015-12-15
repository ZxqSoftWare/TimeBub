package com.timebub.qz.timebubtools;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zxqso on 2015/10/18.
 */
public class TimeBubSharePreference {
    private Context context;
    public TimeBubSharePreference(Context context){
        this.context=context;
    }
    public void saveData(String key, String data){
        SharedPreferences share=context.getSharedPreferences("userPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=share.edit();
        editor.putString(key,data);
        editor.commit();
    }
    public String getData(String key){
        SharedPreferences share=context.getSharedPreferences("userPref",Context.MODE_PRIVATE);
        return share.getString(key,"Not Found");
    }
}
