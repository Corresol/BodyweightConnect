package com.statletics.bodyweightconnect;

import android.app.Activity;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Tonni on 19.10.2016.
 */

public class WearMessageReceiver extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println("Receive Data:"+messageEvent.getPath()+new String(messageEvent.getData()));
        if(ActivityManager.getInstance().getCurrentActivity()!=null){
            Activity activity = ActivityManager.getInstance().getCurrentActivity();
            if(activity instanceof MainActivity){
                MainActivity ma = (MainActivity)activity;
                ma.updateText(new String(messageEvent.getData()));
            }

        }
    }


}
