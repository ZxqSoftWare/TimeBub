package com.timebub.qz.timebud;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.timebub.qz.applock.AppInfo;
import com.timebub.qz.applock.AppLockDao;
import com.timebub.qz.timebub.R;
import com.timebub.qz.timebubtools.TimeBubTools;

import java.util.List;

/**
 * Created by zxqso on 2015/10/15.
 */
public class AppListItem extends BaseAdapter {

    Context context;
    List<AppInfo> appInfoList;
    LayoutInflater inflater;
    ImageView appIcon;
    TextView txtpackage;
    TextView txtName;
    CheckBox isSelected;
    TimeBubTools tools;

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
        txtName=(TextView)view.findViewById(R.id.nameItem);
        txtpackage=(TextView)view.findViewById(R.id.titleItem);
        appIcon=(ImageView)view.findViewById(R.id.imageItem);
        isSelected=(CheckBox)view.findViewById(R.id.checkItem);
        appIcon.setImageDrawable(appInfoList.get(i).getAppIcon());
        txtpackage.setText(appInfoList.get(i).getPackageName());
        txtName.setText(appInfoList.get(i).getAppName());
        tools=new TimeBubTools(context);
        final String txtPackage=appInfoList.get(i).getPackageName();
        final String appname=appInfoList.get(i).getAppName();
        AppLockDao lockDao=new AppLockDao(this.context);
        final List<String> appLocked=lockDao.findAll();
        if(appLocked.contains(appInfoList.get(i).getPackageName())){
            isSelected.setChecked(true);
        }
        isSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Uri uri=Uri.parse("content://com.timebub.qz.applock/ADD");
                    ContentValues values=new ContentValues();
                    values.put("packname", txtPackage);
                    context.getContentResolver().insert(uri, values);
//                    isSelected.setSelected(true);
                    tools.makeToast("新增了" + appname);
                    appLocked.add(txtPackage);
                }else
                {
                    Uri uri=Uri.parse("content://com.timebub.qz.applock/DELETE");
                    context.getContentResolver().delete(uri,null,new String[]{txtPackage});
//                    isSelected.setSelected(false);
                    tools.makeToast("取消了" + appname);
                    appLocked.remove(txtPackage);
                }
            }
        });
        return view;
    }
}
