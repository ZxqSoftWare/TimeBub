package com.timebub.qz.timebub;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.timebub.qz.adapter.RecordList;
import com.timebub.qz.timebubtools.RecordInfo;
import com.timebub.qz.timebubtools.TimeBubTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zxqso on 2015/10/30.
 */
public class TimeBubUserRecord extends Activity {
    ListView recordList;
    String fromDate;
    String toDate;
    TimeBubTools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout20_learningrecord);
        String dateend;
        String dateStart;
        Date endDate=null;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        dateend=dft.format(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 30);
        try {
            endDate = dft.parse(dft.format(date.getTime()));

        }catch (ParseException pe){
            pe.printStackTrace();
        }
        dateStart=dft.format(endDate);
        tools = new TimeBubTools(this);
        recordList = (ListView) findViewById(R.id.listView2);
        fromDate = dateStart+"%2000:00:00";
        toDate = dateend+"%2023:59:59";
        Thread getR = new Thread(new getStudyRecord());
        getR.start();
    }

    class getStudyRecord extends Thread {
        @Override
        public void run() {
            HTTPProcess httpProcess = new HTTPProcess(TimeBubUserRecord.this);
            String result = httpProcess.getUserStudyRecord(fromDate, toDate);
            Message msg = new Message();
            Bundle bdl = new Bundle();
            bdl.putString("record", result);
            msg.setData(bdl);
            handler.sendMessage(msg);
            super.run();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("record");
            List<RecordInfo> infos = new ArrayList<RecordInfo>();
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = (JSONObject) tokener.nextValue();
                String status = object.getString("status");
                if (status.equals("0")) {
                    JSONArray array = object.getJSONArray("data");
                    JSONObject dataobject;
                    for (int i = 0; i < array.length(); i++) {
                        String tem=array.getJSONObject(i).toString();
//                        if(array.getJSONObject(i).toString().contains("successCount")) {
                            RecordInfo info = new RecordInfo();
                            info.setStudyFailedTimes(array.getJSONObject(i).getString("failedCount"));
                            info.setStudyDate(array.getJSONObject(i).getString("studyDate"));
                            info.setStudySucceedTimes(array.getJSONObject(i).getString("successCount"));
                            infos.add(info);
//                        }
                    }
                    if (infos.size() != 0)
                        recordList.setAdapter(new RecordList(TimeBubUserRecord.this, infos));
                    else tools.makeToast("学习记录为空");
                }
            } catch (JSONException jE) {
                jE.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };
}
