package com.timebub.qz.timebub;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.timebub.qz.applock.AppInfo;
import com.timebub.qz.applock.AppInfoProvider;
import com.timebub.qz.applock.AppLockDao;
import com.timebub.qz.timebubtools.TimeBubTools;
import com.timebub.qz.timebud.AppListItem;


import java.util.List;

/**
 * Created by zxqso on 2015/10/18.
 */
public class TimeBubSelectLockedApp extends Activity {
    public static TimeBubSelectLockedApp selectLockedApp=null;
    ListView appList;
    List<String> lockedApp;
    List<String> tempunLock;
    List<AppInfo> appInfoList;
    AppLockDao lockDao;
    AppInfoProvider appInfoProvider;
    LinearLayout progress;
    Button confirmApp;
    TimeBubTools tools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout12_selectapp);
        appList=(ListView)findViewById(R.id.listView);
        confirmApp=(Button)findViewById(R.id.confirmapp);
        lockDao=new AppLockDao(TimeBubSelectLockedApp.this);
        progress=(LinearLayout)findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        appInfoProvider=new AppInfoProvider(TimeBubSelectLockedApp.this);
        selectLockedApp=this;
        tools=new TimeBubTools(TimeBubSelectLockedApp.this);
        lockedApp=lockDao.findAll();
        new Thread(){
            @Override
            public void run() {
                appInfoList=appInfoProvider.getAllApplication();
                handler.sendEmptyMessage(0);
            }
        }.start();
        confirmApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                AppInfo appInfo=(AppInfo)appList.getItemAtPosition(i);
//                String packagename=appInfo.getPackageName();
//                CheckBox chkbox=(CheckBox)view.findViewById(R.id.checkItem);
//                tools.makeToast(appInfo.getAppName());
//                if(lockedApp.contains(packagename)){
//                    Uri uri=Uri.parse("content://com.timebub.qz.applock/DELETE");
//                    getContentResolver().delete(uri,null,new String[]{packagename});
//                    chkbox.setChecked(false);
//                    lockedApp.remove(packagename);
//                }else{
//                    Uri uri=Uri.parse("content://com.timebub.qz.applock/ADD");
//                    ContentValues values=new ContentValues();
//                    values.put("packname",packagename);
//                    getContentResolver().insert(uri,values);
//                    chkbox.setChecked(true);
//                    lockedApp.add(packagename);
//                }
//            }
//        });
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            appList.setAdapter(new AppListItem(TimeBubSelectLockedApp.this,appInfoList));
            progress.setVisibility(View.GONE);

        }
    };
}
