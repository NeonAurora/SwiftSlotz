package com.example.swiftslotz.views.charts;

public class Sector {
    private float startAngle;
    private float sweepAngle;
    private int colorAM;
    private int colorPM;
    private String title;
    private String time;

    public Sector(float startAngle, float sweepAngle, int colorAM, int colorPM, String title, String time) {
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        this.colorAM = colorAM;
        this.colorPM = colorPM;
        this.title = title;
        this.time = time;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public int getColorAM() {
        return colorAM;
    }

    public int getColorPM() {
        return colorPM;
    }

    public String getTitle() { return title; }
    public String getTime() { return time; }
}
