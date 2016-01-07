package com.pucmasterapps.pucsportmaster;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by jakub on 04.12.2015.
 */
 public class FirstFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    private JSONObject json;
   /* public void onEvent(MainActivity.JsonEvent event){
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
       listViewAdapter(getView());
        System.out.println("BLA View called");
    }
*/
    @Override
    public void onStart() {
        super.onStart();
       // EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
      //  EventBus.getDefault().unregister(this);
    }

//    public void listViewAdapter(View view){
//        try {
//
//            JSONArray array = this.json.getJSONArray("results");
//            this.jsonData = new JSONObject[array.length()];
//            for(int i = 0; i < array.length(); i++){
//                this.jsonData[i] = array.getJSONObject(i);
//
//            }
//            ArrayAdapter adapter = new ArrayAdapter(
//                    MainActivity.self,
//                    R.layout.fragment_list_view,
//                    R.id.secondLine,
//                    jsonData
//
//            );
//            ListView lv = (ListView) view.findViewById(android.R.id.list);
//            lv.setAdapter(adapter);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//    private JSONObject[] jsonData;
    // newInstance constructor for creating fragment with arguments
    public static FirstFragment newInstance(int page, String title) {
        FirstFragment fragmentFirst = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
        tvLabel.setText(page + " -- " + title);
        String city = "Kosice";
        MainActivity.self.search(city);
        DbHElper dbHelper = new DbHElper(MainActivity.self);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM runner WHERE username='" + title + "'", null);
        cursor.moveToFirst();
        String Ustring;
        if((Ustring = cursor.getString(cursor.getColumnIndex("username"))) != null) {
            TextView username = ((TextView) view.findViewById(R.id.UsernameText));
            username.setText(Ustring);
        } else {
            ((TextView)view.findViewById(R.id.UsernameText)).setText("no info");
        }

        String TotalDistance;
        if((TotalDistance = cursor.getString(cursor.getColumnIndex("total_distance")))!= null){

            ( (TextView)view.findViewById(R.id.TotalDistanceValue)).setText(TotalDistance);
        } else {
            ( (TextView)view.findViewById(R.id.TotalDistanceValue)).setText("No info");
        }

        String TotalCal;
        if((TotalCal = cursor.getString(cursor.getColumnIndex("total_calories")))!= null){

            ( (TextView)view.findViewById(R.id.TotalCaloriesSpentTextValue)).setText(TotalCal);
        } else {
            ( (TextView)view.findViewById(R.id.TotalCaloriesSpentTextValue)).setText("No info");
        }

        String runCounts;
        if((runCounts = cursor.getString(cursor.getColumnIndex("average_pace")))!= null){

            ( (TextView)view.findViewById(R.id.RunsCountValue)).setText(runCounts);
        } else {
            ( (TextView)view.findViewById(R.id.RunsCountValue)).setText("No info");
        }

      /*  {
            "url": "http://atlantis.cnl.sk:8000/workouts/1385/",
                "username": "matej",
                "duration": 3417,
                "distance": 0.0,
                "pub_date": "2016-01-04T20:10:03.589775Z"
        }*/
        // create cursor
        cursor = db.rawQuery("SELECT * FROM runner ORDER BY _id", null);

        // create adapter and associate it with ListView
        ListAdapter adapter = new ListAdapter(MainActivity.self, cursor);
        ListView lv = (ListView) view.findViewById(android.R.id.list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.self.getApplicationContext(), RunnerInfo.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        db.close();
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        insertNestedFragment();
    }
    private void insertNestedFragment() {
        Fragment childFragment = new WeatherFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.weather_container, childFragment).commit();

    }
}