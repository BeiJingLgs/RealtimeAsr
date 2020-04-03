package com.hanvon.speech.realtime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {

    //数据库名字
    private static final String DB_NAME = "note.db";

    //本版号
    private static final int VERSION = 1;

    //创建表
    private static final String CREATE_TABLE_NOTE = "CREATE TABLE note(_id integer primary key autoincrement," +
            "title text, " +
            "json text, " +
            "content text, " +
            "createtime text," +
            "modifytime text," +
            "sdcard text," +
            "createmillis long," +
            "duration int,"  +
            "time long)";

    public static final String USER_TABLE_NAME = "thirdApp";
    private static final  String CREATE_TABLE_THIRD = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + " third TEXT)";

    //删除表
    private static final String DROP_TABLE_NOTE = "drop table if exists note";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQLiteDatabase 用于操作数据库的工具类
        db.execSQL(CREATE_TABLE_NOTE);
        //db.execSQL(CREATE_TABLE_THIRD);

        // 初始化两个表的数据(先清空两个表,再各加入一个记录)
        //db.execSQL("delete from " + DatabaseHelper.USER_TABLE_NAME);
        //db.execSQL("insert into " + DatabaseHelper.USER_TABLE_NAME + " values(1,999);");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_NOTE);
        //db.execSQL(CREATE_TABLE_THIRD);
        //db.execSQL(CREATE_TABLE_THIRD);
    }
}
