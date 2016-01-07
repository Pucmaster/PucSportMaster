package com.pucmasterapps.pucsportmaster;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jakub on 05.01.2016.
 */
public class ListAdapter extends CursorAdapter {
    private Context context;
    private JSONObject[] data;

    public ListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.fragment_list_view, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
       // try {

        JSONObject json = null;
        String username;
        String time;
        TextView tv1 = (TextView)view.findViewById(R.id.secondLine);
        TextView tv2 = (TextView)view.findViewById(R.id.firstLine);
        ImageView iv = (ImageView)view.findViewById(R.id.icon);
        try {
            json = new JSONObject(cursor.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.setTag(json);
        if(cursor.isAfterLast() == false){
            username = cursor.getString(cursor.getColumnIndex("username"));
            time = cursor.getString(cursor.getColumnIndex("pub_date"));
            if(username.matches(".*[a]$")){
                iv.setImageResource(R.drawable.female);
            } else {
                iv.setImageResource(R.drawable.male);
            }
            tv1.setText(username);
            tv2.setText(time);
            cursor.moveToNext();
        }
}
}
