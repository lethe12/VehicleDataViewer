package com.grean.vehicledataviewer.Sensor;

/**
 * Created by weifeng on 2020/5/8.
 */

public class SensorData {
    private double lat,lng,alt;
    private float tvocData;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public float getTvocData() {
        return tvocData;
    }

    public void setTvocData(float tvocData) {
        this.tvocData = tvocData;
    }
}
