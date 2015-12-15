package com.timebub.qz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timebub.qz.applock.AppInfo;
import com.timebub.qz.timebub.R;

import java.util.List;

/**
 * Created by zxqso on 2015/10/15.
 */
public class AppListItem extends BaseAdapter {

    Context context;
    List<AppInfo> appInfoList;
    LayoutInflater inflater;
    TextView txtName;

    public AppListItem(Context context, List<AppInfo> appInfoList){
        this.context=context;
        this.appInfoList=appInfoList;
        inflater= LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return this.appInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.appInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=inflater.inflate(R.layout.list_item,null);
        txtName=(TextView)view.findViewById(R.id.titleItem);
        txtName.setText(appInfoList.get(i).getAppName());
        return view;
    }
}
