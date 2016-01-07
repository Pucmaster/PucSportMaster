package com.pucmasterapps.pucsportmaster;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.greenrobot.event.EventBus;

public class MapsActivity extends Fragment {
    private List<Marker> markers = new ArrayList<Marker>();


    private final Handler mHandler = new Handler();
    private  GoogleMap mMap;
    private Marker selectedMarker;

    Handler handler = new Handler();
    Random random = new Random();
    Context context;
    public void onEvent(MainActivity.MessageEvent event){
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();

        addLocations();
        animator.startAnimation(true);
    }
    public void onEvent(MainActivity.PauseEvent event){
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
        animator.stopAnimation();
    }
    public void onEvent(MainActivity.ResumeEvent event){
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
        animator.startAnimation(true);
    }

    /*
    *
    *
   */
    @Override
    public void onStart() {

        super.onStart();
        EventBus.getDefault().register(this);

    }
    /*
    *
    *
   */
    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Destroyed View");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        /*try {
            Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (Exception e) {
            System.out.println("Exception View");
            e.printStackTrace();
        }*/

        super.onDestroyView();
       /* SupportMapFragment f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (f != null && f.isResumed()){
            getFragmentManager().beginTransaction().remove(f).commit();
        }*/
        try {
            Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.map));
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
         *s
         *
        */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("View called");
        View root = inflater.inflate(R.layout.activity_maps, container, false);
        mMap = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        return root;
    }
    /*
     *
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();


    }


    // Obtain the SupportMapFragment and get notified when the map is ready to be used.


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMyLocationEnabled(false);
        // Add a marker in Sydney and move the camera
        // buildGoogleApiClient();

    }

    CancelableCallback MyCancelableCallback =
            new CancelableCallback(){

                @Override
                public void onCancel() {
                    System.out.println("onCancelled called");
                }

                @Override
                public void onFinish() {


                    if(++currentPt < markers.size()){

//						//Get the current location
//						Location startingLocation = new Location("starting point");
//						startingLocation.setLatitude(googleMap.getCameraPosition().target.latitude);
//						startingLocation.setLongitude(googleMap.getCameraPosition().target.longitude);
//
//						//Get the target location
//						Location endingLocation = new Location("ending point");
//						endingLocation.setLatitude(markers.get(currentPt).getPosition().latitude);
//						endingLocation.setLongitude(markers.get(currentPt).getPosition().longitude);
//
//						//Find the Bearing from current location to next location
//						float targetBearing = startingLocation.bearingTo(endingLocation);

                        float targetBearing = bearingBetweenLatLngs( mMap.getCameraPosition().target, markers.get(currentPt).getPosition());

                        LatLng targetLatLng = markers.get(currentPt).getPosition();
                        //float targetZoom = zoomBar.getProgress();


                        System.out.println("currentPt  = " + currentPt  );
                        System.out.println("size  = " + markers.size());
                        //Create a new CameraPosition
                        CameraPosition cameraPosition =
                                new CameraPosition.Builder()
                                        .target(targetLatLng)
                                        .tilt(currentPt<markers.size()-1 ? 90 : 0)
                                        .bearing(targetBearing)
                                        .zoom(mMap.getCameraPosition().zoom)
                                        .build();


                        mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(cameraPosition),
                                3000,
                                MyCancelableCallback);
                        System.out.println("Animate to: " + markers.get(currentPt).getPosition() + "\n" +
                                "Bearing: " + targetBearing);

                        markers.get(currentPt).showInfoWindow();

                    }else{
                        //info.setText("onFinish()");
                    }

                }

            };

   /* public void addLocations(){
        addMarkerToMap(new LatLng(50.961813797827055,3.5168474167585373));
        addMarkerToMap(new LatLng(50.96085423274633,3.517405651509762));
        addMarkerToMap(new LatLng(50.96020550146382,3.5177918896079063));
        addMarkerToMap(new LatLng(50.95936754348453,3.518972061574459));
        addMarkerToMap(new LatLng(50.95877285446026,3.5199161991477013));
        addMarkerToMap(new LatLng(50.958179213755905,3.520646095275879));
        addMarkerToMap(new LatLng(50.95901719316589,3.5222768783569336));
        addMarkerToMap(new LatLng(50.95954430150347,3.523542881011963));
        addMarkerToMap(new LatLng(50.95873336312275,3.5244011878967285));
        addMarkerToMap(new LatLng(50.95955781702322,3.525688648223877));
        addMarkerToMap(new LatLng(50.958855004782116,3.5269761085510254));
    }*/
   public void addLocations() {
        List<LatLng> loc = MainActivity.self.markers;
        for(int i=0;i<loc.size();i++){
            addMarkerToMap(loc.get(i));
        }
    }
    private Animator animator = new Animator();
    int currentPt;
    public class Animator implements Runnable {

        private static final int ANIMATE_SPEEED = 1500;
        private static final int ANIMATE_SPEEED_TURN = 1000;
        private static final int BEARING_OFFSET = 20;

        private final Interpolator interpolator = new LinearInterpolator();

        int currentIndex = 0;

        float tilt = 90;
        float zoom = 15.5f;
        boolean upward=true;

        long start = SystemClock.uptimeMillis();

        LatLng endLatLng = null;
        LatLng beginLatLng = null;

        boolean showPolyline = false;

        private Marker trackingMarker;

        public void reset() {
            resetMarkers();
            start = SystemClock.uptimeMillis();
            currentIndex = 0;
            endLatLng = getEndLatLng();
            beginLatLng = getBeginLatLng();

        }

        public void stop() {
            trackingMarker.remove();
            mHandler.removeCallbacks(animator);

        }

        public void initialize(boolean showPolyLine) {
            reset();
            this.showPolyline = showPolyLine;

            highLightMarker(0);

            if (showPolyLine) {
                polyLine = initializePolyLine();
            }

            // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
            LatLng markerPos = markers.get(0).getPosition();
            LatLng secondPos = markers.get(1).getPosition();

            setupCameraPositionForMovement(markerPos, secondPos);

        }

        private void setupCameraPositionForMovement(LatLng markerPos,
                                                    LatLng secondPos) {

            float bearing = bearingBetweenLatLngs(markerPos,secondPos);

            trackingMarker = mMap.addMarker(new MarkerOptions().position(markerPos)
                    .title("title")
                    .snippet("snippet"));

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(markerPos)
                            .bearing(bearing + BEARING_OFFSET)
                            .tilt(90)
                            .zoom(mMap.getCameraPosition().zoom >=16 ? mMap.getCameraPosition().zoom : 16)
                            .build();

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new CancelableCallback() {

                        @Override
                        public void onFinish() {
                            System.out.println("finished camera");
                            animator.reset();
                            Handler handler = new Handler();
                            handler.post(animator);
                        }

                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    }
            );
        }

        private Polyline polyLine;
        private PolylineOptions rectOptions = new PolylineOptions();


        private Polyline initializePolyLine() {
            //polyLinePoints = new ArrayList<LatLng>();
            rectOptions.add(markers.get(0).getPosition());
            return mMap.addPolyline(rectOptions);
        }

        /**
         * Add the marker to the polyline.
         */
        private void updatePolyLine(LatLng latLng) {
            List<LatLng> points = polyLine.getPoints();
            points.add(latLng);
            polyLine.setPoints(points);
        }


        public void stopAnimation() {
            animator.stop();
        }

        public void startAnimation(boolean showPolyLine) {
            if (markers.size()>2) {
                animator.initialize(showPolyLine);
            }
        }


        @Override
        public void run() {

            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);

//			LatLng endLatLng = getEndLatLng();
//			LatLng beginLatLng = getBeginLatLng();

            double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
            double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
            LatLng newPosition = new LatLng(lat, lng);

            trackingMarker.setPosition(newPosition);

            if (showPolyline) {
                updatePolyLine(newPosition);
            }

            // It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
            //navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
            //navigateToPoint(newPosition,false);

            if (t< 1) {
                mHandler.postDelayed(this, 16);
            } else {

                System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + markers.size());
                // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
                if (currentIndex<markers.size()-2) {

                    currentIndex++;

                    endLatLng = getEndLatLng();
                    beginLatLng = getBeginLatLng();


                    start = SystemClock.uptimeMillis();

                    LatLng begin = getBeginLatLng();
                    LatLng end = getEndLatLng();

                    float bearingL = bearingBetweenLatLngs(begin, end);

                    highLightMarker(currentIndex);

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(end) // changed this...
                                    .bearing(bearingL  + BEARING_OFFSET)
                                    .tilt(tilt)
                                    .zoom(mMap.getCameraPosition().zoom)
                                    .build();


                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            ANIMATE_SPEEED_TURN,
                            null
                    );

                    start = SystemClock.uptimeMillis();
                    mHandler.postDelayed(animator, 16);

                } else {
                    currentIndex++;
                    highLightMarker(currentIndex);
                    stopAnimation();
                }

            }
        }



        private LatLng getEndLatLng() {
            return markers.get(currentIndex+1).getPosition();
        }

        private LatLng getBeginLatLng() {
            return markers.get(currentIndex).getPosition();
        }

        private void adjustCameraPosition() {
            //System.out.println("tilt = " + tilt);
            //System.out.println("upward = " + upward);
            //System.out.println("zoom = " + zoom);
            if (upward) {

                if (tilt<90) {
                    tilt ++;
                    zoom-=0.01f;
                } else {
                    upward=false;
                }

            } else {
                if (tilt>0) {
                    tilt --;
                    zoom+=0.01f;
                } else {
                    upward=true;
                }
            }
        }
    };

    /**
     * Allows us to navigate to a certain point.
     */
    public void navigateToPoint(LatLng latLng,float tilt, float bearing, float zoom,boolean animate) {
        CameraPosition position =
                new CameraPosition.Builder().target(latLng)
                        .zoom(zoom)
                        .bearing(bearing)
                        .tilt(tilt)
                        .build();

        changeCameraPosition(position, animate);

    }

    public void navigateToPoint(LatLng latLng, boolean animate) {
        CameraPosition position = new CameraPosition.Builder().target(latLng).build();
        changeCameraPosition(position, animate);
    }

    private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (animate) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }

    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
        Location beginL= convertLatLngToLocation(begin);
        Location endL= convertLatLngToLocation(end);

        return beginL.bearingTo(endL);
    }

    public void toggleStyle() {
        if (GoogleMap.MAP_TYPE_NORMAL == mMap.getMapType()) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }


    /**
     * Adds a marker to the map.
     */
    public void addMarkerToMap(LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("title")
                .snippet("snippet"));
        markers.add(marker);

    }

    /**
     * Clears all markers from the map.
     */
    public void clearMarkers() {
        mMap.clear();
        markers.clear();
    }



    /**
     * Remove the currently selected marker.
     */
    public void removeSelectedMarker() {
        this.markers.remove(this.selectedMarker);
        this.selectedMarker.remove();
    }

    /**
     * Highlight the marker by index.
     */
    private void highLightMarker(int index) {
        highLightMarker(markers.get(index));
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {

		/*
		for (Marker foundMarker : this.markers) {
			if (!foundMarker.equals(marker)) {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			} else {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				foundMarker.showInfoWindow();
			}
		}
		*/
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        marker.showInfoWindow();

        //Utils.bounceMarker(googleMap, marker);

        this.selectedMarker=marker;
    }

    private void resetMarkers() {
        for (Marker marker : this.markers) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }









/*
    Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    double lat;
    double longit;
    Location location;
    PolylineOptions rectOptions = new PolylineOptions();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;
    boolean initialized = false;
    CameraPosition cameraPosition;

    public void GPStracker(Context context) {
        this.context = context;
        getLocation();
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

    @Override
    public void onLocationChanged(Location location) {



        GPStracker(getActivity());
        loc = new LatLng(lat,longit);
        LatLng pos = loc;

        //mMap.addMarker(new MarkerOptions().position(pos));
        addMarkerToMap(loc);
        if(!initialized) {
            polyLine = initializePolyLine();
        }
        updatePolyLine(loc);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

    }


    public Location getLocation(){
        try {
            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
         //   LocationListener locationListener = new MyLocationListener();

            if(!isGPSEnabled && !isNetworkEnabled){
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getActivity(),
                           "No permission granted" , Toast.LENGTH_SHORT).show();
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
                    lat = location.getLatitude();
                    longit = location.getLongitude();
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Need to define the child fragment layout
        View root = inflater.inflate(R.layout.activity_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return root;
    }


    private GoogleMap mMap;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        }


    // Obtain the SupportMapFragment and get notified when the map is ready to be used.



    public LatLng loc;
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

  /*  @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GPStracker(getActivity());
        // Add a marker in Sydney and move the camera
       // buildGoogleApiClient();

        loc = new LatLng(lat,longit);
        LatLng pos = loc;
        addMarkerToMap(loc);
        if(!initialized) {
            polyLine = initializePolyLine();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    private Polyline initializePolyLine() {
        //polyLinePoints = new ArrayList<LatLng>();
        rectOptions.add(markers.get(0).getPosition());

        return mMap.addPolyline(rectOptions);
    }

    private Polyline polyLine;
    private List<Marker> markers = new ArrayList<Marker>();
    private void updatePolyLine(LatLng latLng) {
        List<LatLng> points = polyLine.getPoints();
        points.add(latLng);
        polyLine.setPoints(points);
    }
    public void addMarkerToMap(LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("title")
                .snippet("snippet"));
        markers.add(marker);

    }

*/
}
