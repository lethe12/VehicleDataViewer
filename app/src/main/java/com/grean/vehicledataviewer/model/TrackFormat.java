package com.grean.vehicledataviewer.model;


import com.baidu.mapapi.model.LatLng;
import com.github.mikephil.charting.data.Entry;
import com.grean.vehicledataviewer.Sensor.SensorData;
import com.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2020/5/9.
 */

public class TrackFormat {
    private static final String tag = "TrackFormat";
    private List<LatLng> points = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    //private ColorsConverter colorsConverter = new ColorsConverter();
    private List<String> xDataList = new ArrayList<>();
    private List<Entry> yDataList = new ArrayList<Entry>();
    private List<String> dateList = new ArrayList<>();
    private List<List<String>> dataList=new ArrayList<>();

    public List<String> getxDataList() {
        return xDataList;
    }

    public List<Entry> getyDataList() {
        return yDataList;
    }

    public List<String> getDateList() {
        return dateList;
    }

    public List<List<String>> getDataList() {
        return dataList;
    }

    public void setStartPoint(double lat, double lng){
        points.clear();
        colors.clear();
        LatLng latLng = new LatLng(lat,lng);
        points.add(latLng);
    }

    public int getSize(){
        return colors.size();
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public static int dataToColor(double data){
        double waveLength = data/SensorData.TVocRange*(RgbCalculator.LEN_MAX-RgbCalculator.LEN_MIN)+RgbCalculator.LEN_MIN;
        int color = RgbCalculator.Calc(waveLength);
        //Log.d(tag,"data = "+String.valueOf(data)+";waveLength"+String.valueOf(waveLength));
        //Log.d(tag,"color="+Integer.toHexString(color));
        return color;
    }



    public void addOnePoint(double lat, double lng, float data,long date,int id){
        LatLng latLng = new LatLng(lat,lng);
        points.add(latLng);
        double waveLength = data/SensorData.TVocRange*(RgbCalculator.LEN_MAX-RgbCalculator.LEN_MIN)+RgbCalculator.LEN_MIN;
        int color = RgbCalculator.Calc(waveLength);
        //Log.d(tag,"data = "+String.valueOf(data)+";waveLength"+String.valueOf(waveLength));
        //Log.d(tag,"color="+Integer.toHexString(color));
        colors.add(color);
        xDataList.add(tools.timeToChartString(date));
        yDataList.add(new Entry(id,data));
        dateList.add(tools.timeToChartString(date));
        List<String> item = new ArrayList<>();
        item.add(tools.float2String4(data));
        item.add(tools.float2String4((float) lat));
        item.add(tools.float2String4((float) lng));
        dataList.add(item);
        //colorsConverter.setH((int) (data / rang * 359));
        //colorsConverter.converter();
        //colors.add(colorsConverter.getColor());
    }
    /** HSV转RGB
     * h = 0~359 s=0~1 v= 0~1

    private class ColorsConverter{
        private float r,g,b,s=0.5f,v=0.5f;
        private int h;

        public void converter(){
            int hi = Math.floorMod( (h/60),6);
            float f = h/60-hi;
            float p = v*(1-s);
            float q = v*(1-f*s);
            float t = v*(1-(1-f)*s);
            switch (hi){
                case 0:
                    r = v;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = v;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = v;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = v;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = v;
                    break;
                case 5:
                    r = v;
                    g = p;
                    b = q;
                    break;
                default:
                    break;
            }
        }

        public int getColor(){
            int result = 0xaa000000;
            int iR= (int) (r*255)<<16;
            int iG= (int) (g*255)<<8;
            int iB= (int) (b*255);
            return result+iR+iG+iB;
        }

        public void setH(int h) {
            this.h = h;
        }

        public void setS(float s) {
            this.s = s;
        }

        public void setV(float v) {
            this.v = v;
        }

    }*/

}
