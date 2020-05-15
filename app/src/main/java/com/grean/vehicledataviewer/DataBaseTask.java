package com.grean.vehicledataviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by weifeng on 2020/5/11.
 */

public class DataBaseTask extends SQLiteOpenHelper{
    public DataBaseTask(Context context, int version) {
        super(context, "data.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE device_setting(id INTEGER PRIMARY KEY AUTOINCREMENT,factory_setting INTEGER,content TEXT)");//设置
        db.execSQL("CREATE TABLE track_cache(id INTEGER PRIMARY KEY AUTOINCREMENT,date LONG,tvoc FLOAT,lat DOUBLE,lng DOUBLE)");//走航数据
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
