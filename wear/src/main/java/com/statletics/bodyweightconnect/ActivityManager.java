package com.statletics.bodyweightconnect;

import android.app.Activity;

/**
 * Created by Tonni on 19.10.2016.
 */

public class ActivityManager {

    private static ActivityManager instance;

    private Activity currentActivity;

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if(instance==null){
            instance = new ActivityManager();
        }
        return instance;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }
}
