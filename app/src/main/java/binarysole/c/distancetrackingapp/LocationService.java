package binarysole.c.distancetrackingapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LocationService extends Service {
    public static final String  DETECTED_ACTIVITIES = "DETECTED_ACTIVITIES";
    public static final String KEY_DETECTED_ACTIVITIES = "DETECTED_ACTIVITY_STATUS";
    public static final String LOCATION_SERVICE = "binarysole.c.distancetrackingapp.LocationService";
    Intent i = new Intent(LOCATION_SERVICE);
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    String TAG = "LocationService";
    //Notification
    RemoteViews expandView;
    Notification notification;
    private NotificationManagerCompat notificationManager;
    float kilometers = 0.0f, meters = 0.0f;
    public static final String CHANNEL_ID = "distance";
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Location mLocation;
    Boolean IN_VEHICLE = true;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        expandView = new RemoteViews(getPackageName(), R.layout.navigation_notification_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Distance Measurement",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        notificationManager = NotificationManagerCompat.from(this);
        expandView.setTextViewText(R.id.km_notification, "0.00 km");
        expandView.setTextViewText(R.id.address_notification, "  Loading...");
        expandView.setTextViewText(R.id.time_notification, "  1 hr 32 min");
        expandView.setTextViewText(R.id.activityStatus_notification,"Event: still");
        notification = new NotificationCompat.Builder(LocationService.this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomBigContentView(expandView)
                //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        startForeground(1, notification);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().register(this);
        init();
        Log.e(TAG, "onStartCommand");
        return START_STICKY;
    }

    public void onDestroy() {
        removeLocationUpdate();
        Log.e(TAG, "OnDestroy()");
        ActivityStackManager.getInstance().stopLocationService(this);
        super.onDestroy();
    }

    private void init() {
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient((Context) this);
        this.mLocationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationService.this.onNewLocation(locationResult.getLastLocation());
            }
        };
        setLocationInterval();
        getLastLocation();
        requestLocationUpdates();
    }

    private void getLastLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
                this.mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    public void onComplete(@NonNull Task<Location> task) {
                        if (!task.isSuccessful() || task.getResult() == null) {
                            Log.e(TAG, " getLastLocation() Error");
                            return;
                        }
                        LocationService.this.onNewLocation((Location) task.getResult());
                        Log.e(TAG, " getLastLocation() Success");
                    }
                });
            }
        } catch (SecurityException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Lost location permission.");
            sb.append(e);
            Log.e(TAG, sb.toString());
        }
    }

    /* access modifiers changed from: private */
    public void onNewLocation(Location location) {
        if (location != null) {
//            AppPreferences.saveLocation(new LatLng(location.getLatitude(), location.getLongitude()));
//            i.putExtra("latitude",location.getLatitude());
//            i.putExtra("longtitude",location.getLongitude());
//            sendBroadcast(i);
            StringBuilder sb = new StringBuilder();
            sb.append(location.getLatitude());
            sb.append(",");
            sb.append(location.getLongitude());
            sb.append("  (");
            sb.append(Utils.getUTCDate(location.getTime()));
            sb.append(")");
            Log.e("Location", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("accuracy = ");
            sb2.append(location.getAccuracy());
            Log.e("LocationService", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("timeDifference = ");
            sb3.append(System.currentTimeMillis() - location.getTime());
            Log.e(TAG, sb3.toString());
            if (location.getAccuracy() <= 50.0f && System.currentTimeMillis() - location.getTime() <= 180000 ) {
                if (mLocation != null) {
                    if (IN_VEHICLE) {
                        meters = location.distanceTo(mLocation);
                        expandView.setTextViewText(R.id.time_notification, "  " + String.format("%.2f", meters) + " meters");
                        Log.e(TAG, "" + String.format("%.2f", meters) + " meters");
                        if (meters > 30) { // if meters is above from 30 then add value to firebase.
                            kilometers = kilometers + meters / 1000;
                            Log.e(TAG, "  " + String.format("%.2f", kilometers) + " KM");
                            expandView.setTextViewText(R.id.km_notification, String.format("%.2f", kilometers) + " KM");
                            expandView.setTextViewText(R.id.address_notification, Utils.getAddressFromLocation(LocationService.this, location, TAG));
//                        notificationManager.notify(1, notification);
                        }
                        notificationManager.notify(1, notification);
                    }else {
                        Log.e(TAG,"No IN_VICHEL");
                    }
                }
                Log.e(TAG, "Accurate location.");
                mLocation = location;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setLocationInterval() {
        this.mLocationRequest = LocationRequest.create();
        this.mLocationRequest.setInterval(UPDATE_INTERVAL);
        this.mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.e(TAG, "setLocationInterval");
    }

    /* access modifiers changed from: protected */
    public void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            this.mFusedLocationClient.requestLocationUpdates(this.mLocationRequest, this.mLocationCallback, null);
            Log.e(TAG, "requestLocationUpdates");
        }
    }

    /* access modifiers changed from: protected */
    public void removeLocationUpdate() {
        try {
            this.mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
        } catch (Exception unused) {
        }
    }

    @Subscribe
    public void onEvent(VehicleBusEvent event){
        Log.e(TAG,"onEvent:"+event.getType());
        expandView.setTextViewText(R.id.activityStatus_notification,"Event: "+event.getType());
    }


}
