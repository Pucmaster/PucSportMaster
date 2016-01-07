package com.pucmasterapps.pucsportmaster;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.CharMatcher;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

    }

    public boolean check = false;

    public void onFinish(View view) {
        check = false;

        EditText Editemail = (EditText) findViewById(R.id.RegeditText);
        String email = Editemail.getText().toString();
        if (CharMatcher.is('@').countIn(email) == 1 && CharMatcher.is('.').countIn(email) > 0) {
            EditText EditPass = (EditText) findViewById(R.id.RegeditText2);
            String pass = EditPass.getText().toString();
            EditText Editusername = (EditText) findViewById(R.id.RegeditText3);
            String username = Editusername.getText().toString();
            DbHElper dbHelper = new DbHElper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM runner ORDER BY _id", null);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                String username1;
                if((username1 = cursor.getString(cursor.getColumnIndex("username")))!= null) {
                    if (username.matches(username1)) {
                        Toast.makeText(this, R.string.alreadyexist, Toast.LENGTH_SHORT).show();
                        check = true;
                        break;
                    }
                    String email1;
                    if ((email1 = cursor.getString(cursor.getColumnIndex("email"))) != null) {
                        if (email.matches(email1)) {
                            Toast.makeText(this, R.string.emailexist, Toast.LENGTH_SHORT).show();
                            check = true;
                            break;
                        }
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            if (check == false) {

                DbHElper dbHelper1 = new DbHElper(this);
                SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
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
                cvalues.put("username", username);
                cvalues.put("pass", pass);
                cvalues.put("email", email);
                cvalues.put("email", email);
                cvalues.put("isPucSportMaster", 1);
                cvalues.put("average_pace", 0);
                cvalues.put("total_distance", 0);
                cvalues.put("total_calories", 0);
                db1.insert("runner", null, cvalues);
                db1.close();
                finish();
            }
        } else {
            Toast.makeText(this, R.string.wrongemail, Toast.LENGTH_SHORT).show();
        }
    }
}

