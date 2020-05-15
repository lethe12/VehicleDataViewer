package com.grean.vehicledataviewer.Sensor;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * Created by weifeng on 2020/5/8.
 */

public class SensorData {
    public static final float TVocRange = 5f;
    private double lat,lng,alt;
    private float tvocData;
    private double sumTVoc;
    private int sumTimes;
    private LatLng latLng;

    public void converterCoordinate(){
        CoordinateConverter converter = new CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(new LatLng(lat,lng));
        latLng =  converter.convert();
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void calcSum(){
        sumTVoc+=tvocData;
        sumTimes++;
    }

    public double getMeanTVoc(){
        if(sumTimes>0){
            return sumTVoc/sumTimes;
        }else{
            return 0;
        }
    }

    public void clearSum(){
        sumTVoc = 0;
        sumTimes = 0;
    }

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
