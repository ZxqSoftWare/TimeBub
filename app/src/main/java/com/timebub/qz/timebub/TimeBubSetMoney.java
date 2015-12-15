package com.timebub.qz.timebub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.timebub.qz.timebubtools.TimeBubTools;

/**
 * Created by zxqso on 2015/10/27.
 */
public class TimeBubSetMoney extends Activity {
    EditText txtMoney;
    Button btnConfirm;
    TimeBubTools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout11_changemoney);
        txtMoney = (EditText) findViewById(R.id.money);
        tools=new TimeBubTools(this);
        btnConfirm=(Button)findViewById(R.id.confirmmoney);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money=txtMoney.getText().toString();
                if(!money.equals("")){
                    if(Double.parseDouble(money)<0.01){
                        tools.makeToast("表辣么小气，最起码也得0.01元吧");
                        return;
                    }
                    if(Double.parseDouble(money)<1){
                        money=String.valueOf(Double.parseDouble(money));
                    }
                    Bundle bundle=new Bundle();
                    bundle.putString("money",money);
                    Intent intent=TimeBubSetMoney.this.getIntent();
                    intent.putExtra("lockingMoney",bundle);
                    TimeBubSetMoney.this.setResult(1, intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        String money=txtMoney.getText().toString();
        if(!money.equals("")){
            if(Double.parseDouble(money)<0.01){
                tools.makeToast("表辣么小气，最起码也得0.01元吧");
                return;
            }
            if(Double.parseDouble(money)<1){
                money=String.valueOf(Double.parseDouble(money));
            }
            Bundle bundle=new Bundle();
            bundle.putString("money",money);
            Intent intent=TimeBubSetMoney.this.getIntent();
            intent.putExtra("lockingMoney",bundle);
            TimeBubSetMoney.this.setResult(1,intent);
            finish();
        }
        super.onBackPressed();
    }
}
