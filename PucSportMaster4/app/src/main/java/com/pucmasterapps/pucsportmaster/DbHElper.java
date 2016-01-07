package com.pucmasterapps.pucsportmaster;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jakub on 05.01.2016.
 */
public class DbHElper extends SQLiteOpenHelper
{
    private static final String TAG = "DbHelper";
    public static final int DATABASE_VERSION = 6;
    private static final String SQL_CREATE_RUNNER = "CREATE TABLE runner " + "(id integer primary key, name text,phone text,email text, street text,place text)";
    //);
           /* "_id INTEGER PRIMARY KEY, " , "pub_date DATETIME, " +
            "username TEXT, " +
            "duration INTEGER, " +
            "distance INTEGER, " +
            ")";*/
 /*  private static final String SQL_CREATE_FORECAST = "CREATE TABLE runners (" +
            "_id INTEGER PRIMARY KEY, " +
            "pub_date DATETIME UNIQUE NOT NULL, " +
            "username TEXT, " +
            "duration INTEGER, " +
            "duration INTEGER, " +
            "FOREIGN KEY(city_id) REFERENCES city(id))";*/


    public DbHElper(Context context) {
        super(context, "runnersDatabase.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate()");

        db.execSQL("CREATE TABLE runner " + "(_id INTEGER PRIMARY KEY, username text,pub_date text, duration INTEGER, distance INTEGER, isPucSportMaster INTEGER,pass text, email text, MaxSpeed INTEGER, Calories INTEGER,total_distance INTEGER, total_time INTEGER, total_calories INTEGER , average_pace INTEGER)");
    //    db.execSQL(SQL_CREATE_FORECAST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, String.format("onUpgrade(): old: %d, new: %d", oldVersion, newVersion));

        db.execSQL("DROP TABLE IF EXISTS runner");
    //    db.execSQL("DROP TABLE IF EXISTS forecast");
        onCreate(db);
    }
}