package com.pucmasterapps.pucsportmaster;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class RunnerInfo extends Activity {
    private Integer position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_info);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        DbHElper dbHelper = new DbHElper(MainActivity.self);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        position = position + 1;
        // create cursor
        Cursor cursor = db.rawQuery("SELECT * FROM runner WHERE _id=" + position, null);
        cursor.moveToFirst();

        String Ustring;
        if((Ustring = cursor.getString(cursor.getColumnIndex("username"))) != null) {
            TextView username = ((TextView) findViewById(R.id.RunnerNameValue));
            username.setText(Ustring);
            ImageView iv = (ImageView) findViewById(R.id.imageView13);
            if (Ustring.matches(".*[a]$")) {
                iv.setImageResource(R.drawable.female);
            } else {
                iv.setImageResource(R.drawable.male);
            }
        } else {
            ((TextView) findViewById(R.id.RunnerNameValue)).setText("no info");
        }
        String date;
        if((date = cursor.getString(cursor.getColumnIndex("pub_date"))) != null) {
            String[] dateStr = date.split("T");
            ((TextView) findViewById(R.id.RunnerDateValue)).setText(dateStr[0]);
            ((TextView) findViewById(R.id.PostTimeValue)).setText(dateStr[1]);
        }
        else {
            ((TextView) findViewById(R.id.RunnerDateValue)).setText("no info");
            ((TextView) findViewById(R.id.PostTimeValue)).setText("no info");
        }
        String duration;
        if((duration = cursor.getString(cursor.getColumnIndex("duration"))) != null) {
            ((TextView) findViewById(R.id.RunnerDurationValue)).setText(duration);
        } else {
            ((TextView) findViewById(R.id.RunnerDurationValue)).setText("no info");
        }
        String distance;
        if((distance = cursor.getString(cursor.getColumnIndex("distance")))!= null) {
            ((TextView) findViewById(R.id.RunnerDistanceValue)).setText(distance);
        } else {
            ((TextView) findViewById(R.id.RunnerDistanceValue)).setText("no info");
        }
        String runner;
        if((runner = cursor.getString(cursor.getColumnIndex("_id")))!= null) {
            ((TextView) findViewById(R.id.RunnerIdValue)).setText(runner);

        } else {
            ((TextView) findViewById(R.id.RunnerIdValue)).setText("no info");
        }
        String isPucSportMaster;
        if((isPucSportMaster = cursor.getString(cursor.getColumnIndex("isPucSportMaster")))!= null){
            if(isPucSportMaster.matches("1")){
                ( (TextView)findViewById(R.id.pucSport)).setText("PucSportMaster user");
            } else {
                ( (TextView)findViewById(R.id.pucSport)).setText("No PucSportMaster user");
            }
        }
        String MaxSpeed;
        if((MaxSpeed = cursor.getString(cursor.getColumnIndex("MaxSpeed")))!= null){

                ( (TextView)findViewById(R.id.MaxSpeedValue)).setText(MaxSpeed);
            } else {
                ( (TextView)findViewById(R.id.MaxSpeedValue)).setText("No info");
            }

        String Calories;
        if((Calories = cursor.getString(cursor.getColumnIndex("Calories")))!= null){

                ( (TextView)findViewById(R.id.CaloriesValue)).setText(Calories);
            } else {
                ( (TextView)findViewById(R.id.CaloriesValue)).setText("No info");
            }

        String TotalDistance;
        if((TotalDistance = cursor.getString(cursor.getColumnIndex("total_distance")))!= null){

                ( (TextView)findViewById(R.id.TotalDistanceRunValue)).setText(TotalDistance);
            } else {
                ( (TextView)findViewById(R.id.TotalDistanceRunValue)).setText("No info");
            }

        String TotalCal;
        if((TotalCal = cursor.getString(cursor.getColumnIndex("total_calories")))!= null){

                ( (TextView)findViewById(R.id.TotalCaloriesRunValue)).setText(TotalCal);
            } else {
                ( (TextView)findViewById(R.id.TotalCaloriesRunValue)).setText("No info");
            }

        String runCounts;
        if((runCounts = cursor.getString(cursor.getColumnIndex("average_pace")))!= null){

                ( (TextView)findViewById(R.id.NumberOfRunsValue)).setText(runCounts);
            } else {
                ( (TextView)findViewById(R.id.NumberOfRunsValue)).setText("No info");
            }

       //pass text, email text, MaxSpeed INTEGER, Calories INTEGER,total_distance INTEGER, total_time INTEGER, total_calories INTEGER , average_pace INTEGER)");
        //    db.execSQL(SQL_CREATE_FORECAST);
        cursor.close();
        db.close();
       /* time = cursor.getString(cursor.getColumnIndex("pub_date"));
        if(username.matches(".*[a]$")){
            iv.setImageResource(R.drawable.female);
        } else {
            iv.setImageResource(R.drawable.male);
        }
        tv1.setText(username);
        tv2.setText(time);
        cursor.moveToNext();*/
    }




}
