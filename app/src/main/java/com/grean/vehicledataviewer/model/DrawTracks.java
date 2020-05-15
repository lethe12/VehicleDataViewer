package com.grean.vehicledataviewer.model;

import android.graphics.Bitmap;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.grean.vehicledataviewer.R;
import com.grean.vehicledataviewer.Sensor.SensorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2020/5/9.
 */

public class DrawTracks {
    private Overlay localPoint,polyLines,startPoint,endPoint;
    private BitmapDescriptor localMark,startMark,endMark;
    private BaiduMap baiduMap;
    private LatLng lastLatLng;

    public DrawTracks(BaiduMap baiduMap){
        this.baiduMap = baiduMap;
        localMark = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        startMark = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
        endMark = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
    }

    public void drawTracks(TrackFormat format){
        baiduMap.clear();

        LatLng latLngStart = format.getPoints().get(0);
        setStartPoint(latLngStart);

        LatLng latLngEnd = format.getPoints().get(format.getSize());
        setEndPoint(latLngEnd);

        LatLngBounds latLngBounds = new LatLngBounds.Builder().include(latLngStart).include(latLngEnd).build();
        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLngBounds,200,200,200,200);
        baiduMap.setMapStatus(statusUpdate);

        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(0xAAFF0000).points(format.getPoints()).colorsValues(format.getColors());
        polyLines = baiduMap.addOverlay(polylineOptions);
    }

    public void addNewLine(SensorData data){
        int color  = TrackFormat.dataToColor(data.getMeanTVoc());
        List<LatLng> local = new ArrayList<>();
        local.add(lastLatLng);
        local.add(data.getLatLng());
        List<Integer> colorList = new ArrayList<>();
        colorList.add(color);
        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(0xAAFF0000).points(local).colorsValues(colorList);
        polyLines = baiduMap.addOverlay(polylineOptions);
        newLocalPoint(data.getLatLng());
        //lastLatLng = new LatLng(data.getLatLng().latitude,data.getLatLng().longitude);
    }


    public void clearMap(){
        baiduMap.clear();
    }

    private void setStartPoint(LatLng latLng){
        OverlayOptions options = new MarkerOptions().position(latLng).icon(startMark).draggable(true).flat(true).alpha(0.5f);
        startPoint = baiduMap.addOverlay(options);
    }

    private void setEndPoint(LatLng latLng){
        OverlayOptions options = new MarkerOptions().position(latLng).icon(endMark).draggable(true).flat(true).alpha(0.5f);
        endPoint = baiduMap.addOverlay(options);
    }

    public void newLocalPoint(LatLng latLng){
        if(localPoint!=null){
            localPoint.remove();
        }
        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng,19);
        baiduMap.setMapStatus(statusUpdate);

        OverlayOptions options = new MarkerOptions().position(latLng).icon(localMark).draggable(true).flat(true).alpha(0.5f);
        localPoint = baiduMap.addOverlay(options);

        lastLatLng = new LatLng(latLng.latitude,latLng.longitude);
    }
}
