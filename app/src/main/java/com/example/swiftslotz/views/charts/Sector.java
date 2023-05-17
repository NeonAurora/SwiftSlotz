package com.example.swiftslotz.views.charts;

public class Sector {
    private float startAngle;
    private float sweepAngle;
    private int color;
    private String title;
    private String time;

    public Sector(float startAngle, float sweepAngle, int color, String title, String time) {
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        this.color = color;
        this.title = title;
        this.time = time;
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

    public String getTitle() { return title; }
    public String getTime() { return time; }
}
