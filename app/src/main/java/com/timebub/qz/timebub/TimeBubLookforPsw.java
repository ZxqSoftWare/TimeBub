package com.timebub.qz.timebub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zxqso on 2015/10/18.
 */
public class TimeBubLookforPsw extends Activity {
    String phone;
    String password;
    String username;
    EditText phoneNum;
    EditText confirmCode;
    EditText passWord;
    EditText confirmPw;
    Button getconfirmCode;
    Button register;
    ImageButton backToLogin;
    TimeBubTools tools;
    HTTPProcess httpProcess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout03_retrievepwd);
        phoneNum = (EditText) findViewById(R.id.phone_email);
        confirmCode = (EditText) findViewById(R.id.IDnum);
        passWord = (EditText) findViewById(R.id.pwd);
        confirmPw = (EditText) findViewById(R.id.confirmpwd);
        getconfirmCode = (Button) findViewById(R.id.sendID);
        register = (Button) findViewById(R.id.confirm_pwd);
        backToLogin = (ImageButton) findViewById(R.id.back_to_login1);
        tools = new TimeBubTools(this);
        httpProcess = new HTTPProcess(TimeBubLookforPsw.this);
        SMSSDK.initSDK(TimeBubLookforPsw.this, "b55c20e6e5a6", "5871b2800595387c3fb772e5cce5b0e9");
        SMSSDK.registerEventHandler(eh);
        getconfirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNum.getText().toString().equals("")||phoneNum.getText().toString().length()!=11) {
                    tools.makeToast("请正确输入手机号");
                    return;
                }
                SMSSDK.getVerificationCode("86",phoneNum.getText().toString());
                tools.makeToast("正在尝试发送验证码");
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNum.getText().toString().equals("")) {
                    tools.makeToast("请输入手机号");
                    return;
                }
                if (confirmCode.getText().toString().equals("")) {
                    tools.makeToast("请输入验证码");
                }
                if (passWord.getText().toString().equals("")) {
                    tools.makeToast("请输入密码");
                    return;
                }
                if (!confirmPw.getText().toString().equals(passWord.getText().toString())) {
                    tools.makeToast("两次输入的密码不同，请重新输入");
                    return;
                }
                phone=phoneNum.getText().toString();
                password=passWord.getText().toString();
                SMSSDK.submitVerificationCode("86",phoneNum.getText().toString(),confirmCode.getText().toString());
//                Thread reg=new Thread(new regNewUser());
//                reg.start();
//                if(!result.equals("")){
//                    Log.i("register","success");
//                }
            }
        });
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimeBubLookforPsw.this,TimeBubLogin.class);
                startActivity(intent);
            }
        });

    }


    public Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result =msg.getData().getString("status");
            if(result!=""){
                httpProcess=new HTTPProcess(TimeBubLookforPsw.this);
                tools=new TimeBubTools(TimeBubLookforPsw.this);
                try {
                    JSONTokener tokener = new JSONTokener(result);
                    JSONObject object = (JSONObject) tokener.nextValue();
                    String userid=object.getString("status");
                    if(userid.equals("0")) {
                        tools.makeToast("修改成功");
                        Intent intent = new Intent(TimeBubLookforPsw.this,TimeBubLogin.class);
                        startActivity(intent);
                    }else{
                        tools.makeToast("修改失败失败，请检查手机号码及验证码是否正确");
                    }
                }catch (JSONException je){
                    je.printStackTrace();
                }

//                tools.makeToast("1" + result);

            }
            super.handleMessage(msg);
        }
    };


    Handler toastHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result=msg.getData().getString("result");
            if(result.equals("提交成功")){
                Thread reg=new Thread(new regNewUser());
                reg.start();;
            }
//            tools.makeToast(result);
            super.handleMessage(msg);
        }
    };


    class regNewUser extends Thread{
        @Override
        public void run() {
            httpProcess=new HTTPProcess(TimeBubLookforPsw.this);
            String result=httpProcess.lookPsw(phone,password);
            Message msg=new Message();
            Bundle bdl=new Bundle();
            bdl.putString("status",result);
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }

    EventHandler eh=new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Throwable throwable;
            String masg="";
            Message msg = new Message();
            Bundle bdl = new Bundle();
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    masg="提交成功";

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    masg= "验证码已发送";
                }
            } else {
                throwable=(Throwable)data;
                masg=throwable.getMessage();
            }
            bdl.putString("result", masg);
            msg.setData(bdl);
            toastHandler.sendMessage(msg);
        }
    };
}
