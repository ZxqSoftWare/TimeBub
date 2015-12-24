package com.timebub.qz.timebub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timebub.qz.applock.AppLockDao;
import com.timebub.qz.applock.AppLockService;
import com.timebub.qz.timebubtools.GetServiceStatus;
import com.timebub.qz.timebubtools.IService;
import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.List;

/**
 * Created by Aphasia on 15/9/24.
 */
public class TimeBub_Frag_Main extends Fragment {
    FrameLayout appList;
    View view;
    LinearLayout setTime;
    LinearLayout setMoney;
    TimeBubTools tools;
    TimeBubSharePreference sharePreference;
    Button btnStart;
    IService iService;
    MyConn conn;
    Intent serviceIntent;
    TextView learnTime;
    TextView money;
    AppLockDao lockDao;
    List<String> packageNames;
    String timeHour;
    String timeMin;
    ProgressDialog progressDialog;
    HTTPProcess httpProcess;
    String latestVer;
    String latestVerInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout06_main, container, false);
        appList = (FrameLayout) view.findViewById(R.id.bubbleView);
        setTime = (LinearLayout) view.findViewById(R.id.settime);
        setMoney = (LinearLayout) view.findViewById(R.id.setmoney);
        btnStart = (Button) view.findViewById(R.id.learn);
        learnTime = (TextView) view.findViewById(R.id.learn_time);
        money = (TextView) view.findViewById(R.id.textView);
        lockDao = new AppLockDao(getActivity().getApplicationContext());
        httpProcess = new HTTPProcess(getActivity());
        serviceIntent = new Intent(getActivity().getApplicationContext(), AppLockService.class);
        conn = new MyConn();
        getActivity().bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
        tools = new TimeBubTools(getActivity().getApplicationContext());
        sharePreference = new TimeBubSharePreference(TimeBub_Frag_Main.this.getActivity().getApplicationContext());
        if (!sharePreference.getData("timeLimt").equals("Not Found") && !sharePreference.getData("money").equals("Not Found")) {
            String timeHour1 = sharePreference.getData("timeLimt").split(":")[0];
            String timeMin1 = sharePreference.getData("timeLimt").split(":")[1];
            if (Integer.parseInt(timeHour1) < 10) timeHour1 = "0" + timeHour1;
            if (Integer.parseInt(timeMin1) < 10) timeMin1 = "0" + timeMin1;
            learnTime.setText(timeHour1 + " : " + timeMin1);
            money.setText(sharePreference.getData("money"));
        }
//        updateDialog("V1.2","test");
        String tem = tools.getCurrentVer();
//        new Thread(new getLatestVer()).start();
//        GetServiceStatus serviceStatus = new GetServiceStatus();
//        if (serviceStatus.getServiceStatus(getActivity().getApplicationContext(), "com.timebub.qz.applock.AppLockService")) {
//            if(iService.isLocking()) {
//                tools.makeToast("检测到您正在学习，将继续上次的进程");
//                Intent intent = new Intent(getActivity().getApplicationContext(), TimeBubAppLocking.class);
//                startActivity(intent);
//            }
//        }

//        new Thread() {
//            @Override
//            public void run() {
//                GetServiceStatus serviceStatus = new GetServiceStatus();
//                while (iService == null) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (serviceStatus.getServiceStatus(getActivity().getApplicationContext(), "com.timebub.qz.applock.AppLockService")) {
//                    boolean tem = iService.isLocking();
//                    if (iService.isLocking()) {
//                        tools.makeToast("检测到您正在学习，将继续上次的进程");
//                        Intent intent = new Intent(getActivity().getApplicationContext(), TimeBubAppLocking.class);
//                        startActivity(intent);
//                    }
//                }
//                super.run();
//            }
//        }.start();


        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TimeBubSetTime.class);
                startActivityForResult(intent, 0);
            }
        });
        setMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TimeBubSetMoney.class);
                startActivityForResult(intent, 1);
            }
        });
        appList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TimeBubSelectLockedApp.class);
                getActivity().startActivity(intent);
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = String.valueOf(System.currentTimeMillis());
                sharePreference.saveData("startDate", date);
                boolean isTime = true;
                boolean isMoney = true;
                if (sharePreference.getData("timeLimt").toString().equals("Not Found")) {
                    sharePreference.saveData("timeLimt", "0:30");
                    isTime = false;
//                    tools.makeToast("由于您没有修改时间或者金钱，将以默认的设置开始本服务，默认值为时限30分钟，金额2.0元");
//                    return;
                }
                if (sharePreference.getData("money").toString().equals("Not Found")) {
                    sharePreference.saveData("money", "2.0");
                    isMoney = false;
//                    tools.makeToast("由于您没有修改时间或者金钱，将以默认的设置开始本服务，默认值为时限30分钟，金额2.0元");
//                    return;
                }
                if (!isTime) tools.makeToast("您未设置学习时间，默认为30分钟");
                if (!isMoney) tools.makeToast("您未设置金额，默认为2.0元");
                if (lockDao.findAll().size() == 0) {
                    tools.makeToast("不选应用就想学习？想得美！");
                    return;
                }
                if (!iService.isLocking() || !iService.isStudy()) {
                    iService.setStudyStatus(true);
                    iService.setLockingStatus(true);
//                    tools.makeToast("开启时间泡服务");
                    packageNames = lockDao.findAll();
                    Thread upload = new Thread(new upLoadStudyInfo());
                    upload.start();
                    sharePreference.saveData("lastState", "using");
                    Intent intent = new Intent(TimeBub_Frag_Main.this.getActivity(), TimeBubAppLocking.class);
                    getActivity().startActivity(intent);
                } else {
//                    tools.makeToast("时间泡服务运行中");
                    Intent intent = new Intent(TimeBub_Frag_Main.this.getActivity(), TimeBubAppLocking.class);
                    getActivity().startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.layout06_main, null);
        appList = (FrameLayout) view.findViewById(R.id.bubbleView);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) {
            Bundle bundle = data.getBundleExtra("lokingTime");
            timeHour = bundle.getString("hour");
            timeMin = bundle.getString("min");
            tools.makeToast("设置了" + String.valueOf(timeHour) + "小时" + String.valueOf(timeMin) + "分钟");
            sharePreference.saveData("timeLimt", timeHour + ":" + timeMin);
            String timeHour1 = timeHour, timeMin1 = timeMin;
            if (Integer.parseInt(timeHour1) < 10) timeHour1 = "0" + timeHour1;
            if (Integer.parseInt(timeMin1) < 10) timeMin1 = "0" + timeMin1;
            learnTime.setText(timeHour1 + " : " + timeMin1);
        }
        if (requestCode == 1 && resultCode == 1) {
            Bundle bundle = data.getBundleExtra("lockingMoney");
            tools.makeToast("设置了" + bundle.getString("money"));
            sharePreference.saveData("money", bundle.getString("money"));
            money.setText("￥" + bundle.getString("money"));
        }
    }

    class upLoadStudyInfo extends Thread {
        @Override
        public void run() {
            HTTPProcess httpProcess = new HTTPProcess(getActivity().getApplicationContext());
            String packageArray = "";
            for (int i = 0; i < packageNames.size(); i++)
                packageArray += packageNames.get(i) + ",";
            String timeHour = "";
            String timeMin = "";
            if (!sharePreference.getData("timeLimt").equals("Not Found")) {
                timeHour = sharePreference.getData("timeLimt").split(":")[0];
                timeMin = sharePreference.getData("timeLimt").split(":")[1];
            } else
                tools.makeToast("请选择学习时长或金额");
            String result = httpProcess.upLoadStudyInfo(String.valueOf((Integer.parseInt(timeHour) * 60) + Integer.parseInt(timeMin)), packageArray);
            Message msg = new Message();
            Bundle bdl = new Bundle();
            bdl.putString("result", result);
            msg.what = 2;
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("result");
            if (result.equals("timeout")) {
                tools.makeToast(Public_Enum.timeoutMsg);
                return;
            }
            if (result.equals("downLoad finished")) {
                tools.makeToast(result);
                tools.installAPK((File) msg.obj);
                return;
            }
            if (result.equals("downLoad error")) {
                tools.makeToast(result);
                return;
            }
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = (JSONObject) tokener.nextValue();
                String status = "";
                String data = "";
                status = object.getString("status");
                if (status.equals("0")) {
                    if (msg.what == 2) {
                        data = object.getString("data");
//                        tools.makeToast("上传用户学习成功，数据回传ID为:" + data);
                        sharePreference.saveData("lockID", data);
                    }
                    if (msg.what == 1) {
                        JSONObject dataObj = object.getJSONObject("data");
                        latestVer = dataObj.getString("lastestVersion");
                        latestVerInfo = dataObj.getString("updateInfo");
                        if (!tools.getCurrentVer().equals(latestVer))
                            updateDialog(latestVer.split(" ")[0], latestVerInfo);
                    }

                }
            } catch (JSONException jE) {
                jE.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };

//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            int hour=Integer.parseInt(msg.getData().getString("hour"));
//            int min=Integer.parseInt(msg.getData().getString("min"));
//
//            serviceIntent=new Intent(getActivity(), AppLockTimeCountService.class);
//            getActivity().startService(serviceIntent);
//            tools.makeToast("倒计时服务已运行");
//            conn=new MyConn();
//
//            getActivity().bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
//            iService.setTimeCount(hour, min);
//
//            super.handleMessage(msg);
//        }
//    };
//    private class MyConn implements ServiceConnection {
//        //在操作者在连接一个服务成功时被调用。IBinder对象就是onBind(Intent intent)返回的IBinder对象。
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            //因为返回的IBinder实现了iService接口（向上转型）
//            iService = (IService_TimeCount) service;
//        }
//        //在服务崩溃或被杀死导致的连接中断时被调用，而如果我们自己解除绑定时则不会被调用
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        //解除绑定
//        getActivity().unbindService(conn);
//    }

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
    public void onDestroy() {
        super.onDestroy();

        //解除绑定
        getActivity().unbindService(conn);
    }

    class getLatestVer extends Thread {
        @Override
        public void run() {
            String result = httpProcess.getAppVersion();
            Message msg = new Message();
            Bundle bdl = new Bundle();
            bdl.putString("result", result);
            msg.what = 1;
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }


    protected void updateDialog(String ver, String infomation) {
        final String lVer = ver;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setIcon(getResources().getDrawable(R.drawable.logo));
        builder.setTitle("泡泡叫你来升级啦");
        builder.setMessage("版本:" + ver + "\n更新日志:" + infomation);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("泡泡正在升级");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String fileName1 = "timeBub_V" + lVer + ".apk";
                File file1 = new File(Environment.getExternalStorageDirectory(), fileName1);
                if (tools.fileIsExists(file1)) {
                    tools.makeToast("更新包已下载，将直接安装");
                    tools.installAPK(file1);
                    return;
                }
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    progressDialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            String fileName = "timeBub_V" + lVer + ".apk";
                            File file = new File(Environment.getExternalStorageDirectory(), fileName);
                            file = tools.getUpdateFile("http://120.26.132.72:8089/apk/lastest.apk", file.getAbsolutePath(), progressDialog);
                            if (file != null) {
                                Message msg = new Message();
                                Bundle bdl = new Bundle();
                                bdl.putString("result", "downLoad finished");
                                msg.setData(bdl);
                                msg.obj = file;
                                handler.sendMessage(msg);
                                super.run();
                            } else {
                                Message msg = new Message();
                                Bundle bdl = new Bundle();
                                bdl.putString("result", "downLoad error");
                                msg.setData(bdl);
                                handler.sendMessage(msg);
                                super.run();
                            }
                            progressDialog.dismiss();
                            super.run();
                        }
                    }.start();
                } else {
                    tools.makeToast("存储空间不可用");
                }
            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


}


