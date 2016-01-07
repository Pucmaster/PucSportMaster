package com.pucmasterapps.pucsportmaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class PopUp extends Activity {
   // Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.5),(int)(height*.3));
    }
    public void yes(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    public void no(View view){
        finish();
    }
}
