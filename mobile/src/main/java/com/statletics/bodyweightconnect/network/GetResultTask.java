package com.statletics.bodyweightconnect.network;

import android.os.AsyncTask;
import android.widget.Toast;

import com.statletics.bodyweightconnect.type.Location;
import com.statletics.bodyweightconnect.util.ActivityManager;
import com.statletics.bodyweightconnect.util.LocationUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tonni on 02.11.2016.
 */

public class GetResultTask  extends AsyncTask<String,Void, Map<String,List<Location>>> {

    public static String KEY_CURRENT = "current";
    public static String KEY_RECENTLY = "recently";
    public static String KEY_OTHER = "other";

    @Override
    protected Map<String,List<Location>> doInBackground(String... data) {
        Map<String,List<Location>> map = new HashMap<>();
        List<Location> result1 = new ArrayList<>();
        List<Location> result2 = new ArrayList<>();
        List<Location> result3 = new ArrayList<>();
        map.put(KEY_CURRENT,result1);
        map.put(KEY_RECENTLY,result2);
        map.put(KEY_OTHER,result3);
        Location loc = LocationUtil.getInstance(ActivityManager.getInstance().getCurrentActivity()).getLocation();
        URL url = null;
        try {
            if(!NetUtil.hasNetworkConnect((ActivityManager.getInstance().getCurrentActivity()))){
                Toast.makeText(ActivityManager.getInstance().getCurrentActivity(),"No Network Connection",Toast.LENGTH_LONG).show();

                return map;
            }

            // Get XRFS token
            url = new URL(data[0]);
            HttpURLConnection client1 = (HttpURLConnection) url.openConnection();
            client1.setConnectTimeout(30000);

            client1.connect();
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

            String postParamaters = "long="+loc.getLongitude()+"&lat="+loc.getLatitude()+"&device="+data[2]+"&distance="+data[3];

            client.setFixedLengthStreamingMode(postParamaters.getBytes().length);
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.print(postParamaters);
            out.close();

            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            IOUtils.copy(client.getInputStream(),out2);
            // connect
            client.connect();
            out2.flush();
            out2.close();
            String dataRows  = out2.toString();

            //System.out.println(dataRows);

            try {
                if (dataRows.length() > 0) {
                    JSONObject rootObj = new JSONObject(dataRows);
                    JSONArray a = rootObj.getJSONArray("data1");
                    JSONArray b = rootObj.getJSONArray("data2");
                    JSONArray c = rootObj.getJSONArray("data3");

//                    System.out.println("A:" + a.length());
//                    System.out.println("B:" + b.length());
//                    System.out.println("C:" + c.length());

                    for (int i = 0; i < a.length(); i++) {
                        Location loc2 = new Location();
                        loc2.setLatitude(a.getJSONObject(i).getDouble("latitude"));
                        loc2.setLongitude(a.getJSONObject(i).getDouble("longitude"));
                        result1.add(loc2);
                    }
                    for (int i = 0; i < b.length(); i++) {
                        Location loc2 = new Location();
                        try {
                            loc2.setLatitude(b.getJSONObject(i).getDouble("latitude"));
                            loc2.setLongitude(b.getJSONObject(i).getDouble("longitude"));
                            result2.add(loc2);
                        } catch (JSONException ex) {
                            //do nothing
                        }
                    }
                    for (int i = 0; i < c.length(); i++) {
                        Location loc2 = new Location();
                        try {
                            loc2.setLatitude(c.getJSONObject(i).getDouble("latitude"));
                            loc2.setLongitude(c.getJSONObject(i).getDouble("longitude"));
                            result3.add(loc2);
                        } catch (JSONException ex) {
                            //do nothing
                        }
                    }
                }
            }catch (JSONException ex){
                //do nothing
                //verwirff den fehler
            }

            //System.out.println("Result:: "+client.getResponseCode()+"/"+client.getResponseMessage());

//            System.out.println("Map A :"+map.get(KEY_CURRENT).size());
//            System.out.println("Map B :"+map.get(KEY_RECENTLY).size());
//            System.out.println("Map C :"+map.get(KEY_OTHER).size());


            if(client != null) { // Make sure the connection is not null.
                //client.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof UnknownHostException){

            }
        }
        return map;
    }
}
