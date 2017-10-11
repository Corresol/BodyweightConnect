package com.statletics.bodyweightconnect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.statletics.bodyweightconnect.type.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tonni on 01.11.2016.
 */

public class MapView extends View {

    private int distance = 5000;
    private List<Location> locations = new ArrayList<>();
    private float radius = 10;
    private Location myLocation;
    private float angelView = 0;

    public MapView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //System.out.println("Draw :: MapView");

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);

        if(myLocation==null){
            return;
        }

        //double m_per_deg_lat = 111132.954 - 559.822 * Math.cos( 2.0 * myLocation.getLatitude() ) + 1.175 * Math.cos( 4.0 * myLocation.getLatitude());
        //double m_per_deg_lon = (3.14159265359/180 ) * 6367449 * Math.cos ( myLocation.getLatitude() );

        for(Location loc:locations){
            Double x = getWidth()/2.;
            Double y = getHeight()/2.;
            double dist = distFrom(myLocation.getLatitude(),myLocation.getLongitude(),loc.getLatitude(),loc.getLongitude());
            //System.out.println("Distance: "+dist);
            if(distance >  dist ){
                Double angle = getDegrees(myLocation.getLatitude(),myLocation.getLongitude(),loc.getLatitude(),loc.getLongitude(),90+angelView);
                //paint
                int radius2 = Math.round(getWidth()/2)-10;

                x = x + ((dist/distance)*radius2) * Math.cos(angle*Math.PI/180);
                y = y + ((dist/distance)*radius2) * Math.sin(angle*Math.PI/180);

                //System.out.println("X:"+x+"/Y:"+y+" ---  "+getWidth()+"/"+getHeight()+" -- "+angle);

                canvas.drawCircle(x.floatValue(),y.floatValue(),radius,paint);
            }
        }
    }


    public void setDataset(List<Location> data){
        locations.clear();
        locations.addAll(data);
        System.out.println("Set Locations:"+data);
        invalidate();
    }

    public void setDistance(int distance){
        this.distance=distance;
        invalidate();
    }

    public void setViewPort(Location myLocation){
        this.myLocation=myLocation;
        invalidate();
    }

    public void setAngelView(float angelView) {
        this.angelView = angelView;
        invalidate();
    }

    private double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (earthRadius * c);
        return dist;
    }

    private double getDegrees(double lat1, double long1, double lat2, double long2, double headX) {

        //double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(long2-long1);

        double rlat1 = Math.toRadians(lat1);
        double rlat2 = Math.toRadians(lat2);

        double y = Math.sin(dLon) * Math.cos(rlat2);
        double x = Math.cos(rlat1)*Math.sin(rlat2) -
                Math.sin(rlat1)*Math.cos(rlat2)*Math.cos(dLon);
        double brng = Math.toDegrees(Math.atan2(y, x));

        // fix negative degrees
        if(brng<0) {
            brng=360-Math.abs(brng);
        }

        return brng - headX;
    }
}
