package com.statletics.bodyweightconnect.network;

import android.os.AsyncTask;
import android.widget.Toast;

import com.statletics.bodyweightconnect.type.Location;
import com.statletics.bodyweightconnect.util.ActivityManager;
import com.statletics.bodyweightconnect.util.LocationUtil;

import org.apache.commons.io.IOUtils;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Tonni on 25.10.2016.
 */

public class SendPositionTask extends AsyncTask<String,Void,Boolean> {

    @Override
    protected Boolean doInBackground(String... data) {
        Location loc = LocationUtil.getInstance(ActivityManager.getInstance().getCurrentActivity()).getLocation();
        URL url = null;
        try {
            if(!NetUtil.hasNetworkConnect((ActivityManager.getInstance().getCurrentActivity()))){
                Toast.makeText(ActivityManager.getInstance().getCurrentActivity(),"No Network Connection",Toast.LENGTH_LONG).show();
                return false;
            }


            // Get XRFS token
            url = new URL(data[0]);
            HttpURLConnection client1 = (HttpURLConnection) url.openConnection();
            client1.connect();
            client1.setConnectTimeout(30000);

            List<String> token = client1.getHeaderFields().get("X-CSRF-PROTECT");
            client1.disconnect();

            url = new URL(data[1]);
            //System.out.println(url.toExternalForm());
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(30000);
            client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            client.setRequestProperty("X-CSRF-PROTECT",token.get(0));
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setDoInput(true);
            client.setUseCaches(false);

            String postParamaters = "long="+loc.getLongitude()+"&lat="+loc.getLatitude()+"&device="+data[2]+"&state="+data[3];

            client.setFixedLengthStreamingMode(postParamaters.getBytes().length);
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.print(postParamaters);
            out.close();

            IOUtils.copy(client.getInputStream(),System.out);
            // connect
            client.connect();

            System.out.println(client.getResponseCode()+"/"+client.getResponseMessage());



            if(client != null) { // Make sure the connection is not null.
                //client.disconnect();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            if(e instanceof UnknownHostException){
                //Toast.makeText(ActivityManager.getInstance().getCurrentActivity(),"URL unknown "+e.getMessage() ,Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }



}
