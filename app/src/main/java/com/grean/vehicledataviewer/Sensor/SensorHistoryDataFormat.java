package com.grean.vehicledataviewer.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2020/5/14.
 */

public class SensorHistoryDataFormat {
    private List<Double> lats = new ArrayList<>(),lngs = new ArrayList<>();
    private List<Float> TVocs = new ArrayList<>();
    private List<Long> times = new ArrayList<>();
    private List<Integer> ids = new ArrayList<>();

    public void addOnePoint(int id,long time,float data,double lat,double lng){
        ids.add(id);
        times.add(time);
        TVocs.add(data);
        lats.add(lat);
        lngs.add(lng);
    }

    public int getSize(){
        return ids.size();
    }

    public List<Double> getLat() {
        return lats;
    }

    public List<Double> getLng() {
        return lngs;
    }

    public List<Float> getTVoc() {
        return TVocs;
    }

    public List<Long> getTime() {
        return times;
    }

    public List<Integer> getId() {
        return ids;
    }
}
