package binarysole.c.distancetrackingapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = Utils.getConnectivityStatus(context);
        int status = 0;
        if (conn == Utils.TYPE_WIFI) {
            status = NETWORK_STATUS_WIFI;
        } else if (conn == Utils.TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == Utils.TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }


    public static boolean isServiceRunning(Context context, Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isTopActivity(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) {
                return true;
            }
            return context.getClass().getName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo) activityManager.getRunningTasks(1).get(0)).topActivity.getClassName());
        } catch (Exception unused) {
            return true;
        }
    }

    public static boolean isActivityRunning(Context context, Class<?> cls) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                for (ActivityManager.RunningTaskInfo runningTaskInfo : activityManager.getRunningTasks(Integer.MAX_VALUE)) {
                    if (cls.getName().equalsIgnoreCase(runningTaskInfo.baseActivity.getClassName())) {
                        return true;
                    }
                }
            }
        } catch (Exception unused) {
        }
        return false;
    }

    public static String getDeviceId(Context context) {
        String str;
        if (Build.VERSION.SDK_INT >= 23 && !Permissions.hasPhoneStatePermission(context)) {
            return "";
        }
        String str2 = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (Build.VERSION.SDK_INT < 23) {
                try {
                    Method method = Class.forName(telephonyManager.getClass().getName()).getMethod("getDeviceId", new Class[]{Integer.TYPE});
                    Object[] objArr = {Integer.valueOf(1)};
                    String str3 = "";
                    if (method != null) {
                        str3 = (String) method.invoke(telephonyManager, objArr);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Second :");
                    sb.append(str3);
                    Log.e("SimData", sb.toString());
                    if (TextUtils.isEmpty(str3)) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str3);
                        sb2.append(",");
                        str2 = sb2.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
                return "";
            } else {
                if (TextUtils.isEmpty(telephonyManager.getDeviceId(1))) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(telephonyManager.getDeviceId(1));
                    sb3.append(",");
                    str2 = sb3.toString();
                }
            }
        }
        try {
            if (!TextUtils.isEmpty(str2) || !str2.contains(telephonyManager.getDeviceId())) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str2);
                sb4.append(telephonyManager.getDeviceId());
                str = sb4.toString();
            } else {
                str = telephonyManager.getDeviceId();
            }
            if (TextUtils.isEmpty(str)) {
                str = Settings.Secure.getString(context.getContentResolver(), "android_id");
            }
            StringBuilder sb5 = new StringBuilder();
            sb5.append("");
            sb5.append(str);
            return sb5.toString();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static String getUTCDate(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(Long.valueOf(j));
    }

    public static LatLngBounds getPakistanLatLngBounds() {
        Builder builder = new Builder();
        builder.include(new LatLng(24.967189d, 61.575533d));
        builder.include(new LatLng(25.344269d, 66.515127d));
        builder.include(new LatLng(24.818733d, 66.515127d));
        builder.include(new LatLng(24.602479d, 67.169772d));
        return builder.build();
    }

    public static boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            return !TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), "location_providers_allowed"));
        }
        boolean z = false;
        try {
            if (Settings.Secure.getInt(context.getContentResolver(), "location_mode") != 0) {
                z = true;
            }
            return z;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static float calculateDistance(double d, double d2, double d3, double d4) {
        Location location = new Location("gps");
        location.setLatitude(d);
        location.setLongitude(d2);
        Location location2 = new Location("gps");
        location2.setLatitude(d3);
        location2.setLongitude(d4);
        return location.distanceTo(location2);
    }

    public static double bearingBetweenLocations(LatLng latLng, LatLng latLng2) {
        double d = (latLng.latitude * 3.14159d) / 180.0d;
        double d2 = (latLng2.latitude * 3.14159d) / 180.0d;
        double d3 = ((latLng2.longitude * 3.14159d) / 180.0d) - ((latLng.longitude * 3.14159d) / 180.0d);
        return (Math.toDegrees(Math.atan2(Math.sin(d3) * Math.cos(d2), (Math.cos(d) * Math.sin(d2)) - ((Math.sin(d) * Math.cos(d2)) * Math.cos(d3)))) + 360.0d) % 360.0d;
    }

    public static String getAddressFromLocation(Context context, Location location, String TAG) {
        Geocoder gcd = new Geocoder(context,
                Locale.getDefault());
        String addressInfo = "";
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String locality = addresses.get(0).getLocality();
                String subLocality = addresses.get(0).getSubLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                Log.e(TAG, ", locality:" + locality + ", subLocality:" + subLocality + ", state:" + state + ", country:" + country + ", postalCode:" + postalCode + ", knownName:" + knownName);
//                addressInfo = address+","+locality+","+subLocality+","+state+","+country+","+postalCode+","+knownName;
                addressInfo = knownName + ", " + subLocality + ", " + locality;
                Log.e(TAG, knownName + ", " + subLocality + ", " + locality);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return addressInfo;
    }




}
