package com.example.upora.data;

import java.time.LocalDateTime;

public class Box {
    private int boxID;
    private LocalDateTime dateStamp;
    private boolean opened;
    private float longitude;
    private float latitude;

    public Box(int boxID, LocalDateTime dateStamp, boolean opened, float longitude, float latitude) {
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

    public LocalDateTime getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(LocalDateTime dateStamp) {
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
}