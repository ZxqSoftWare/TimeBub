package com.timebub.qz.timebub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.timebub.qz.applock.AppLockService;
import com.timebub.qz.timebubtools.GetServiceStatus;
import com.timebub.qz.timebubtools.IService;
import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

//建立一个新的activity，沿用actionbaractivity，实现接口是后面那个
public class TimeBubMain extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     * 通过mNavigationDrawerFragment来调用
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    static public TimeBubMain timeBubMain=null;
    IService iService;
    MyConn conn;
    Intent serviceIntent;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    HTTPProcess httpProcess;
    private int casenumber;
    //private View thisismyview;
    //private CharSequence mLayout;//本来是尝试改变layout的……后来感觉好像不是很合适……
    String lastStudyState;
    TimeBubSharePreference share;
    TimeBubTools tools;
    ActionBar actionBar;
    String latestVer;
    String latestVerInfo;
    ProgressDialog progressDialog;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    //生成窗口
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        httpProcess = new HTTPProcess(this);
        share = new TimeBubSharePreference(this);
        actionBar = getSupportActionBar();
        serviceIntent = new Intent(TimeBubMain.this.getApplicationContext(), AppLockService.class);
        conn = new MyConn();
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);//这个就是找到开始的navigation drawer啦
        //其实还是侧滑菜单那个东西= =
        mTitle = getTitle();//获取action bar的标题
        timeBubMain=this;
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));//获取drawer_layout的id……【这个到底在哪里啊
        restoreActionBar();
        new Thread(new getLatestVer()).start();
        GetServiceStatus serviceStatus = new GetServiceStatus();
//        mNavigationDrawerFragment.openDrawer();
        tools = new TimeBubTools(this);
        lastStudyState = share.getData("lastState");
        if(!serviceStatus.getServiceStatus(TimeBubMain.this, "com.timebub.qz.applock.AppLockService")) {
            if (lastStudyState.equals("Not Found") || lastStudyState.equals("normal")) {
                share.saveData("lastState", "normal");
            } else {
                tools.makeToast("曾经的强退带来了今天的惩罚，接招吧！");
                Thread checkLgn = new Thread(new shutDownApp());
                checkLgn.start();
//            share.saveData("lastState", "normal");
            }
        }

        new Thread() {
            @Override
            public void run() {
                GetServiceStatus serviceStatus = new GetServiceStatus();
                while (iService == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (serviceStatus.getServiceStatus(getApplicationContext(), "com.timebub.qz.applock.AppLockService")) {
                    boolean tem = iService.isLocking();
                    if (iService.isLocking()) {
                        tools.makeToast("检测到您正在学习，将继续上次的进程");
                        Intent intent = new Intent(getApplicationContext(), TimeBubAppLocking.class);
                        startActivity(intent);
                    }
                }
                super.run();
            }
        }.start();

        if (serviceStatus.getServiceStatus(TimeBubMain.this, "com.timebub.qz.applock.AppLockService")) {
//            if(iService.isLocking()) {
//                tools.makeToast("检测到您正在学习，将继续上次的进程");
//                Intent intent = new Intent(TimeBubMain.this, TimeBubAppLocking.class);
//                startActivity(intent);
//            }
        } else {
            Intent intent1 = new Intent(TimeBubMain.this, AppLockService.class);
            startService(intent1);
//            tools.makeToast("服务启动成功");

        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();//获取fragmentmangager
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))//替换container
                //然而这个position是啥我也没搞懂
                //好像是后面用来更改界面用的
                // .addToBackStack(null)//听说是用来允许后退时返回上个界面的，总之先放着
                .commit();
    }

    /*
    *Activity中使用getSupportFragmentManager()获取FragmentManager
    *之后调用beginTransaction去创建一个FragmentTransaction对象
    *再调用add()方法即可添加一个fragment
    *在activity中可以使用同一个FragmentTransaction对象去执行多个fragment事务，当做这样操作时，必须调用commint()方法。

     */
    //点击字幕更改mtitle的值…………//能否在点击case之后更改界面呢？
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                changeFragment(number);
                actionBar.setTitle(mTitle);
                //casenumber = 1;
                //changeFragment();
                //mLayout = getString(R.string.layout_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                changeFragment(number);
                actionBar.setTitle(mTitle);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                changeFragment(number);
                actionBar.setTitle(mTitle);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                changeFragment(number);
                actionBar.setTitle(mTitle);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                changeFragment(number);
                actionBar.setTitle(mTitle);
                break;

        }
    }

    //更改actionbar的内容
    public void restoreActionBar() {

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff41bbbc")));
    }

    class getLatestVer extends Thread{
        @Override
        public void run() {
            String result = httpProcess.getAppVersion();
            Message msg=new Message();
            Bundle bdl=new Bundle();
            bdl.putString("result",result);
            msg.what=1;
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "TimeBubMain Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.timebub.qz.timebub/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "TimeBubMain Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.timebub.qz.timebub/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }


    class shutDownApp extends Thread {
        @Override
        public void run() {
            String result = httpProcess.shutDownApp();
            Message msg = new Message();
            Bundle bdl = new Bundle();
            bdl.putString("result", result);
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }


    @Override
    //创建optionmenu
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);//这个main菜单是用来干嘛的，旁边的那个setting嘛
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);//super一般是指父类的对象
    }

    /**
     * A placeholder fragment containing a simple view.
     * 大概这里就是要改的界面了吧……
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            /*
            int number = Integer.parseInt(ARG_SECTION_NUMBER);
            View rootView;
            switch (number) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                default:
                   rootView = inflater.inflate(R.layout.fragment_main, container, false);//更改页面布局
                //View rootView = thisismyview;
            }
            */
            View rootView = inflater.inflate(R.layout.layout06_main, container, false);//更改页面布局
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((TimeBubMain) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = "";
            String result = msg.getData().getString("result");
            if (result.equals("timeout")) {
                tools.makeToast(Public_Enum.timeoutMsg);
                return;
            }
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = (JSONObject) tokener.nextValue();
                String status = object.getString("status");
                if (status.equals("0")) {
                    if(msg.what==1){
                        JSONObject dataObj=object.getJSONObject("data");
                        latestVer=dataObj.getString("lastestVersion");
                        latestVerInfo=dataObj.getString("updateInfo");
                        if(!tools.getCurrentVer().equals(latestVer))
                            updateDialog(latestVer.split(" ")[0],latestVerInfo);
                    }
                    String temdata = object.getString("data");
                    if (temdata.equals("success")) {
                        tools.makeToast("本次惩罚为扣除积分100，经验值将清零");
                        share.saveData("lastState", "normal");
                        return;
                    }
                }
            } catch (JSONException jE) {
                jE.printStackTrace();
            }
//            tools.makeToast(data);
            super.handleMessage(msg);
        }
    };

    private void changeFragment(int number) {
        switch (number) {
            case 1:
                FragmentManager fragmentManager = getSupportFragmentManager();//获取fragmentmangager
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new TimeBub_Frag_Main())//替换container
                        .addToBackStack(null)
                        .commit();
                break;
            case 2:
                fragmentManager = getSupportFragmentManager();//获取fragmentmangager
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new TimeBub_Frag_UserInfo())//替换container
                        .addToBackStack(null)
                        .commit();
                break;
            case 3:
                fragmentManager = getSupportFragmentManager();//获取fragmentmangager
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new TimeBub_Frag_Option())//替换container
                        .addToBackStack(null)
                        .commit();
                break;

            case 4:
                fragmentManager = getSupportFragmentManager();//获取fragmentmangager
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new TimeBub_Frag_Help())//替换container
                        .addToBackStack(null)
                        .commit();
                break;

            case 5:
                fragmentManager = getSupportFragmentManager();//获取fragmentmangager
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new TimeBub_Frag_Feedback())//替换container
                        .addToBackStack(null)
                        .commit();
                break;

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo =
                pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mNavigationDrawerFragment.isDrawerOpen()) {
                mNavigationDrawerFragment.closeDrawer();
                return true;
            }
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
            startActivitySafely(startIntent);
            return true;
        } else if(keyCode==KeyEvent.KEYCODE_MENU){
            if(mNavigationDrawerFragment.isDrawerOpen()) {
                mNavigationDrawerFragment.closeDrawer();
                return true;
            }
            else {
                mNavigationDrawerFragment.openDrawer();
                return true;
            }
        }  else
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

    protected void updateDialog(String ver,String infomation){
        final String lVer=ver;
        AlertDialog.Builder builder=new AlertDialog.Builder(TimeBubMain.this);
//        builder.setIcon(getResources().getDrawable(R.drawable.logo));
        builder.setTitle("泡泡叫你来升级啦");
        builder.setMessage("版本:" + ver + "\n更新日志:" + infomation);

        progressDialog=new ProgressDialog(TimeBubMain.this);
        progressDialog.setMessage("泡泡正在升级");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String fileName1 = "timeBub_V" + lVer + ".apk";
                File file1 = new File(Environment.getExternalStorageDirectory(), fileName1);
                if(tools.fileIsExists(file1)){
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

    public void onDestroy() {
        super.onDestroy();

        //解除绑定
        unbindService(conn);
    }
}
