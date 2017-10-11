package com.statletics.bodyweightconnect.util;

import android.app.Activity;
import android.content.Context;

import com.statletics.bodyweightconnect.WebActivity;
import com.statletics.bodyweightconnect.uifragments.TrainContainerFragment;
import com.statletics.bodyweightconnect.uifragments.WebFragment;

import io.flic.lib.FlicBroadcastReceiver;
import io.flic.lib.FlicButton;

/**
 * Created by Tonni on 25.06.2016.
 */
public class BWCFlicBroadcastReceiver extends FlicBroadcastReceiver {
    @Override
    protected void onRequestAppCredentials(Context context) {
        // Set app credentials by calling FlicManager.setAppCredentials here
        //FlicManager.setAppCredentials("BodyWeightConnect", "69b7020b-e0c8-4054-841e-32e37e5bc6b1", "BodyWeightConnect");
    }

    @Override
    public void onButtonUpOrDown(Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isUp, boolean isDown) {
        if (isUp) {
            // Code for button up event here
            //Toast.makeText(context, "Receive event", Toast.LENGTH_SHORT).show();
        } else {
            // Code for button down event here
            //Toast.makeText(context, "Receive event", Toast.LENGTH_SHORT).show();
            Activity ac = ActivityManager.getInstance().getCurrentActivity();
            if(ac instanceof WebActivity){
                WebActivity wac = (WebActivity)ac;
                WebFragment wf = (WebFragment) ((TrainContainerFragment) wac.getSupportFragmentManager().findFragmentByTag("train")).getCurrentFragment();
                if (wf.isFlicDevice()) {
                    wf.callClickAction(null);
                }
            }
        }
    }

    @Override
    public void onButtonRemoved(Context context, FlicButton button) {
        // Button was removed
        button.removeAllFlicButtonCallbacks();
    }
}
