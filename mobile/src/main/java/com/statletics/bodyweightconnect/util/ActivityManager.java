package com.statletics.bodyweightconnect.util;

import android.app.Activity;

/**
 * Created by Tonni on 25.06.2016.
 */
public class ActivityManager {

    private static ActivityManager instance;

    private Activity currentActivity;

    private  ActivityManager() {
    }

    public static ActivityManager getInstance(){
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
