package com.example.control;

/**
 * Created by shanlihou on 15-4-28.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Map;

/**
 * Created by Administrator on 2015/1/17 0017.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "control.db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME_USER = "user";
    private final static String TABLE_NAME_SEARCH = "search";
    public final static String FIELD_ID = "id";
    public final static String FIELD_NAME = "name";
    public final static String FIELD_PASSWORD = "pass";
    public final static String FIELD_CODE = "code";
    public final static String FIELD_DATE = "date";
    public final static String FIELD_OBJID = "objId";
    private static DBHelper instance = null;
    private Context mContext;
    private DBHelper(){
        super(MainApplication.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("shanlihou", "init db");
    }

    public static DBHelper getInstance() {
        if (instance == null){
            instance = new DBHelper();
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + TABLE_NAME_USER + "("
                + FIELD_NAME + " varchar(100) NOT NULL PRIMARY KEY,"
                + FIELD_PASSWORD + " varchar(100));";
        db.execSQL(sql);
        sql = "Create table " + TABLE_NAME_SEARCH + "("
                + FIELD_NAME + " varchar(100),"
                + FIELD_CODE + " varchar(100),"
                + FIELD_DATE + " varchar(100),"
                + FIELD_OBJID + " varchar(100),"
                + "PRIMARY KEY (" + FIELD_OBJID + "));";
        db.execSQL(sql);
    }
    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = " DROP TABLE IF EXISTS " + TABLE_NAME_USER;
        db.execSQL(sql);
        sql = " DROP TABLE IF EXISTS " + TABLE_NAME_SEARCH;
        db.execSQL(sql);
        onCreate(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = " DROP TABLE IF EXISTS " + TABLE_NAME_USER;
        db.execSQL(sql);
        sql = " DROP TABLE IF EXISTS " + TABLE_NAME_SEARCH;
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor query() {
        SQLiteDatabase db;
        Cursor cursor;
        db = this.getReadableDatabase();
        cursor = db.query(TABLE_NAME_USER, null, null, null, null, null, null);
        return cursor;
    }
    public long insert(String name, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        long ret;
        ret = db.insert(TABLE_NAME_USER, null, createValues(name, pass));
        return ret;
    }
    public long insert(Map<String, String> map){
        SQLiteDatabase db = this.getWritableDatabase();
        long ret;
        ret = db.insert(TABLE_NAME_SEARCH, null, createSearchValues(map));
        return ret;
    }

    public void delete(String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_NAME + "=?";
        String[] whereValue = {
                location
        };
        db.delete(TABLE_NAME_USER, where, whereValue);
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_USER, null, null);
    }


    public void showDB(int id){
        Cursor cursor = query();
        if (cursor.moveToFirst()){
            do{
                String key = cursor.getString(1);
                String value = cursor.getString(2);
                Log.d("shanlihou", key + ":" + value);
            }while(cursor.moveToNext());
        }
    }

    private ContentValues createValues(String name, String pass) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_NAME, name);
        cv.put(FIELD_PASSWORD, pass);
        return cv;
    }
    private ContentValues createSearchValues(Map<String, String> map){
        ContentValues cv = new ContentValues();
        cv.put(FIELD_NAME, map.get("name"));
        cv.put(FIELD_CODE, map.get("search"));
        cv.put(FIELD_DATE, map.get("date"));
        cv.put(FIELD_OBJID, map.get("objId"));
        return cv;
    }
}

