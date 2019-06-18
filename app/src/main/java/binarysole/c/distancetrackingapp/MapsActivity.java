package binarysole.c.distancetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleMap mMap;
    FusedLocationProviderClient locationProviderClient, getLastLocationFromLocationProvider;
    LocationRequest locationRequest;
    Location mLocation, mLocation2;
    LatLng mLatlng;
    Marker user_marker;
    GeoFire geoFire;
    String TAG = "MapsActivity";
    String LOCATION = "location", KM = "kilometers", LONGTITUDE = "longtitude", LATITUDE = "latitude";
    DatabaseReference locationRef;
    Map<String, String> map = new HashMap<>();
    float kilometers = 0.0f, meters = 0.0f;
    TextView kiloTv, timeTv;
    StringBuilder builder;
    private ActivityRecognitionClient mActivityRecognitionClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        kiloTv = (TextView) findViewById(R.id.total_distance_campaignMap);
        timeTv = (TextView) findViewById(R.id.travel_time_campaignMap);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRef = FirebaseDatabase.getInstance().getReference().child(LOCATION).child("+923339218035");
        geoFire = new GeoFire(locationRef);
        builder = new StringBuilder();
        SharedPreferences sharedPreferences = getSharedPreferences(LocationService.DETECTED_ACTIVITIES, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        BroadcastReceiverManager.enableBroadCastReciever(this);
        ActivityRecognitionManager.registerActivityRecognition(this, mActivityRecognitionClient);
        ActivityStackManager.getInstance().startLocationService(MapsActivity.this);
//        Intent intent = new Intent("binarysole.c.distancetrackingapp.LONGRUNSERVICE1");
//        intent.setPackage(this.getPackageName());
//        startService(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(20000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (Permissions.getInstance(MapsActivity.this).checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            locationProviderClient.requestLocationUpdates(locationRequest, mLocationCallBack, Looper.myLooper());
            getLastLocationFromLocationProvider = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
            getLastLocationFromLocationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                        user_marker = mMap.addMarker(new MarkerOptions().position(mLatlng).title("My Location"));
                        CircleOptions circleOptions = new CircleOptions()
                                .center(mLatlng)   //set center
                                .radius(100)   //set radius in meters
                                .fillColor(R.color.colorPrimaryDark2)  //default
                                .strokeColor(Color.LTGRAY)
                                .strokeWidth(5);
                        mMap.addCircle(circleOptions);
                        CameraPosition cameraPosition = CameraPosition.builder()
                                .target(mLatlng)
                                .zoom(11)
                                .bearing(90)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                    }
                }
            });

        }
    }

    LocationCallback mLocationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mLocation = locationResult.getLastLocation();
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    if (location.getAccuracy() <= 50.0f && System.currentTimeMillis() - location.getTime() <= 180000) {
                        map.put("lat", String.valueOf(location.getLatitude()));
                        map.put("lon", String.valueOf(location.getLongitude()));
                        map.put("Accuracy", String.valueOf(location.getAccuracy()));
                        map.put("Speed", String.valueOf(location.getSpeed()));
                        map.put("Time", String.valueOf(location.getTime()));
                        if (mLocation2 != null) {
                            builder.append("-\n");
                            builder.append("[accuracy:" + location.getAccuracy() + "],\n");
                            builder.append("[speed:" + location.getSpeed() + "],\n");
                            builder.append("[time:" + location.getTime() + "],\n");
                            builder.append("[latitude:" + location.getLatitude() + "],\n");
                            builder.append("[longtitude:" + location.getLongitude() + "],\n");
                            meters = location.distanceTo(mLocation2);
                            builder.append("[Meters:" + meters + "],\n");
                            if (meters > 30) { // if meters is above from 30 then add value to firebase.
                                map.put("Meters", String.valueOf(meters));
                                kilometers = kilometers + meters / 1000;
                                kiloTv.setText("" + String.format("%.2f", kilometers) + " KM");
                                map.put("kilometers", String.valueOf(kilometers));
                                builder.append("[Kilometers:" + kilometers + "],\n");
                                builder.append("\n\n");
                                locationRef.push().setValue(map);
                                Log.e(TAG, builder.toString());
                            }
                            builder.setLength(0);
                        }
                        mLocation2 = location;
                    } else {
                        Log.e(TAG, "location is not correct");
                    }
                    // geoFire.setLocation(locationRef.push().getKey(), new GeoLocation(location.getLatitude(), location.getLongitude()));
                    Log.e(TAG, "location updated.");
                }
            }

        }
    };


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
//            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MapsActivity", "OnDestroy()");
        BroadcastReceiverManager.disableBroadCastReciever(this);
        stopService(new Intent(this, LocationService.class));
        ActivityRecognitionManager.unregisterActivityRecognition(this, mActivityRecognitionClient);

        SharedPreferences sharedPreferences = getSharedPreferences(LocationService.DETECTED_ACTIVITIES, Context.MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences sharedPreferences1 = getSharedPreferences(LocationService.DETECTED_ACTIVITIES, Context.MODE_PRIVATE);
        Boolean IN_VEHICLE = sharedPreferences1.getBoolean(LocationService.KEY_DETECTED_ACTIVITIES, true);
        if (IN_VEHICLE) {
            Log.e(TAG, "User is in vichel");
        } else {
            Log.e(TAG, "User is not in vichel");
        }
    }
}
