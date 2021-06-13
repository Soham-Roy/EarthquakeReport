package com.example.quakereport;

public class Earthquake {

    private String mDirection;
    private long mTime;
    private float mMagnitude;
    private String url;

    public Earthquake(String dir, long time, float mag, String url){
        mDirection = dir;
        mTime = time;
        mMagnitude = mag;
        this.url = url;
    }

    public float getMagnitude() {
        return mMagnitude;
    }

    public String getDirection() {
        return mDirection;
    }

    public long getTime() {
        return mTime;
    }

    public String getUrl() {
        return url;
    }
}
