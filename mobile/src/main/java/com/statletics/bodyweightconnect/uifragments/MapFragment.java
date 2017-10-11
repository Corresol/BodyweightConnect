package com.statletics.bodyweightconnect.uifragments;


import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.statletics.bodyweightconnect.R;
import com.statletics.bodyweightconnect.network.GetResultTask;
import com.statletics.bodyweightconnect.network.SendPositionTask;
import com.statletics.bodyweightconnect.type.Location;
import com.statletics.bodyweightconnect.util.LocationUtil;
import com.statletics.bodyweightconnect.view.SignalQualityView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Tonni on 10.08.2016.
 */
public class MapFragment extends Fragment implements SensorEventListener, OnMapReadyCallback {

    private final String remoteURL = "https://bwcnnct.com/meet/";
    //private final String remoteURL = "http://192.168.178.34/android";
    private final String remoteRequestURL = remoteURL + "/request.php";
    private final String remoteSendURL = remoteURL + "/send.php";
    private final String remoteReceiveURL = remoteURL + "/receive.php";

    private float currentDegree = 0f;
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    // device sensor manager
    private SensorManager mSensorManager;

    private TextView tv;
    private TextView gps;
    private Button btnSendPos;

    private SignalQualityView signalView;

    //private ImageView imageNorth;
    //private MapView mapView;

    private int count = 0;
    private int refresh = 20;
    private List<Float> dataList = new ArrayList<>();

    private Thread updateGPS;
    private Bundle saveBundle;

    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap googleMap;
    private int distance = 1000;
    private boolean trainingStatus = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_map_fragment, container, false);

//        tv = (TextView) v.findViewById(R.id.tvDegree);
//        tv.setText("0 °");
        gps = (TextView) v.findViewById(R.id.gpsView);
        //imageNorth = (ImageView) v.findViewById(R.id.northView);
        //mapView = (MapView) v.findViewById(R.id.paintView);

        signalView = (SignalQualityView)v.findViewById(R.id.signalView);

        Location loc = LocationUtil.getInstance(getActivity()).getLocation();
        gps.setText("GPS Signal Quality");

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        btnSendPos  = (Button) v.findViewById(R.id.btSendPosition);
        final Spinner spinner = (Spinner) v.findViewById(R.id.spinner2);
        btnSendPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location loc = LocationUtil.getInstance(getActivity()).getLocation();
                String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                new SendPositionTask().execute(remoteRequestURL, remoteSendURL, android_id, Boolean.toString(trainingStatus));
                //Toast.makeText(view.getContext(), "Sended " + android_id, Toast.LENGTH_LONG).show();
                updateMapView(distance);

                trainingStatus = !trainingStatus;
                btnSendPos.setText(((trainingStatus)?"Stop":"Start")+" training");
            }
        });

        btnSendPos.setText("Start training");


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = adapterView.getItemAtPosition(i).toString();
                distance = Integer.parseInt(s.replaceAll("km", "").trim()) * 1000;
                updateMapView(distance);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        updateGPS = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean run = true;
                while (run) {
                    final Location loc = LocationUtil.getInstance(getActivity()).getLocation();
                    final int current = LocationUtil.getInstance(getActivity()).getCurrentSats();
                    MapFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //gps.setText("Lat:" + loc.getLatitude() + "/Long:" + loc.getLongitude());
                            int max=10;
                            int cur = (current>10)?10:current;
                            signalView.setData(cur,max);
                            //System.out.println("Max:"+max+"Current:"+current);
                            if(googleMap!=null && googleMap.getCameraPosition().target.latitude==0 && googleMap.getCameraPosition().target.longitude==0){
                                CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(),loc.getLongitude()));
                                googleMap.moveCamera(center);
                                updateMapView(distance);
                            }
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        run = false;
                    }
                }
            }
        });
        updateGPS.start();

        if (saveBundle != null && saveBundle.containsKey("training.status") ) {
            trainingStatus=saveBundle.getBoolean("training.status");
            btnSendPos.setText(((trainingStatus)?"Stop":"Start")+" training");
        }
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
        if (updateGPS != null && updateGPS.isAlive()) {
            updateGPS.interrupt();
        }
        saveBundle = new Bundle();
        saveBundle.putBoolean("training.status", trainingStatus);
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private long lastTime = System.currentTimeMillis();
    private int fps = 50;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }

        SensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        float[] data = mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        if (tv != null) {
            float degree = 0;
            if (data[0] < 0) {
                degree = (float) (360 - ((180 / 3.14159) * Math.abs(data[0])));
            } else {
                degree = (float) (180 / 3.14159) * data[0];
            }
            dataList.add(degree);
            if (count % refresh == 0) {
                float a = 0;
                for (Float f : dataList) {
                    a += f;
                }
                a = a / dataList.size();
                dataList.clear();
                currentDegree = (currentDegree + a) / 2;
                count = 0;
            }
            count++;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap=map;
        Location loc= LocationUtil.getInstance(getActivity()).getLocation();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(loc.getLatitude(), loc.getLongitude()), 15));
        try{
            googleMap.setMyLocationEnabled(true);
        }catch (SecurityException ex){
            //
        }

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
//        map.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.house_flag))
//                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
//                .position(new LatLng(41.889, -87.622)));
    }


    private void updateMapView(final int distance) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Location loc = LocationUtil.getInstance(getActivity()).getLocation();
                String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                try {
                    final Map<String,List<Location>> data = new GetResultTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, remoteRequestURL, remoteReceiveURL, android_id, distance + "").get();


                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(googleMap!=null) {
                                Location loc1= LocationUtil.getInstance(getActivity()).getLocation();
                                if(distance<=1000) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(loc1.getLatitude(), loc1.getLongitude()), 15));
                                }else if (distance<=5000) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(loc1.getLatitude(), loc1.getLongitude()), 13));
                                }else if (distance<=10000) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(loc1.getLatitude(), loc1.getLongitude()), 11));
                                }else if (distance<=20000) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(loc1.getLatitude(), loc1.getLongitude()), 9));
                                }
                                googleMap.clear();
                                int[] green = {Color.rgb(230, 242, 208),Color.rgb(122, 184, 0)};
                                int[] orange = {Color.rgb(228, 192, 162),Color.rgb(183, 82, 200)};
                                int[] yellow = {Color.rgb(241, 239, 208),Color.rgb(183, 168, 0)};

                                addMapOverlay(data.get(GetResultTask.KEY_OTHER),yellow);
                                addMapOverlay(data.get(GetResultTask.KEY_RECENTLY),orange);
                                addMapOverlay(data.get(GetResultTask.KEY_CURRENT),green);
                            }
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addMapOverlay(List<Location> loc, int[] gradientColor){
        if(loc==null || loc.isEmpty()){
            return;
        }
        List<LatLng> markers = new ArrayList<>();
        for (Location l : loc) {
            markers.add(new LatLng(l.getLatitude(), l.getLongitude()));
        }
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        float[] startPoints = {0.3f, 1f};
        TileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(markers)
                .gradient(new Gradient(gradientColor,startPoints))
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

}