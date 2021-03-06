package com.hanvon.speech.realtime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.hanvon.speech.realtime.bean.FileBean;

import java.util.ArrayList;
import java.util.logging.Logger;


public class DatabaseUtils {

    private DatabaseHelper dbHelper;

    private static final String TAG = "DatabaseUtils ";

    private static Logger logger = Logger.getLogger("DatabaseUtils");

    private DatabaseUtils(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static DatabaseUtils getInstance(Context context) {
        return new DatabaseUtils(context);
    }

    /**
     * 添加数据
     *
     * @param note
     */
    //"title text,content text,createtime text,modifytime text,createmillis long)
    public void insert(FileBean note) {
        logger.info("insert ： " + note.title);
        String sql = "insert into note(title, json, content, createtime, modifytime, createmillis, duration, time, sign)values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] args = {note.title, note.json, note.content, note.createtime, note.modifytime, note.createmillis, note.duration, note.time, note.sign};
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(sql, args);
        db.close();
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public void deleteBymillis(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "delete from note where createmillis = ?";
        Object[] args = {id};
        db.execSQL(sql, args);
        db.close();
    }



    /**
     * 查询所有
     *
     * @return
     */
    public ArrayList<FileBean> findAll() {
        logger.info("findAll ： " );
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select * from note order by _id desc";
        Cursor cursor = db.rawQuery(sql,null);


        ArrayList<FileBean> notes = new ArrayList<FileBean>();
        FileBean note = null;
        while (cursor.moveToNext()) {
            note = new FileBean();
            note.title = cursor.getString(1);
            note.content = cursor.getString(3);
            note.json = cursor.getString(2);
            note.createtime = cursor.getString(4);
            note.modifytime = cursor.getString(5);
            note.mSd = cursor.getString(6);
            note.createmillis = cursor.getString(7);
            note.duration = cursor.getInt(8);
            note.time = cursor.getLong(9);
            note.sign = cursor.getInt(10);
            notes.add(note);
        }
        cursor.close();
        db.close();
        return notes;
    }

    /**
     * 查询所有
     *
     * @return
     */
    public ArrayList<FileBean> queryLocalBookShelfByKeyFromDB(String key) {
        logger.info("queryLocalBookShelfByKeyFromDB ： " );
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select * from note where title " + "like '%"+ key +"%' "+ " order by _id desc";
        Cursor cursor = db.rawQuery(sql,null);


        ArrayList<FileBean> notes = new ArrayList<FileBean>();
        FileBean note = null;
        while (cursor.moveToNext()) {
            note = new FileBean();
            note.title = cursor.getString(1);
            note.content = cursor.getString(3);
            note.json = cursor.getString(2);
            note.createtime = cursor.getString(4);
            note.modifytime = cursor.getString(5);
            note.mSd = cursor.getString(6);
            note.createmillis = cursor.getString(7);
            note.duration = cursor.getInt(8);
            note.time = cursor.getLong(9);
            note.sign = cursor.getInt(10);
            notes.add(note);
        }
        cursor.close();
        db.close();
        return notes;
    }

    public void deleteTable(){
        logger.info("deleteTable ： " );
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("delete from note");
    }

    public void updateByContent(FileBean userBean) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("content", userBean.content);
            contentValues.put("json", userBean.json);
            contentValues.put("modifytime", userBean.modifytime);
            contentValues.put("sdcard", userBean.mSd + "");
            contentValues.put("duration", userBean.duration + "");
            dbHelper.getWritableDatabase().update("note", contentValues
                    , "createmillis=?"
                    , new String[]{userBean.createmillis + ""});
        } catch (Exception ignored) {

        }
    }

    public void updateDurationByContent(FileBean userBean) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("duration", userBean.duration + "");
            dbHelper.getWritableDatabase().update("note", contentValues
                    , "createmillis=?"
                    , new String[]{userBean.createmillis + ""});
        } catch (Exception ignored) {

        }
    }

    public void updateTime(FileBean userBean) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("time", userBean.time);
            dbHelper.getWritableDatabase().update("note", contentValues
                    , "createmillis=?"
                    , new String[]{userBean.createmillis + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTileByMillis(FileBean userBean) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", userBean.title);
            dbHelper.getWritableDatabase().update("note", contentValues
                    , "createmillis=?"
                    , new String[]{userBean.createmillis + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateModifyTime(FileBean userBean) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("modifytime", userBean.modifytime);
            dbHelper.getWritableDatabase().update("note",contentValues
                    , "createmillis=?"
                    , new String[]{userBean.createmillis + ""});
        } catch (Exception ignored) {

        }
    }
}
