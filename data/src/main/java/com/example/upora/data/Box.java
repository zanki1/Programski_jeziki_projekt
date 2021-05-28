package com.example.upora.data;

import java.time.LocalDateTime;

public class Box {
    private int boxID;
    private String dateStamp;
    private boolean opened;
    private float longitude;
    private float latitude;

    /*public Box(){
        this.boxID = 0;
        this.dateStamp = null;
        this.opened = false;
        this.longitude = 0;
        this.latitude = 0;
    }*/
    public Box(){
    }

    public Box(int boxID, String dateStamp, boolean opened, float longitude, float latitude) {
        this.boxID = boxID;
        this.dateStamp = dateStamp;
        this.opened = opened;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getBoxID() {
        return boxID;
    }

    public void setBoxID(int boxID) {
        this.boxID = boxID;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Box{" +
                "boxID=" + boxID +
                ", dateStamp='" + dateStamp + '\'' +
                ", opened=" + opened +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}