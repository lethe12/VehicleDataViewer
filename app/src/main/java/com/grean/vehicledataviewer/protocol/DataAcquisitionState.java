package com.grean.vehicledataviewer.protocol;

import android.util.Log;

import com.grean.vehicledataviewer.Sensor.SensorData;

import org.json.JSONException;

/**
 * Created by weifeng on 2020/5/8.
 */

public class DataAcquisitionState implements ProtocolState{
    private static final String tag = "DataAcquisitionState";
    private ProtocolCommand command;
    private SensorData data;

    public DataAcquisitionState(ProtocolCommand command){
        this.command = command;
    }



    @Override
    public void handleReceiveBuff(byte[] buff, int length) {
        String string = new String(buff,0,length);
        //Log.d(tag,string);
        if(JSON.isFrameRight(string)){
            try {
                if(GetProtocols.getInstance().getProtocolInfo()!=null) {
                    JSON.handleJsonString(string.substring(string.indexOf("$$") + 2, string.indexOf("$#$#")), GetProtocols.getInstance().getProtocolInfo());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{

        }
    }

    @Override
    public boolean isConnect() {
        return command.isConnected();
    }

    @Override
    public void getRealTimeData() {
        try {
            command.executeSendTask(JSON.getRealTimeCommand());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setConfig(UploadingConfigFormat format) {

    }


    @Override
    public void handleNetError() {

    }

    @Override
    public void handleNewConnect() {

    }

    @Override
    public void setSensorData(SensorData data) {
        this.data = data;
    }
}
