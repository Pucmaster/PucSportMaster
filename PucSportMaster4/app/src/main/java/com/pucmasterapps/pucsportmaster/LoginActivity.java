package com.pucmasterapps.pucsportmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.CharMatcher;

public class LoginActivity extends Activity {
    Context context;
    DbHElper dbHelper;
    SQLiteDatabase db;
    String email;
    String login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puc_sport_master_login);
        context = this;


    }
    public void Procceed(View view){

        dbHelper = new DbHElper(this);
        db = dbHelper.getReadableDatabase();

        EditText Editemail = (EditText)findViewById(R.id.email);
        EditText Editlogin = (EditText)findViewById(R.id.password);

        if(Editemail != null) {
            email = Editemail.getText().toString();
        }
        if(Editlogin != null) {
            login = Editlogin.getText().toString();
        }
        String emailC;
        String loginC;
        boolean checker = false;
        if(CharMatcher.is('@').countIn(email) == 1){
            Cursor cursor = db.rawQuery("SELECT * FROM runner ORDER BY _id", null);
            cursor.moveToFirst();

           while(cursor.isAfterLast() == false){
               if(( emailC = cursor.getString(cursor.getColumnIndex("email")))!= null){
                 if(email.matches(emailC)) {

                   if(( loginC = cursor.getString(cursor.getColumnIndex("pass")))!= null){
                     if (login.matches(loginC)) {



                       Intent intent = new Intent(this, MainActivity.class);
                       intent.putExtra("username", cursor.getString(cursor.getColumnIndex("username")));
                       db.close();
                       startActivity(intent);
                       checker = true;
                       break;

                     } else {
                       Toast.makeText(this, R.string.wrongpass, Toast.LENGTH_SHORT).show();
                       checker = true;
                       break;
                     }
                   }
               } else {
                   cursor.moveToNext();
               }
               } else {
                   cursor.moveToNext();
               }


           }
            cursor.close();
        }
        if(checker == false) {

            db.close();
            Intent intent = new Intent(this, PopUp.class);
            startActivity(intent);
        }
    }
}
