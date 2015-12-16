package com.timebub.qz.timebub;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
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
import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);//这个就是找到开始的navigation drawer啦
        //其实还是侧滑菜单那个东西= =
        mTitle = getTitle();//获取action bar的标题

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));//获取drawer_layout的id……【这个到底在哪里啊
        restoreActionBar();
        GetServiceStatus serviceStatus = new GetServiceStatus();
//        mNavigationDrawerFragment.openDrawer();
        tools = new TimeBubTools(this);
        lastStudyState = share.getData("lastState");
        if (serviceStatus.getServiceStatus(TimeBubMain.this, "com.timebub.qz.applock.AppLockService")) {
//            tools.makeToast("服务正在运行");
        } else {
            Intent intent1 = new Intent(TimeBubMain.this, AppLockService.class);
            startService(intent1);
//            tools.makeToast("服务启动成功");

        }
        if (lastStudyState.equals("Not Found") || lastStudyState.equals("normal")) {
            share.saveData("lastState", "normal");
        } else {
            tools.makeToast("曾经的强退带来了今天的惩罚，接招吧！");
            Thread checkLgn = new Thread(new shutDownApp());
            checkLgn.start();
//            share.saveData("lastState", "normal");
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
}
