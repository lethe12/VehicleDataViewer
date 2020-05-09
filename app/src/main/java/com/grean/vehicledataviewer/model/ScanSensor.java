package com.grean.vehicledataviewer.model;

import com.grean.vehicledataviewer.Sensor.SensorData;
import com.grean.vehicledataviewer.presenter.MainDisplayListener;
import com.grean.vehicledataviewer.protocol.DataAcquisitionState;
import com.grean.vehicledataviewer.protocol.ProtocolCommand;
import com.grean.vehicledataviewer.protocol.ProtocolInfo;
import com.grean.vehicledataviewer.protocol.ProtocolState;
import com.grean.vehicledataviewer.protocol.ProtocolTcpServer;

/**
 * Created by weifeng on 2020/5/8.
 */

public class ScanSensor implements ProtocolInfo{
    private SensorData data = new SensorData();
    private MainDisplayListener listener;
    private ProtocolState state;
    private boolean run=false;

    public ScanSensor(MainDisplayListener listener, ProtocolState state){
        this.listener = listener;
        this.state = state;
        this.state.setSensorData(data);
    }

    public boolean starScan(){
        if(!run){
            new Scan().start();
            return true;
        }else{
            return false;
        }
    }

    public void stopScan(){
        if(run){
            run = false;
        }
    }

    private class Scan extends Thread{
        @Override
        public void run() {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listener.onToast("开始走航!");
            run = true;
            while (run) {

                state.getRealTimeData();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listener.onRealTimeResult(data);
            }
            listener.onToast("停止走航!");
        }

    }

    @Override
    public SensorData getSensorData() {
        return data;
    }
}
