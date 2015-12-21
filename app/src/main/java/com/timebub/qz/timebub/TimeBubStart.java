package com.timebub.qz.timebub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zxqso on 2015/10/18.
 */
public class TimeBubStart extends Activity {
    Button btnLogin;
    Button btnReg;
    TimeBubSharePreference share;
    LinearLayout startbtn;
    HTTPProcess httpProcess;
    TimeBubTools tools;
    String lastStudyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout01_start);
        share = new TimeBubSharePreference(this);
        String userid = share.getData("userID");
        String userToken = share.getData("userToken");
        lastStudyState=share.getData("lastState");
        tools=new TimeBubTools(this);
        httpProcess = new HTTPProcess(this);
//        if(lastStudyState.equals("Not Found")||lastStudyState.equals("normal")){
//            share.saveData("lastState","normal");
//        }else{
//            tools.makeToast("检测到您上次强行退出，我们将对您进行惩罚");
//            Thread checkLgn=new Thread(new shutDownApp());
//            checkLgn.start();
////            share.saveData("lastState", "normal");
//        }
        startbtn = (LinearLayout) findViewById(R.id.startbtn);
        if ((!userid.equals("Not Found")&&!userid.equals("")) && (!userToken.equals("Not Found")&&!userToken.equals(""))) {
            Public_Enum.userID=userid;
            Public_Enum.userToken=userToken;
            Thread checkLgn=new Thread(new checkLogin());
            checkLgn.start();
            startbtn.setVisibility(View.GONE);
        }

        btnLogin = (Button) findViewById(R.id.login);

        btnReg = (Button) findViewById(R.id.register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimeBubStart.this, TimeBubLogin.class);
                startActivity(intent);
                finish();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimeBubStart.this, TimeBubReg.class);
                startActivity(intent);
                finish();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data="";
            String result = msg.getData().getString("result");
            if(result.equals("timeout")){
                tools.makeToast(Public_Enum.timeoutMsg);
                Timer timer=new Timer();
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(TimeBubStart.this,TimeBubMain.class);
                        startActivity(intent);
                        finish();
                    }
                };
                timer.schedule(task, 2000);
                return;
            }
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = (JSONObject) tokener.nextValue();
                String status=object.getString("status");
                if(status.equals("0")){
                    String temdata=object.getString("data");
                    if(temdata.equals("success")){
                        tools.makeToast("本次惩罚为扣除积分100，经验值将清零");
                        share.saveData("lastState", "normal");
                        return;
                    }
                    if(temdata.equals("0")){
                        data = "当前会话已经过期，请重新登录";
                        tools.makeToast("当前会话已经过期，请重新登录");
                        startbtn.setVisibility(View.VISIBLE);
                    }
                    if(temdata.equals("1")) {
                        data = "验证成功";
                        tools.makeToast("自动登录成功");
                        Timer timer=new Timer();
                        TimerTask task=new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(TimeBubStart.this,TimeBubMain.class);
                                startActivity(intent);
                                finish();
                            }
                        };
                        timer.schedule(task, 2000);
                    }
                }
            }catch (JSONException jE){
                jE.printStackTrace();
            }
//            tools.makeToast(data);
            super.handleMessage(msg);
        }
    };

    class checkLogin extends Thread {
        @Override
        public void run() {
            String result=httpProcess.checkToken();
            Message msg=new Message();
            Bundle bdl=new Bundle();
            bdl.putString("result",result);
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }

    class shutDownApp extends Thread{
        @Override
        public void run() {
            String result=httpProcess.shutDownApp();
            Message msg=new Message();
            Bundle bdl=new Bundle();
            bdl.putString("result",result);
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }



}
