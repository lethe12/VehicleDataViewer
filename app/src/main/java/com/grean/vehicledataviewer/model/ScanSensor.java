package com.grean.vehicledataviewer.model;

import android.content.Context;
import android.util.Log;

import com.grean.vehicledataviewer.Sensor.ScanStoreData;
import com.grean.vehicledataviewer.Sensor.SensorData;
import com.grean.vehicledataviewer.presenter.LocalServerListener;
import com.grean.vehicledataviewer.presenter.MainDisplayListener;
import com.grean.vehicledataviewer.protocol.DataAcquisitionState;
import com.grean.vehicledataviewer.protocol.ProtocolCommand;
import com.grean.vehicledataviewer.protocol.ProtocolInfo;
import com.grean.vehicledataviewer.protocol.ProtocolState;
import com.grean.vehicledataviewer.protocol.ProtocolTcpServer;
import com.tools;

import java.util.List;

/**
 * Created by weifeng on 2020/5/8.
 */

public class ScanSensor implements ProtocolInfo{
    private static final String tag = "ScanSensor";
    private SensorData data = new SensorData();
    private MainDisplayListener listener;
    private ProtocolState state;
    private boolean run=false;
    private long now,last;
    private static final  long interval = 20000l;
    private ScanStoreData storeData;
    private double lastLat = 0,lastLng = 0;
    private float lastData=0;

    public ScanSensor(MainDisplayListener listener, ProtocolState state, Context context){
        this.listener = listener;
        this.state = state;
        this.state.setSensorData(data);
        storeData = new ScanStoreData(context);
    }

    /**
     * 模拟位置改变
     */
    private void simulateMoving(){
        if(lastLat == 0){
            lastLat = data.getLat();
            lastLng = data.getLng();
        }
        double newLat = lastLat+ Math.random()*.002-0.001;;
        double newLng = lastLng+ Math.random()*.002-0.001;
        lastLat = newLat;
        lastLng = newLng;
        data.setLat(newLat);
        data.setLng(newLng);
        data.converterCoordinate();
    }

    private void simulateData(){
        lastData+=0.02;
        if(lastData >= SensorData.TVocRange){
            lastData = SensorData.TVocRange;
        }
        data.setTvocData(lastData);
    }

    public boolean starScan(LocalServerListener localServerListener){
        if(!run){
            new Scan(localServerListener).start();
            return true;
        }else{
            return false;
        }
    }

    public boolean isRun() {
        return run;
    }

    public void stopScan(){
        if(run){
            run = false;
        }
    }

    public boolean exportDataToFile(String trackName,String fileName){
        return storeData.exportToFile(trackName,fileName);
    }

    private class Scan extends Thread{
        private LocalServerListener localServerListener;

        protected Scan(LocalServerListener localServerListener){
            this.localServerListener = localServerListener;
        }

        private void scan(){
            localServerListener.onComplete(true);
            state.getRealTimeData();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            listener.onRealTimeResult(data);
            data.calcSum();
            listener.setFirstPoint(data);
            now = tools.nowtime2timestamp();
            last = now+interval;
            storeData.savePoint(now,data);
            Log.d(tag,"now="+tools.timeStamp2TcpString(now)+"last="+tools.timeStamp2TcpString(last));
            run = true;
            while (run) {

                state.getRealTimeData();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                listener.onRealTimeResult(data);
                simulateData();
                data.calcSum();
                now = tools.nowtime2timestamp();
                if(now > last){
                    if(data.getLat()!=0) {
                        simulateMoving();
                        listener.addPoint(data);
                        //Log.d(tag, "new point"+String.valueOf(data.getMeanTVoc()));
                        storeData.savePoint(last,data);
                        data.clearSum();
                    }
                    last += interval;
                }
            }
            data.clearSum();
            storeData.saveTrack(now);
            //listener.onToast("停止走航!");
        }




        @Override
        public void run() {
            int times=0;
            while (!state.isConnect()&&(times < 30)) {
                times++;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(times < 30){
                scan();
            }else{
                localServerListener.onComplete(false);
            }

        }

    }

    public void saveTrackCache(){
        long now = tools.nowtime2timestamp();
        storeData.saveTrack(now);
    }

    public int getTrackCacheSize(){
        return storeData.getTrackCacheSize();
    }

    public void deleteTrack(String name){
        storeData.deleteTrack(name);
    }

    public void deleteTracks(String[] names){
        for (int i=0;i<names.length;i++){
            storeData.deleteTrack(names[i]);
        }
    }

    public TrackFormat getTrack(String tableName){
        return storeData.getTrack(tableName);
    }

    public String[] getTrackListStrings(){

        List<String> list = storeData.getTrackList();
        if(list.size()>0) {
            String[] trackStrings = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                trackStrings[i] = list.get(i);
            }
            return trackStrings;
        }else{
            return null;
        }
    }

    @Override
    public SensorData getSensorData() {
        return data;
    }
}
