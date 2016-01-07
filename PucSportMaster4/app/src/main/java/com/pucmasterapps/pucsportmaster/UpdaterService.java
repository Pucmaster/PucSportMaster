package com.pucmasterapps.pucsportmaster;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";
    private static final String RUNNER_URL = "http://atlantis.cnl.sk:8000/workouts/ ";
    private static final String SQL_INSERT_DATA = "INSERT INTO forecast VALUES (NULL, %d, %d, '%s')";
    private Handler mHandler;
    public UpdaterService() {
        super(TAG);
        Log.i(TAG, "created");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent()");

        try {
            URL url = new URL(String.format(RUNNER_URL));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.connect();

            Log.i(TAG, String.format("Connecting to %s", url.toString()));
            Log.i(TAG, String.format("HTTP Status Code: %d", connection.getResponseCode()));

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Response code is not OK");
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + '\n');
            }

            Log.i(TAG, String.format("GET: %s", stringBuilder.toString()));
            JSONObject json = new JSONObject(stringBuilder.toString());
            JSONArray list = json.getJSONArray("results");

            DbHElper dbHelper = new DbHElper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cvalues = new ContentValues();
     /*       try {
                db.execSQL(String.format("INSERT INTO city VALUES (NULL, '%s', %e, %e)",
                        ThemedSpinnerAdapter.Helper.getCityName(json),
                        ThemedSpinnerAdapter.Helper.getLatitude(json),
                        ThemedSpinnerAdapter.Helper.getLongitude(json)
                ));
            }catch(SQLiteConstraintException e){
                Log.e(TAG, "Trying to insert existing city entry");
            }
*/
            for(int i = 0; i < list.length(); i++){
                JSONObject data = list.getJSONObject(i);
                try {
                    cvalues.put("pub_date",data.getString("pub_date"));
                    cvalues.put("username",data.getString("username"));
                    cvalues.put("duration",data.getInt("duration"));
                    cvalues.put("distance",data.getInt("distance"));
                    db.insert("runner", null, cvalues);


                    /*
                    String query = String.format(SQL_INSERT_DATA, data.getLong("dt"), 1, data.toString());
                    db.execSQL(query);*/
                   /* "_id INTEGER PRIMARY KEY, " +
                            "pub_date DATETIME UNIQUE NOT NULL, " +
                            "username TEXT, " +
                            "duration INTEGER, " +
                            "distance INTEGER, " +*/
                }catch(SQLiteConstraintException e){
                    Log.e(TAG, "Trying to insert existing forecast data");
                }
            }

            db.close();
            dbHelper.close();
            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Forecast Updated")
                            .setContentText("New Data Available");

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notification.build());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}