package com.statletics.bodyweightconnect.util;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Tonni on 17.07.2016.
 */
public class WearableManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private String TAG = "WearableManager";

    public WearableManager() {
        System.out.println("Start GoogleAPI..");
        mGoogleApiClient = new GoogleApiClient.Builder(ActivityManager.getInstance().getCurrentActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Log.d(TAG, "Connect GoogleAPI");
        mGoogleApiClient.connect();

//        final PendingResult<Status> statusPendingResult = Wearable.MessageApi.addListener(mGoogleApiClient, new MessageApi.MessageListener() {
//            @Override
//            public void onMessageReceived(MessageEvent messageEvent) {
//                System.out.println("Message Receive..");
//                System.out.println("Path:" + messageEvent.getPath());
//                System.out.println("NodeID:" + messageEvent.getSourceNodeId());
//                if (messageEvent.getData() != null) {
//                    receiveMessage(messageEvent.getData());
//                }
//            }
//        });

        Log.d(TAG, "fertig");
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
        Log.d(TAG, "Failed to connect");
    }


//    private void receiveMessage(byte[] data) {
//        //----- make some
//        String json = new String(data);
//        try{
//            JSONObject obj = new JSONObject(json);
//            if(Objects.equals(obj.get("event"),"tap")){
//                Activity ac = ActivityManager.getInstance().getCurrentActivity();
//                if(ac instanceof WebActivity){
//                    WebActivity wac = (WebActivity)ac;
//                    wac.callClickAction(null);
//                }
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        Log.d(TAG, "Data:"+ new String(data));
//
//    }

    public void sendData(final String data) {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (int i = 0; i < getConnectedNodesResult.getNodes().size(); i++) {
                    Node node = getConnectedNodesResult.getNodes().get(i);
                    String nName = node.getDisplayName();
                    String nId = node.getId();
                    Log.d(TAG, "Node name and ID: " + nName + " | " + nId);

                    PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                            "data", data.getBytes());

                    messageResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            System.out.println("Message:" + sendMessageResult.getRequestId());
                            System.out.println("\t\t" + sendMessageResult.getStatus().getStatusMessage());
                        }
                    });

                }
            }
        });
    }
}
