package com.camera;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDBHelper extends SQLiteOpenHelper {

    // 步骤1：设置常数参量
    private static final String DATABASE_NAME = "image_db";
    private static final int VERSION = 1;

    // 步骤2：重载构造方法
    public SqliteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /*
     * 参数介绍：context 程序上下文环境 即：XXXActivity.this
     * name 数据库名字
     * factory 接收数据，一般情况为null
     * version 数据库版本号
     */
    public SqliteDBHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }
    //数据库第一次被创建时，onCreate()会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String CREATE_TABLE_STUDENT="CREATE TABLE "+ SqlImage.TABLE_Name+"("
                +SqlImage.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +SqlImage.KEY_name+" TEXT, "
                +SqlImage.KEY_type+" TEXT, "
                +SqlImage.KEY_size+" TEXT, "
                +SqlImage.KEY_changetime+" TEXT, "
                +SqlImage.KEY_path+" TEXT)";
        db.execSQL(CREATE_TABLE_STUDENT);
    }
    //数据库版本变化时，会调用onUpgrade()
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果旧表存在，删除，所以数据将会消失
        db.execSQL("DROP TABLE IF EXISTS "+ SqlImage.TABLE_Name);
        //再次创建表
        onCreate(db);
    }
}