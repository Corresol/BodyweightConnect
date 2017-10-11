package com.statletics.bodyweightconnect;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    String TAG = "MainActivity";

    private MessageHandler msgHandler;
    private RoundView roundText;

    public MainActivity() {
        ActivityManager.getInstance().setCurrentActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMsgHandler();
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int conResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(conResult!=ConnectionResult.SUCCESS){
            Log.e(TAG,"Google play services are not available on this device");
        }


        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override public void onLayoutInflated(WatchViewStub stub) {
                // Now you can access your views;
                roundText = (RoundView) stub.findViewById(R.id.roundText);
            }
        });
        System.out.println("Finish onCreate..");

    }

    private final int gap = 500;
    private long lastclick = -1;

    public void clickWear(View view) {
        //check double Click...
        if(System.currentTimeMillis()>(lastclick+gap)){
            lastclick=System.currentTimeMillis();
        }else{
            //double click action
            System.out.println("[WEAR] Click / Tap ...");
            getMsgHandler().sendData("{ 'event' : 'tap' }");
            vibrate(50);
            //reset click
            lastclick=-1;
        }
    }

    /**
     * Vibrate Wear
     * @param i - Time in millies
     */
    private void vibrate(int i) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()){
            vibrator.vibrate(i);
        }else{
            Log.e(TAG,"no vibrator");
        }
    }

    public void updateText(String s) {
        System.out.println("write: "+s);
        if(roundText!=null){
            System.out.println("...");
            try {
                JSONObject json = new JSONObject(s);
                roundText.setText(json.getString("text"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //roundText.setVisibility(View.GONE);
            //roundText.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Get Sender to send Message to Device
     *
     * @return
     */
    private MessageHandler getMsgHandler() {
        if (msgHandler == null) {
            GoogleApiClient cli = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            msgHandler = new MessageHandler(cli);

        }
        return msgHandler;
    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected:" + bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "ConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect : "+connectionResult.toString());
    }


}
