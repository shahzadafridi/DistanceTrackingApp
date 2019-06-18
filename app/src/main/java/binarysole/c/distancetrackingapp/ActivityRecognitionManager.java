package binarysole.c.distancetrackingapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ActivityRecognitionManager {

    static String TAG = "ActivityRecognitionManager";
    private static long UPDATE_INTERVAL = 25 * 1000;  /* 30 sec */

    public static final int[] POSSIBLE_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };


    public static void registerActivityRecognition(Context context, ActivityRecognitionClient mActivityRecognitionClient) {
        //Set the activity detection interval. I’m using 3 seconds//
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                UPDATE_INTERVAL,
                getActivityDetectionPendingIntent(context));
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.e(TAG, "onSucess, Activity Recognition Registered.");
            }
        });

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public static void unregisterActivityRecognition(Context context, ActivityRecognitionClient mActivityRecognitionClient) {
        final PendingIntent myPendingIntent = getActivityDetectionPendingIntent(context);
        Task<Void> task = mActivityRecognitionClient.removeActivityTransitionUpdates(myPendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        myPendingIntent.cancel();
                        Log.e(TAG, "onSucess, Activity Recognition Unregistered.");
                    }
                });

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public static PendingIntent getActivityDetectionPendingIntent(Context context) {
        //Send the activity data to our AppBroadCastReciever class//
        Intent intent = new Intent(context, AppBroadCastReciever.class);
        intent.setAction(context.getResources().getString(R.string.user_state));
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void handleDetectedActivities(List<DetectedActivity> probableActivities, Context context) {
        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
                    setSharedPrefValue(context,"Vehicle", activity.getConfidence(), true);
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e("ActivityRecogition", "On Foot: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e("ActivityRecogition", "Running: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.STILL: {
                    setSharedPrefValue(context,"Still", activity.getConfidence(), false);
                    Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e("ActivityRecogition", "Tilting: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
                    setSharedPrefValue(context, "Walking",activity.getConfidence(), false);
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e("ActivityRecogition", "Unknown: " + activity.getConfidence());
                    setSharedPrefValue(context, "Unknown", activity.getConfidence(), false);
                    break;
                }
            }
        }
    }

    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.vehicle);
            default:
                return resources.getString(R.string.unknown_activity, detectedActivityType);
        }
    }

    public static String detectedActivitiesToJson(ArrayList<DetectedActivity> detectedActivitiesList) {
        Type type = new TypeToken<ArrayList<DetectedActivity>>() {
        }.getType();
        return new Gson().toJson(detectedActivitiesList, type);
    }

    public static ArrayList<DetectedActivity> detectedActivitiesFromJson(String jsonArray) {
        Type listType = new TypeToken<ArrayList<DetectedActivity>>() {
        }.getType();
        ArrayList<DetectedActivity> detectedActivities = new Gson().fromJson(jsonArray, listType);
        if (detectedActivities == null) {
            detectedActivities = new ArrayList<>();
        }
        return detectedActivities;
    }

    public static void setSharedPrefValue(Context context,String type, int confidence, Boolean value) {
        if (confidence > 75) {
            VehicleBusEvent event = new VehicleBusEvent();
            event.setType(type);
            event.setConfidence(confidence);
            event.setVehicle(value);
            EventBus.getDefault().post(event);
            Log.e(TAG, "KEY_DETECTED_ACTIVITIES updated.");
        }
    }
}
