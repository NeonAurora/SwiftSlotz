package com.example.swiftslotz.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Appointment implements Serializable {
    private int id;
    private String title;
    private String date;
    private String time;

    private String details;
    private String key;
    private int durationInMinutes;

    private String requestingUserFirebaseKey;
    public String hostUserFirebaseKey;
    private List<String> involvedUsers;

    private String status;
    public List<String> imageUrls;
    private Integer timeConstraintInMinutes;
    public Appointment() {
        imageUrls = new ArrayList<>();
    }
    private long timeToStart;
    public int linearProgressPercentage;
    public int circularProgressPercentage;
    private long creationTimestamp;
    private boolean linearProgressVisible;
    private boolean circularProgressVisible;
    public Appointment( String title, String date, String time, String details) {


        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
    }


    public Appointment( String title, String date, String time, String details, int durationInMinutes) {


        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
        this.durationInMinutes = durationInMinutes;
    }

    public Appointment( String title, String date, String time, String details, int durationInMinutes, String status) {


        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
        this.durationInMinutes = durationInMinutes;
        this.status = status;
    }

    public int getId() {
        return id;
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

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }
    public int getDuration() {
        return durationInMinutes;
    }

    public void setDuration(int duration) {
        this.durationInMinutes = duration;
    }

    public String getRequestingUserFirebaseKey() {
        return requestingUserFirebaseKey;
    }

    public void setRequestingUserFirebaseKey(String requestingUserFirebaseKey) {
        this.requestingUserFirebaseKey = requestingUserFirebaseKey;
    }

    public String getHostUserFirebaseKey() {
        return hostUserFirebaseKey;
    }

    public void setHostUserFirebaseKey(String hostUserFirebaseKey) {
        this.hostUserFirebaseKey = hostUserFirebaseKey;
    }

    public List<String> getInvolvedUsers() {
        return involvedUsers;
    }

    public void setInvolvedUsers(List<String> involvedUsers) {
        this.involvedUsers = involvedUsers;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public List<String> getImageUrls() {
        return imageUrls;
    }
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    public Integer getTimeConstraintInMinutes() {
        return timeConstraintInMinutes;
    }
    public void setTimeConstraintInMinutes(Integer timeConstraintInMinutes) {
        this.timeConstraintInMinutes = timeConstraintInMinutes;
    }
    public long getTimeToStart() {
        return timeToStart;
    }
    public void setTimeToStart(long timeToStart) {
        this.timeToStart = timeToStart;
    }
    public int getLinearProgressPercentage() {
        return linearProgressPercentage;
    }

    public void setLinearProgressPercentage(int linearProgressPercentage) {
        this.linearProgressPercentage = linearProgressPercentage;
    }
    public long getCreationTimestamp() {
        return creationTimestamp;
    }
    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
    public boolean isLinearProgressVisible() {
        return linearProgressVisible;
    }
    public void setLinearProgressVisible(boolean linearProgressVisible) {
        this.linearProgressVisible = linearProgressVisible;
    }
    public int getCircularProgressPercentage() {
        return circularProgressPercentage;
    }
    public void setCircularProgressPercentage(int circularProgressPercentage) {
        this.circularProgressPercentage = circularProgressPercentage;
    }
    public boolean isCircularProgressVisible() {
        return circularProgressVisible;
    }
    public void setCircularProgressVisible(boolean circularProgressVisible) {
        this.circularProgressVisible = circularProgressVisible;
    }
}
