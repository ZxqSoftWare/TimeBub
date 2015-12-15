package com.timebub.qz.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timebub.qz.timebub.R;
import com.timebub.qz.timebubtools.RecordInfo;

import java.util.List;

/**
 * Created by zxqso on 2015/10/30.
 */
public class RecordList extends BaseAdapter {
    Context context;
    List<RecordInfo> info;
    TextView txtDate;
    TextView txtSucceedTimes;
    TextView txtFailedTimes;
    public RecordList(Context context,List<RecordInfo> list){
        info=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Object getItem(int i) {
        return info.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=View.inflate(context, R.layout.history_record_item,null);
        txtDate=(TextView)view.findViewById(R.id.learn_date);
        txtSucceedTimes=(TextView)view.findViewById(R.id.succes_num);
        txtFailedTimes=(TextView)view.findViewById(R.id.fail_num);
        txtDate.setText(info.get(i).getStudyDate());
        txtSucceedTimes.setText("成功学习"+info.get(i).getStudySucceedTimes()+"次");
        txtFailedTimes.setText("失败"+info.get(i).getStudyFailedTimes()+"次");
        return view;
    }
}
