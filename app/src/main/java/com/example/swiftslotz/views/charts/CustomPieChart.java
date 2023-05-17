package com.example.swiftslotz.views.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class CustomPieChart extends View {
    private Paint paint;
    private List<Sector> sectors;

    public CustomPieChart(Context context) {
        super(context);
        init();
    }

    public CustomPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        if (sectors == null) return;

        float width = getWidth();
        float height = getHeight();
        float cx = width / 2f;
        float cy = height / 2f;

        float outerRadius = Math.min(width, height) / 2f;
        outerRadius = outerRadius * 0.8f;
        float innerRadius = outerRadius * 0.8f; // adjust this factor to control the thickness of the ring

        // Draw each sector as part of the ring
        for (Sector sector : sectors) {
            Log.d("Circular value of Cx", String.valueOf(cx));
            Log.d("Circular value of Cy", String.valueOf(cy));
            paint.setColor(sector.getColor());
            canvas.drawArc(cx - outerRadius, cy - outerRadius, cx + outerRadius, cy + outerRadius,
                    sector.getStartAngle(), sector.getSweepAngle(), true, paint);
        }

        // Draw the inner circle to create the ring effect
        paint.setColor(Color.WHITE); // adjust this to the background color
        canvas.drawCircle(cx, cy, innerRadius, paint);

        // Draw the legend
        drawLegend(canvas, cx, cy, outerRadius + 20);
        Log.d("Canvas Size", String.valueOf(canvas.getHeight()));// adjust these coordinates as needed
    }

    private void drawLegend(Canvas canvas, float cx, float cy, float radius) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);


        for (Sector sector : sectors) {
            float startAngle = sector.getStartAngle();
            float textAngle = (float) Math.toRadians(startAngle);

            // reduce the radius a bit to draw the text inside the circle
            float textRadius = radius;

            // calculate the x and y coordinates for the text
            float x = (float) (cx + (textRadius * Math.cos(textAngle)));
            float y = (float) (cy + (textRadius * Math.sin(textAngle)));
            Log.d("Calculating textRadius", String.valueOf(textRadius));
            Log.d("Calculating textAngle", String.valueOf(textAngle));
            Log.d("Calculating cos", String.valueOf(Math.cos(textAngle)));
            Log.d("Calculating sin", String.valueOf(Math.sin(textAngle)));
            Log.d("Value of cx", String.valueOf(cx));
            Log.d("Value of x", String.valueOf(x));
            Log.d("Value of cy", String.valueOf(cy));
            Log.d("Value of y", String.valueOf(y));



            // draw the text
            paint.setColor(Color.BLACK);
            Log.d("get time", sector.getTime());
            canvas.drawText(sector.getTime(), x, y, paint);
        }
    }
}
