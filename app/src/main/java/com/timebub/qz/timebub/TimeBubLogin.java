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

import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by zxqso on 2015/10/18.
 */
public class TimeBubLogin extends Activity {
    String txtPhone;
    String password;
    EditText txtUsername;
    EditText txtPassword;
    Button btnConfirm;
    Button btnForgotPwd;
    ImageButton btn2Reg;
    TimeBubTools tools;
    HTTPProcess httpProcess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout02_login);
        txtUsername=(EditText)findViewById(R.id.name);
        txtPassword=(EditText)findViewById(R.id.password);
        btnConfirm=(Button)findViewById(R.id.confirm);
        btnForgotPwd=(Button)findViewById(R.id.forgotpwd);
        btn2Reg=(ImageButton)findViewById(R.id.back);
        tools=new TimeBubTools(TimeBubLogin.this);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtUsername.getText().toString().equals("")){
                    tools.makeToast("请输入手机号码");
                    return;
                }
                if(txtPassword.getText().toString().equals("")){
                    tools.makeToast("请输入密码");
                    return;
                }
                txtPhone=txtUsername.getText().toString();
                password=txtPassword.getText().toString();
                Thread userLogin=new Thread(new login());
                userLogin.start();
            }
        });
        btnForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TimeBubLogin.this,TimeBubLookforPsw.class);
                startActivity(intent);
                finish();
            }
        });
        btn2Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TimeBubLogin.this,TimeBubReg.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result =msg.getData().getString("status");
            if(result!=""){
                httpProcess=new HTTPProcess(TimeBubLogin.this);
                tools=new TimeBubTools(TimeBubLogin.this);
                try {
                    JSONTokener tokener = new JSONTokener(result);
                    JSONObject object = (JSONObject) tokener.nextValue();
                    String userid=object.getString("status");
                    JSONObject userdata=object.getJSONObject("data");
                    
                    if(userid.equals("0")) {
//                        tools.makeToast("登陆成功"+userdata.getString("userId"));
                        tools.makeToast("登录成功");
//                        Intent intent = new Intent(TimeBubLogin.this,TimeBubLogin.class);
                        Public_Enum.userID=userdata.getString("userId");
                        Public_Enum.userToken=userdata.getString("userToken");
                        TimeBubSharePreference sharePreference=new TimeBubSharePreference(TimeBubLogin.this);
                        sharePreference.saveData("userID",Public_Enum.userID);
                        sharePreference.saveData("userToken",Public_Enum.userToken);
                        Intent intent=new Intent(TimeBubLogin.this,TimeBubMain.class);
                        startActivity(intent);
                        finish();
                    }else{
                        tools.makeToast("登录失败，请检查手机号码及密码是否正确");
                    }
                }catch (JSONException je){
                    je.printStackTrace();
                }

//                tools.makeToast("1" + result);

            }
            super.handleMessage(msg);
        }
    };

    class login extends Thread{
        @Override
        public void run() {
            httpProcess=new HTTPProcess(TimeBubLogin.this);
            String result=httpProcess.userLogin(txtPhone,password);
            Message msg=new Message();
            Bundle bdl=new Bundle();
            bdl.putString("status",result);
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }
}
