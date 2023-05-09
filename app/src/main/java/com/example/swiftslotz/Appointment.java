package com.example.swiftslotz;

public class Appointment {
    private String title;
    private String date;
    private String time;
    private String details;
    public Appointment() {
    }
    public Appointment(String title, String date, String time, String details) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetails() {return details;}
    public void setDetails(String details) {
        this.details = details;
    }
}

