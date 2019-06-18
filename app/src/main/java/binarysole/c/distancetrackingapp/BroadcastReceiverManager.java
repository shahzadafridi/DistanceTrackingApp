package binarysole.c.distancetrackingapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

public class BroadcastReceiverManager {

    static String TAG = "BroadcastReceiverManager";

    public static void registerNetworkBroadCast(Context context) {
        IntentFilter networkFilter = new IntentFilter(context.getResources().getString(R.string.network_state));
        context.registerReceiver(new AppBroadCastReciever(), networkFilter);
    }

    public static void registerLocationBroadCast(Context context) {
        IntentFilter locationFilter = new IntentFilter(context.getResources().getString(R.string.location_state));
        context.registerReceiver(new AppBroadCastReciever(), locationFilter);
    }

    public static void registerUserActivityRecogBroadCast(Context context) {
        IntentFilter userStateFilter = new IntentFilter(context.getResources().getString(R.string.user_state));
        context.registerReceiver(new AppBroadCastReciever(), userStateFilter);
    }

    public static void enableBroadCastReciever(Context context) {
        ComponentName component = new ComponentName(context, AppBroadCastReciever.class);
        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Log.e(TAG, "receiver is enabled");

    }

    public static void disableBroadCastReciever(Context context) {
        ComponentName component = new ComponentName(context, AppBroadCastReciever.class);
        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        Log.e(TAG, "receiver is disabled");

    }
}
