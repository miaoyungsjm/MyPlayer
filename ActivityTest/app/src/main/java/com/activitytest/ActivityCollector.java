package com.activitytest;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2017/8/29.
 */

public class ActivityCollector {

    private static List<Activity> activityList = new ArrayList<>();

    private static final String TAG = "ActivityCollector";

    public static void addActivity(Activity activity){
        Log.i(TAG, " ----- ActivityCollector : addActivity");
        Log.i(TAG, "       Activity : " + activity);
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity){
        Log.i(TAG, " ----- ActivityCollector : removeActivity");
        Log.i(TAG, "       Activity : " + activity);
        activityList.remove(activity);
    }

    public static void finishAll(){
        Log.i(TAG, " ----- ActivityCollector : finishAll");
        for (Activity activity : activityList){
            if(!activity.isFinishing()){
                activity.finish();
                Log.i(TAG, "       activity.finish() : " + activity);
            }
        }
    }

}
