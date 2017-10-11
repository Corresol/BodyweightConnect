package com.statletics.bodyweightconnect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tonni on 28.11.2016.
 */

public class SignalQualityView extends View {

    private int current,max;

    public SignalQualityView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.LTGRAY);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(0,getHeight());
        path.lineTo(getWidth(),getHeight());
        path.lineTo(getWidth(),0);
        path.close();

        canvas.drawPath(path, paint);


        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.BLUE);

        Path path2 = new Path();
        path2.setFillType(Path.FillType.EVEN_ODD);
        path2.moveTo(0,getHeight());
        path2.lineTo(getWidth()*(current/(float)max),getHeight());
        path2.lineTo(getWidth()*(current/(float)max),getHeight()-(getHeight()*(current/(float)max)));
        path2.close();

        canvas.drawPath(path2, paint2);

    }


    public void setData(int current, int max) {
        this.current=current;
        this.max=max;
        invalidate();
    }

}
