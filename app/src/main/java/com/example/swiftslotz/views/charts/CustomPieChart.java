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
        float innerRadius = outerRadius * 0.8f; // adjust this factor to control the thickness of the ring

        // Draw each sector as part of the ring
        for (Sector sector : sectors) {
            paint.setColor(sector.getColor());
            canvas.drawArc(cx - outerRadius, cy - outerRadius, cx + outerRadius, cy + outerRadius,
                    sector.getStartAngle(), sector.getSweepAngle(), true, paint);
            paint.setColor(Color.BLACK);
            Log.d("logging title", sector.getTitle());
            canvas.drawText(sector.getTitle(), cx, cy, paint);
        }

        // Draw the inner circle to create the ring effect
        paint.setColor(Color.WHITE); // adjust this to the background color
        canvas.drawCircle(cx, cy, innerRadius, paint);
    }

}
