package com.statletics.bodyweightconnect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tonni on 17.10.2016.
 */

public class RoundView extends View {

    private String YOUR_TEXT = "Bodyweight Connect || WEAR";
    private Path _arc;

    private Paint _paintText;


    public RoundView(Context context, AttributeSet attrs) {
        super(context,attrs);


        _paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        _paintText.setStyle(Paint.Style.FILL_AND_STROKE);
        _paintText.setColor(Color.BLACK);
        _paintText.setTextSize(20f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        _arc = new Path();
        int offset = 10;

        RectF oval = new RectF(offset,offset,getWidth()-(2*offset),getHeight()-(2*offset));
        _arc.addArc(oval, -180, 180);

        canvas.drawTextOnPath(YOUR_TEXT, _arc, 0, 10, _paintText);
        invalidate();
    }

    public void setText(String text){
        if(text==null){
            text="";
        }
        this.YOUR_TEXT = text;
    }

}
