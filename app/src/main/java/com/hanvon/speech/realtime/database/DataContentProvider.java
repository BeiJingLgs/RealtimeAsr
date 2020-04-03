package com.hanvon.speech.realtime.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Carson_Hoon 17/6/6.
 */
public class DataContentProvider extends ContentProvider {

    private Context mContext;
    SQLiteDatabase db = null;
    public static final String AUTOHORITY = "cn.scu.myprovider";
    // 设置ContentProvider的唯一标识

    public static final int User_Code = 1;

    // UriMatcher类使用:在ContentProvider 中注册URI
    private static final UriMatcher mMatcher;

    static {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(AUTOHORITY, DatabaseHelper.USER_TABLE_NAME, User_Code);
    }

    // 以下是ContentProvider的6个方法

    /**
     * 初始化ContentProvider
     */
    @Override
    public boolean onCreate() {
        mContext = getContext();
        return true;
    }

    /**
     * 添加数据
     */

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = getTableName(uri);
        db.insert(table, null, values);
        mContext.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    /**
     * 查询数据
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table = getTableName(uri);
        // 查询数据
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    /**
     * 更新数据
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String table  = getTableName(uri);
        if (table==null){
            throw new IllegalArgumentException("Unsupport URI");
        }
        int row = db.update(table,values,selection,selectionArgs);
        if (row>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return row;
    }

    /**
     * 删除数据
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = getTableName(uri);
        if (table==null){
            throw new IllegalArgumentException("Unsupport URI");
        }
        int count = db.delete(table,selection,selectionArgs);
        if (count>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {

        // 由于不展示,此处不作展开
        return null;
    }

    /**
     * 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
     */
    private String getTableName(Uri uri) {
        String tableName = null;
        switch (mMatcher.match(uri)) {
            case User_Code:
                tableName = DatabaseHelper.USER_TABLE_NAME;
                break;

        }
        return tableName;
    }
}

