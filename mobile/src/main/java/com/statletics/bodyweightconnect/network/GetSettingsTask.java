package com.statletics.bodyweightconnect.network;

import android.os.AsyncTask;
import android.widget.Toast;

import com.statletics.bodyweightconnect.util.ActivityManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tonni on 09.11.2016.
 */

public class GetSettingsTask extends AsyncTask<String,Void, Map> {


    @Override
    protected Map doInBackground(String... data) {
        try {
            Map<String,Float> dataMap = new HashMap<>();
            if(!NetUtil.hasNetworkConnect((ActivityManager.getInstance().getCurrentActivity()))){
                Toast.makeText(ActivityManager.getInstance().getCurrentActivity(),"No Network Connection",Toast.LENGTH_LONG).show();
                return dataMap;
            }

            URL url = new URL(data[0]);
            HttpURLConnection client1 = (HttpURLConnection) url.openConnection();
            client1.setConnectTimeout(1000);
            client1.connect();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(client1.getInputStream(),out);
            client1.disconnect();
            out.flush();

            String s = new String(out.toByteArray());
            String[] rows = s.replaceAll("\r","").split("\n");

            for(String row:rows){
                if(row.contains(";")){
                    String[] cells=row.split(";");
                    dataMap.put(cells[0],Float.parseFloat(cells[1]));
                    //System.out.println(cells[0]+" - "+Float.parseFloat(cells[1]));
                }

            }
            return dataMap;
        } catch (IOException e) {
            //e.printStackTrace();
            if(e instanceof UnknownHostException){
                //Toast.makeText(ActivityManager.getInstance().getCurrentActivity(),"URL unknown"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        return  null;
    }
}
