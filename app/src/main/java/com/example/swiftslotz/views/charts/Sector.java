package com.example.swiftslotz.views.charts;

public class Sector {
    private float startAngle;
    private float sweepAngle;
    private int color;

    public Sector(float startAngle, float sweepAngle, int color) {
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        this.color = color;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public int getColor() {
        return color;
    }
}
