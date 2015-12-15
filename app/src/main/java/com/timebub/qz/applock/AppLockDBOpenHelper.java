package com.timebub.qz.applock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zxqso on 2015/10/15.
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper {
    public AppLockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    /**
     * 数据库第一次被创建的时候执行该方法 在该方法中，一般用于指定数据库的表结构
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock (_id integer primary key autoincrement, packname varchar(20))");
    }

    /**
     * 当数据库的版本号 发生增加的时候调用的方法. 一般用于升级程序后,更新数据库的表结构.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
