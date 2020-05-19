package com.grean.vehicledataviewer.model;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.grean.vehicledataviewer.presenter.LocalServerListener;
import com.grean.vehicledataviewer.presenter.NotifyProcessDialogInfo;
import com.grean.vehicledataviewer.protocol.GetProtocols;
import com.grean.vehicledataviewer.protocol.ProtocolTcpServer;
import com.grean.vehicledataviewer.protocol.UploadingConfigFormat;
import com.wifi.WifiAdmin;

import java.util.List;

/**
 * Created by weifeng on 2020/5/8.
 */

public class LocalServerManger {
    private static final String tag = "LocalServerManger";
    private LocalServerListener listener;
    private Context context;
    private WifiAdmin wifiAdmin;
    private UploadingConfigFormat format = new UploadingConfigFormat();

    public LocalServerManger (Context context,LocalServerListener listener){
        this.listener= listener;
        this.context = context;
        wifiAdmin = new WifiAdmin(context);
    }

    public void scanServer(){
        wifiAdmin.openWifi();
        wifiAdmin.startScan();
        List<ScanResult> list = wifiAdmin.getWifiList();
        for(int i=0;i<list.size();i++){
            Log.d(tag,list.get(i).SSID);
            if(list.get(i).SSID.equals("greanAir")){
                wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("greanAir","1234567890",3));
                break;
            }
        }
    }

    private boolean isServerAvailable(){
        String [] wifiInfo = wifiAdmin.getWifiInfo().split(",");
        if(wifiInfo[0].equals("SSID: greanAir")){
            return true;
        }else{
            return false;
        }
    }

    public void startConnectToServer(NotifyProcessDialogInfo info){
        new Connect(info).start();
    }

    private class Connect extends Thread{
        private int times;
        private NotifyProcessDialogInfo info;
        public Connect(NotifyProcessDialogInfo info){
            this.info = info;
        }

        @Override
        public void run() {
            super.run();
            info.showProcess(5);
            if(!isServerAvailable()) {
                //Log.d(tag,"启用新连接");
                scanServer();
            }
            times = 0;
            do{
                info.showInfo("正在连接设备...");
                times++;
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while ((!isServerAvailable())&&(times < 10));
            info.showProcess(35);
            if(times < 10){
                //开始连接服务器
                if (ProtocolTcpServer.getInstance().getFormat()==null){
                    Log.d(tag,"new connected");
                    format.setServerAddress("192.168.43.1");
                    format.setServerPort(8888);
                    ProtocolTcpServer.getInstance().setConfig(format);
                    ProtocolTcpServer.getInstance().setState(GetProtocols.getInstance().getProtocolState());
                    ProtocolTcpServer.getInstance().connectServer(context);
                    try {
                        sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }/*else {
                    if (!ProtocolTcpServer.getInstance().isConnected()) {
                        Log.d(tag,"disconnected and start new");
                        format.setServerAddress("192.168.43.1");
                        format.setServerPort(8888);
                        ProtocolTcpServer.getInstance().setConfig(format);
                        ProtocolTcpServer.getInstance().setState(GetProtocols.getInstance().getProtocolState());
                        ProtocolTcpServer.getInstance().connectServer(context);
                        try {
                            sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }*/
                info.showProcess(65);
                listener.readyToScan();

                //listener.onComplete(true);
            }else{//超时
                listener.onComplete(false);
            }

        }
    }
}
