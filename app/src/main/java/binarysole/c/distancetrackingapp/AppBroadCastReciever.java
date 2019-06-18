package binarysole.c.distancetrackingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransitionResult;


public class AppBroadCastReciever extends BroadcastReceiver {

    String TAG = "AppBroadCastReciever";
    TextView textView;
    AppBarLayout appBarLayout;

    public AppBroadCastReciever(){

    }

//    public AppBroadCastReciever(TextView textView , AppBarLayout appBarLayout){
//        this.textView = textView;
//        this.appBarLayout = appBarLayout;
//    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String getAction = intent.getAction();
        Log.e("onRecieve",intent.getAction());

        if (getAction.equals(context.getResources().getString(R.string.network_state))) {
            int status = Utils.getConnectivityStatusString(context);
            if (status == Utils.NETWORK_STATUS_NOT_CONNECTED) {
                Log.e(TAG,"No connection.");
            } else {
                Log.e(TAG,"Connection exists.");
            }
        }else if (getAction.equals(context.getResources().getString(R.string.location_state))){

        }else if (getAction.equals(context.getResources().getString(R.string.user_state))){
            if (ActivityRecognitionResult.hasResult(intent)) {
                //If data is available, then extract the ActivityRecognitionResult from the Intent//
                ActivityTransitionResult transitionResult = ActivityTransitionResult.extractResult(intent);
                ActivityRecognitionResult recognitionResult = ActivityRecognitionResult.extractResult(intent);
                ActivityRecognitionManager.handleDetectedActivities(recognitionResult.getProbableActivities(),context);

            }

//            if (ActivityTransitionResult.hasResult(intent)) {
//                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
//                for (ActivityTransitionEvent event : result.getTransitionEvents()) {
//                    //continue only if the activity happened in the last 5 seconds
//                    //for some reason callbacks are received for old activities when the receiver is registered
//                    Log.e(TAG,"Outdated: Activity_type:"+event.getActivityType()+" , Transition_type"+event.getTransitionType());
//                    if(((SystemClock.elapsedRealtime()-(event.getElapsedRealTimeNanos()/1000000))/1000) <= 5) {
//                        //activity transition is legit. Do stuff here..
//                        Log.e(TAG,"Activity_type:"+event.getActivityType()+" , Transition_type"+event.getTransitionType());
//                    }
//                }
//            }
        }
    }
}