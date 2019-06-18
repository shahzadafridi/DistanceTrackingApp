package binarysole.c.distancetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ActivityStackManager {

    private static final ActivityStackManager mActivityStack = new ActivityStackManager();

    private ActivityStackManager() {
    }

    public static ActivityStackManager getInstance() {
        return mActivityStack;
    }

    public void stopLocationService(Context context) {
        if (Utils.isServiceRunning(context, LocationService.class)) {
            context.stopService(new Intent(context, LocationService.class));
            Log.e("StackManager", "LocationService Stop");
        }
    }

    public void startLocationService(Context context){
        Intent serviceIntent = new Intent(context, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
