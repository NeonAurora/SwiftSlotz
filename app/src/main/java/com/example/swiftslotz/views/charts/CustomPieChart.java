package com.example.swiftslotz.views.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
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
        float radius = Math.min(width, height) / 2f;
        float cx = width / 2f;
        float cy = height / 2f;

        for (Sector sector : sectors) {
            paint.setColor(sector.getColor());
            canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius,
                    sector.getStartAngle(), sector.getSweepAngle(), true, paint);
        }
    }
}
