package com.statletics.bodyweightconnect.util;

import android.app.Activity;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.statletics.bodyweightconnect.WebActivity;
import com.statletics.bodyweightconnect.uifragments.TrainContainerFragment;
import com.statletics.bodyweightconnect.uifragments.WebFragment;

/**
 * Created by Tonni on 19.10.2016.
 */

public class WearMessageReceiver extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //System.out.println("Receive Data:"+messageEvent.getPath()+new String(messageEvent.getData()));
        Activity ac = ActivityManager.getInstance().getCurrentActivity();
        if (ac instanceof WebActivity) {
            WebActivity wac = (WebActivity) ac;
            if (((TrainContainerFragment) wac.getSupportFragmentManager().findFragmentByTag("train")).getCurrentFragment() instanceof WebFragment) {
                WebFragment wf = (WebFragment) ((TrainContainerFragment) wac.getSupportFragmentManager().findFragmentByTag("train")).getCurrentFragment();
                if (wf.isWearDevice()) {
                    wf.callClickAction(null);
                }
            }
        }
    }


}
