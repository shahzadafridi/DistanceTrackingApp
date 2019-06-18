package binarysole.c.distancetrackingapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Opriday on 11/15/2018.
 */
@TargetApi(23)
public class Permissions {

    public static Permissions permissions;
    static Context context;
    public static int LOCATION_PERMISSION_CODE=101;
    public static int DEVICE_ID_PERMISSION_CODE = 102;
    public static int READ_EXTERNAL_STORAGE_CODE=103;

    public static Permissions getInstance(Context ctx){
        context = ctx;
        if (permissions == null){
            permissions = new Permissions();
            return permissions;
        }
        return permissions;
    }

    public boolean checkInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()){
            return true;
        }else {
            return false;
        }
    }

    public boolean checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            requestLocationPermission();
            return false;
        }
    }

    public void requestLocationPermission(){
        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
    }

    public boolean checkDeviceIdPermission(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            requestDeviceIdPermission();
            return false;
        }
    }

    public void requestDeviceIdPermission(){
        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_PHONE_STATE},DEVICE_ID_PERMISSION_CODE);
    }

    public boolean checkReadExternalStoragePermission(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            requestReadExternalStoragePermission();
            return false;
        }
    }

    public void requestReadExternalStoragePermission(){
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
    }
    public static boolean hasCameraPermissions(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.CAMERA") == 0;
    }

    public static boolean hasPhoneStatePermission(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == 0;
    }

    public static void getCameraPermissions(Context context) {
        ((Activity) context).requestPermissions(new String[]{"android.permission.CAMERA"}, 11);
    }

    public static boolean hasSMSPermissions(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.RECEIVE_SMS") == 0;
    }

    public static void getSMSPermissions(Context context) {
        ((AppCompatActivity) context).requestPermissions(new String[]{"android.permission.RECEIVE_SMS"}, 18);
    }

    public static boolean hasLocationPermissions(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0 && context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0 && context.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == 0;
    }

    public static void getLocationPermissions(Activity activity) {
        activity.requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_PHONE_STATE"}, 12);
    }

    public static void getCallAndLocationPermissions(Context context) {
        ((Activity) context).requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.CALL_PHONE"}, 17);
    }

    public static boolean hasContactsPermissions(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.GET_ACCOUNTS") == 0;
    }

    public static void getContactsPermissions(Context context) {
        ((Activity) context).requestPermissions(new String[]{"android.permission.GET_ACCOUNTS"}, 14);
    }

    public static boolean hasStoragePermissions(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    public static boolean hasCameraPermission(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.CAMERA") == 0;
    }

    public static void getStoragePermissions(Context context) {
        ((Activity) context).requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 13);
    }

    public static void getPhotosPermissions(Context context) {
        ((Activity) context).requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"}, 11);
    }

    public static boolean hasPermission(Context context, String str) {
        return ContextCompat.checkSelfPermission(context, str) == 0;
    }

    public static boolean hasCallPermission(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.CALL_PHONE") == 0;
    }

    public static void getCallPermission(Context context) {
        ((Activity) context).requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 10);
    }

    public static boolean hasMicPermission(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") == 0 && context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    public static void getMicPermission(Context context) {
        ((Activity) context).requestPermissions(new String[]{"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"}, 15);
    }

    public static boolean isReadContactsPermissionRequired(Context context) {
        return Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, "android.permission.READ_CONTACTS") != 0;
    }

    public static void getReadContactsPermission(Context context) {
        ((Activity) context).requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 21);
    }


}
