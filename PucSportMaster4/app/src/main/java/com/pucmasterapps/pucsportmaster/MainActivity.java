package com.pucmasterapps.pucsportmaster;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements android.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private JSONObject data;
    private JSONObject json;
    FragmentPagerAdapter adapterViewPager;
    ViewPager vpPager;
    boolean pauseClicked = false;
    boolean startClicked = false;
    boolean OnPause = false;
    public boolean parseItems = false;
    long hours;
    Timer stopWatchTimer = new Timer("stopWatchTimer", true);
    Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    LatLng pos;
    public List<LatLng> markers = new ArrayList<>();
    public List<Integer> distanceStats = new ArrayList<>();
    public List<Float> speedStats = new ArrayList<>();
    public List<Float> caloriesStats = new ArrayList<>();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 3;
    private static final long MIN_TIME_BW_UPDATES = 6000;
    protected LocationManager locationManager;
    public String city;
    public static MainActivity self;
    public LatLng loc;
    String userName;
    /************************************************************
    ***                                                       ***
    ***     EVENTY PRE POSIELANIE DO MAPS ACTIVITY            ***
    ***                                                       ***
     ************************************************************/
    public class MessageEvent {
        public final String message;
        public MessageEvent(String message) {
            this.message = message;
        }}
    public void onEvent(MessageEvent event) {
    }
    public class PauseEvent {
        public final String message;

        public PauseEvent(String message) {
            this.message = message;
        }}
    public void onEvent(PauseEvent event) {
    }
    public class ResumeEvent {
        public final String message;

        public ResumeEvent(String message) {
            this.message = message;
        }}
    public void ResumeEvent(PauseEvent event) {}
    public class JsonEvent {
        public final String message;

        public JsonEvent(String message) {
            this.message = message;
        }}
    public void onEvent(JsonEvent event) {}
    /*
    *
    *
    *
     */
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_main);
           Intent intent = getIntent();
           userName = intent.getStringExtra("username");


        self = this;
        EventBus.getDefault().register(this);
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(vpPager);
        // Attach the page change listener inside the activity
        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

      /*   loc = GPStracker(this);
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(loc.latitude, loc.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
            city = addresses.get(0).getLocality();*/


        //   Intent intent = new Intent(this, UpdaterService.class);
        //   startService(intent);


    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;

        private String tabTitles[] = new String[]{getString(R.string.infoTab), getString(R.string.BehTAb),getString(R.string.sumTab)};

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return FirstFragment.newInstance(0, self.userName);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return SecondFragment.newInstance(1, "Page # 2");

                case 2: // Fragment # 1 - This will show SecondFragment
                    return ThirdFragment.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }
        // Returns the page title for the top indicator

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.receiver, filter);
    }
    /************************************************************
     ***                                                       ***
     ***                FUNKCIE PRE POCASIE                    ***
     ***                                                       ***
     ************************************************************/
    public void openLocation(View view){
       // Log.i(TAG, "location clicked");
        try {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(String.format("geo:%f,%f?z=10", this.latitude, this.longitude)));

            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(this, R.string.geonotinst, Toast.LENGTH_SHORT).show();
        }
    }
    public void openProvider(View view){
        String ct = "Kosice";
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://openweathermap.org/find?q=" + ct ));
        startActivity(intent);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.broadcastReceived(intent);
        }
    };
    private void broadcastReceived(Intent intent){
      //  Toast.makeText(getApplicationContext(), "UI Updated", Toast.LENGTH_SHORT).show();
        TextView text = (TextView)findViewById(R.id.celsius);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
        } else {

            text.setText(R.string.nointcon);
        }
    }
    private String city_names;
    private double latitude;
    private double longitude;

    public void search(String city1){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            Toast.makeText(this, R.string.nointcon, Toast.LENGTH_SHORT).show();
            return;
        }
        WheaterTask task = new WheaterTask();
        task.execute(city1);
       // SetUiTask task1= new SetUiTask();
       // task1.execute();

    }

    public class WheaterTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            try {
                city_names = json.getString("name");
                TextView tv = (TextView)findViewById(R.id.city_name);
                tv.setText(city_names);

                JSONObject coord = json.getJSONObject("coord");
                JSONArray weather = json.getJSONArray("weather");
                JSONObject weatherDesc = weather.getJSONObject(0);

                latitude = coord.getDouble("lat");
                longitude = coord.getDouble("lon");

                TextView tempV = (TextView)findViewById(R.id.temperature);
                tempV.setText(json.getJSONObject("main").getString("temp") + "°C");

                TextView mainV = (TextView)findViewById(R.id.main_screen);
                mainV.setText(json.getJSONObject("main").getString("temp"));

                TextView humiV = (TextView)findViewById(R.id.humidity);
                humiV.setText(json.getJSONObject("main").getString("humidity") + "%");

                TextView pressV = (TextView)findViewById(R.id.pressure);
                pressV.setText(json.getJSONObject("main").getString("pressure") + "hPa");

                TextView windV = (TextView)findViewById(R.id.wind);
                windV.setText(json.getJSONObject("wind").getString("speed") + "m/s");

                TextView sunV = (TextView)findViewById(R.id.sun);


                long unix = Long.parseLong(json.getJSONObject("sys").getString("sunrise"));
                Date date = new Date(unix*1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                sunV.setText(sdf.format(date));

                TextView moonV = (TextView)findViewById(R.id.moon);
                long unix2 = Long.parseLong(json.getJSONObject("sys").getString("sunset"));
                Date date2 = new Date(unix2*1000L);

                moonV.setText(sdf.format(date2));

                TextView eyeV = (TextView)findViewById(R.id.eye);
                eyeV.setText(weatherDesc.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


           // Intent intent = new Intent(getApplicationContext(), ForecastActivity.class);

          //      intent.putExtra("json", json.toString());

          //      System.out.println("BLA View called");System.out.println("BLA View called");System.out.println("BLA View called");

          //      Toast.makeText(context, "Geolocation viewer is not installed.", Toast.LENGTH_SHORT).show();

          //  startActivity(intent);
        }

        @Override
        protected JSONObject doInBackground(String... cities) {

            for (String city : cities) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?units=metric&APPID=444453193f63707dab6d0f111f94835b&q=" + city);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + '\n');
                    }


                    return new JSONObject(stringBuilder.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
    /************************************************************
     ***                                                       ***
     ***         BUTTONY Z DRRUHEHO FRAGMENTU                  ***
     ***                                                       ***
     ************************************************************/
    public void onClickStart(View view) {

        if (!startClicked) {
            finaldistance = 0;
            TotalCalories = (float)0;
            MaxSpeed = (float)0;
            i=0;
            loc = GPStracker(this);
            markers.add(loc);
            startClicked = true;
            stopWatchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startTimer();
                        }
                    });
                }
            }
                    , 0, 100);


        }
    }
    public void onClickPause(View view) {
        if (startClicked) {
            if (!pauseClicked) {
                pauseClicked = true;
                Button btnPause = (Button) findViewById(R.id.btnPause);
                pauseTimer(view);
                btnPause.setText(R.string.resumeString);
            } else {
                pauseClicked = false;
                OnPause = false;
                startTimer();
                Button btnPause = (Button) findViewById(R.id.btnPause);
                btnPause.setText(R.string.pauseString);
            }
        }
    }
    public void onClickStop(View view) {
        summaryFunction();
        resetTimer(view);
        startClicked = false;
        parseItems = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);

        EventBus.getDefault().post(new MessageEvent(getString(R.string.maploaded)));
        stopWatchTimer = new Timer("stopWatchTimer", true);
        vpPager.setCurrentItem(2);

    }
    public void startTimer(){
                if (!OnPause) {
                    TextView hodiny = (TextView) findViewById(R.id.hours_id);
                    TextView minuty = (TextView) findViewById(R.id.minutes_id);
                    TextView sekundy = (TextView) findViewById(R.id.seconds_id);
                    TextView milisekundy = (TextView) findViewById(R.id.tenth_id);
                    //hodiny.setText("10");

                    int hourPart = Integer.parseInt(((TextView) findViewById(R.id.hours_id)).getText().toString());
                    int minutePart = Integer.parseInt(((TextView) findViewById(R.id.minutes_id)).getText().toString());
                    int secondPart = Integer.parseInt(((TextView) findViewById(R.id.seconds_id)).getText().toString());
                    int tenthPart = Integer.parseInt(((TextView) findViewById(R.id.tenth_id)).getText().toString());

                    if (minutePart > 59) {
                        hourPart++;
                        hodiny.setText(hourPart < 10 ? "0" + Integer.toString(hourPart) : Integer.toString(hourPart));
                        minuty.setText("00");
                        sekundy.setText("00");
                        milisekundy.setText("0");
                    } else {
                        if (secondPart > 59) {
                            minutePart++;
                            minuty.setText(minutePart < 10 ? "0" + Integer.toString(minutePart) : Integer.toString(minutePart));
                            sekundy.setText("00");
                            milisekundy.setText("0");
                        } else {
                            if (tenthPart >= 9) {
                                secondPart++;

                                sekundy.setText(secondPart < 10 ? "0" + Integer.toString(secondPart) : Integer.toString(secondPart));
                                milisekundy.setText("0");
                            } else {
                                tenthPart++;
                                milisekundy.setText(Integer.toString(tenthPart));
                            }
                        }
                    }
                }
            }


    public void pauseTimer(View view){
       OnPause = true;


    }
    public void resetTimer(View view) {
        stopWatchTimer.cancel();
        stopWatchTimer.purge();
        OnPause=false;
        pauseClicked=false;
        Button btnPause = (Button) findViewById(R.id.btnPause);
        btnPause.setText(R.string.pauseString);
        hours = 0;
        TextView hours = (TextView)findViewById(R.id.hours_id);
        hours.setText("00");
        TextView minutes = (TextView)findViewById(R.id.minutes_id);
        minutes.setText("00");
        TextView second = (TextView)findViewById(R.id.seconds_id);
        second.setText("00");
        TextView tenth = (TextView)findViewById(R.id.tenth_id);
        tenth.setText("0");
    }
    public LatLng GPStracker(Context context) {
        this.context = context;
        return getLocation();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    int i;

    Integer finaldistance;
    Integer distanceprev;
    Float MaxSpeed;

    Float TotalCalories;
    Float calories;
    float prev_time;
    @Override
    public void onLocationChanged(Location location) {
       // if(locFixer == true) {
            loc = GPStracker(this);

        /*if(markers.get(0) != null){
            double number = CalculationByDistance(markers.get(0).latitude * Math.PI/180, markers.get(0).longitude * Math.PI/180, loc.latitude * Math.PI/180, loc.longitude * Math.PI/180 );

        }*/
            float cur_time = SystemClock.uptimeMillis() / 1000L;
            if (markers.get(0) != null) {
                Integer distance = getDistance(markers.get(i), loc);
                distanceStats.add(distance);
                finaldistance = finaldistance + distance;
                String stringDist = finaldistance.toString();
                changeTextDist(stringDist);
                Float speed = (float) distance / (cur_time - prev_time);
                if (Float.isNaN(speed) || Float.isInfinite(speed)) {
                    speed = 0f;
                }
           /* if (location.hasSpeed()) {
                changeSpeedToText(location.getSpeed());
            } else {*/
                if (distanceprev != finaldistance && speed == 0) {

                } else {
                    changeSpeedToText(speed);
                    speedStats.add(speed);

                }

                distanceprev = finaldistance;
                calories = ((float) finaldistance * 124) / 1000;
                caloriesStats.add(calories);
                changeCaloriesToText(calories);
            }

            prev_time = cur_time;
            markers.add(loc);
            i++;
            //mMap.addMarker(new MarkerOptions().position(pos));
       // }
       // locFixer = true;
    }
    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public LatLng getLocation(){
        try {

            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,
                            R.string.noperm , Toast.LENGTH_SHORT).show();
                    return null;
                }
            }else {
                this.canGetLocation = true;

                if(isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
                if(isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if(location != null){
                   pos = new LatLng(location.getLatitude(),location.getLongitude());
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return pos;
    }
    /*************** Haversine formula
    * https://en.wikipedia.org/wiki/Haversine_formula
    * http://stackoverflow.com/questions/17787235/creating-a-method-using-haversine-formula-android-v2/17787472#17787472
    *
    ****************/
    /*public double CalculationByDistance(double initialLat, double initialLong, double finalLat, double finalLong){


        double latDiff = finalLat - initialLat;
        double longDiff = finalLong - initialLong;
        double earthRadius = 6371; //In Km if you want the distance in km

        double distance = 2*earthRadius*Math.asin(Math.sqrt(Math.pow(Math.sin(latDiff/2.0),2)+Math.cos(initialLat)*Math.cos(finalLat)*Math.pow(Math.sin(longDiff/2),2)));

        return distance;

    }*/
    private Integer getDistance(LatLng my_latlong,LatLng frnd_latlong){
        Location l1=new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2=new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        Integer distance=(int)l1.distanceTo(l2);

        Integer dist=distance;

        if(distance>1000)
        {
            distance=distance/1000;
            dist=distance;
        }
        return dist;
    }
    public void changeTextDist(String distance){
        TextView distM = (TextView) findViewById(R.id.dist_m);

       distM.setText(distance);
    }
    public void changeSpeedToText(Float speed){
        String pacer = speed.toString();
        TextView pacel = (TextView) findViewById(R.id.pace_l);
        pacel.setText(pacer);
    }
    public void changeCaloriesToText(Float calories){
        String calorer = calories.toString();
        TextView cal = (TextView)findViewById(R.id.calValue);
        cal.setText(calorer);
    }
    public void summaryFunction(){
       TextView distances = (TextView)findViewById(R.id.DistanceSummaryValue);
        distances.setText(finaldistance.toString());
       TextView maxSpeed = (TextView)findViewById(R.id.MaxSpeedSummaryValue);
        maxSpeed.setText(maxSpeedFunc().toString());
        TextView averageSpeed = (TextView)findViewById(R.id.MedianSpeedValue);
        averageSpeed.setText(averageSpeedFunc().toString());
        TextView caloriesSpnt = (TextView)findViewById(R.id.CaloriesSummaryValue);
        caloriesSpnt.setText(calories.toString());
        DbHElper dbHelperer = new DbHElper(this);
        SQLiteDatabase db1 = dbHelperer.getReadableDatabase();
        Cursor cursor = db1.rawQuery("SELECT * FROM runner WHERE username='" + userName + "'", null);
        cursor.moveToFirst();

        String TotalDistance = "0";;
        String TotalCal = "0";
        String runCounts = "0";
        if((cursor.getString(cursor.getColumnIndex("username"))) != null) {

            if ((TotalDistance = cursor.getString(cursor.getColumnIndex("total_distance"))) != null) {
                TotalDistance = ((Integer) (Integer.valueOf(TotalDistance) + finaldistance)).toString();
            } else {
                TotalDistance = "0";
            }


            if ((TotalCal = cursor.getString(cursor.getColumnIndex("total_calories"))) != null) {
                Integer cal = calories.intValue();
                Integer bla = Float.valueOf(TotalCal).intValue();
                TotalCal = ((Integer) (bla + cal)).toString();
            } else {
                TotalCal = "0";
            }

            if ((runCounts = cursor.getString(cursor.getColumnIndex("average_pace"))) != null) {

                runCounts = ((Integer) (Integer.valueOf(runCounts) + 1)).toString();
            } else {
                runCounts = "0";
            }
        }
        cursor.close();
        db1.close();
        TextView hodiny = (TextView) findViewById(R.id.hours_id);
        TextView minuty = (TextView) findViewById(R.id.minutes_id);
        TextView sekundy = (TextView) findViewById(R.id.seconds_id);
        TextView milisekundy = (TextView) findViewById(R.id.tenth_id);
        TextView Shodiny = (TextView) findViewById(R.id.Summary_hours_id);
        TextView Sminuty = (TextView) findViewById(R.id.Summmary_minutes_id);
        TextView Ssekundy = (TextView) findViewById(R.id.Summary_seconds_id);
        TextView Smilisekundy = (TextView) findViewById(R.id.Summmary_tenth_id);
        Shodiny.setText(hodiny.getText().toString());
        Sminuty.setText(minuty.getText().toString());
        Ssekundy.setText(sekundy.getText().toString());
        Smilisekundy.setText(milisekundy.getText().toString());

        DbHElper dbHelper = new DbHElper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "UPDATE runner SET distance='"+finaldistance.toString()+"',MaxSpeed='" +maxSpeedFunc().toString()+"',Calories='"+calories.toString()+"',total_distance='"+TotalDistance+"',total_calories='"+TotalCal+"',average_pace='"+runCounts+"' WHERE username='"+userName+"'";
        //_id INTEGER PRIMARY KEY, username text,pub_date text, duration INTEGER, distance INTEGER, isPucSportMaster INTEGER,pass text, email text, MaxSpeed INTEGER, Calories INTEGER,total_distance INTEGER, total_time INTEGER, total_calories INTEGER , average_pace INTEGER)");
        //    db.execSQL(SQL_CREATE_FORECAST);
       db.execSQL(query);
        db.close();

    }
    public Float maxSpeedFunc(){
        Float max = (float)0;
        for(int i=0;i<speedStats.size();i++){
            if(max < speedStats.get(i)){
                max = speedStats.get(i);
            }
        }
        return max;
    }
    public Float averageSpeedFunc(){
        Float average = (float)0;
        Float allSpeed = (float)0;
        for(int i=0;i<speedStats.size();i++){
                allSpeed = allSpeed + speedStats.get(i);
            }
        average = allSpeed/speedStats.size();
        return average;
    }
    boolean animBClicked = false;
    public void animationButton(View view){
        if (!animBClicked) {
            TextView btn = (TextView)findViewById(R.id.anim_button);
            btn.setText(R.string.restartanim);
            animBClicked = true;
            EventBus.getDefault().post(new PauseEvent(getString(R.string.animstopped)));

        } else {
            animBClicked = false;
            TextView btn = (TextView)findViewById(R.id.anim_button);
            btn.setText(R.string.stopanim);
            EventBus.getDefault().post(new ResumeEvent(getString(R.string.animrestarted)));
        }
    }
}

