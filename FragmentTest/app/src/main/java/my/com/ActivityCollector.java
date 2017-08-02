package my.com;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2017/7/27.
 */

public class ActivityCollector {
    public static List<Activity> activityList = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity : activityList){
            if(!activity.isFinishing()){
                activity.finish();
                Log.d("ActivityCollector", " ------------------------------ ActivityCollector : finishAll");
            }
        }
    }

}
