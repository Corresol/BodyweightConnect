package com.statletics.bodyweightconnect;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.logging.Logger;

/**
 * Created by Tonni on 22.07.2016.
 */
public class MessageHandler {

    private static final String SEND_TAP_CAPABILITY_NAME = "send_tap";
    private String TAG = "MessageHandler";
    private GoogleApiClient mGoogleApiClient;
    private String transcriptionNodeId = null;

    public MessageHandler(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.mGoogleApiClient.connect();
    }

    public void sendData(final String data) {
        Logger.getLogger(MessageHandler.class.getName()).info("sendData ...");
        if(this.mGoogleApiClient.isConnected()==false){
            this.mGoogleApiClient.connect();
        }
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
