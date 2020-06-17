package com.grean.vehicledataviewer.model;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.grean.vehicledataviewer.R;
import com.grean.vehicledataviewer.Sensor.SensorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2020/5/9.
 */

public class DrawTracks implements BaiduMap.OnMapStatusChangeListener{
    private static final String tag = "DrawTracks";
    private static final int msgShowDetail=1,msgHideDetail=2;
    private Overlay localPoint,polyLines,startPoint,endPoint;
    private  List<Overlay>detailStrings;
    private BitmapDescriptor localMark,startMark,endMark;
    private TrackFormat lastFormat;
    private BaiduMap baiduMap;
    private LatLng lastLatLng;
    private boolean hasShowDetail = false,hasNewTracks=false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case msgHideDetail:
                    hasShowDetail = false;
                    if(detailStrings!=null) {
                        for (int i = 0; i < detailStrings.size(); i++) {
                            detailStrings.get(i).remove();
                        }
                    }
                    break;
                case msgShowDetail:
                    hasShowDetail = true;
                    String detail;
                    List<OverlayOptions> list = new ArrayList<>();
                    for(int i=0;i<lastFormat.getSize();i++){
                        detail = lastFormat.getDateList().get(i);//时间
                        detail += " "+lastFormat.getDataList().get(i).get(0)+"ppm";
                        LatLng latLng = lastFormat.getPoints().get(i);
                        OverlayOptions mTextOptions = new TextOptions()
                                .text(detail) //文字内容
                                .bgColor(0xAAFFFF00) //背景色
                                .fontSize(24) //字号
                                .fontColor(0xFFFF00FF) //文字颜色
                                .position(latLng);
                        list.add(mTextOptions);
                    }
                    detailStrings = baiduMap.addOverlays(list);
                    break;
                default:
                    break;
            }
        }
    };

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
        hasNewTracks = true;
        lastFormat = format;
    }

    public void addNewLine(float data,LatLng newLatLng){
        int color  = TrackFormat.dataToColor(data);
        List<LatLng> local = new ArrayList<>();
        local.add(lastLatLng);
        local.add(newLatLng);
        List<Integer> colorList = new ArrayList<>();
        colorList.add(color);
        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(0xAAFF0000).points(local).colorsValues(colorList);
        polyLines = baiduMap.addOverlay(polylineOptions);
        newLocalPoint(newLatLng);
        hasNewTracks =false;
        hasShowDetail = false;
        //lastLatLng = new LatLng(data.getLatLng().latitude,data.getLatLng().longitude);
    }


    public void clearMap(){
        baiduMap.clear();
        hasNewTracks =false;
        hasShowDetail = false;
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

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {

        if(hasNewTracks) {//只在显示历史曲线时显示细节
            Log.d(tag,String.valueOf(hasNewTracks)+String.valueOf(mapStatus.zoom));
                if (hasShowDetail) {//已经显示细节
                    if (mapStatus.zoom < 17.5) {
                        handler.sendEmptyMessage(msgHideDetail);
                    }
                }else {//未显示细节
                    if (mapStatus.zoom > 18.5) {
                        handler.sendEmptyMessage(msgShowDetail);
                    }
            }
        }
    }
}
