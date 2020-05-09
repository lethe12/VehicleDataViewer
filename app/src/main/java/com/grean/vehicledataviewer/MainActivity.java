package com.grean.vehicledataviewer;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.grean.vehicledataviewer.Sensor.SensorData;
import com.grean.vehicledataviewer.model.LocalServerManger;
import com.grean.vehicledataviewer.model.ScanSensor;
import com.grean.vehicledataviewer.presenter.DialogProcessFragmentBarStyle;
import com.grean.vehicledataviewer.presenter.LocalServerListener;
import com.grean.vehicledataviewer.presenter.MainDisplayListener;
import com.grean.vehicledataviewer.protocol.GetProtocols;
import com.wifi.WifiAdmin;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainDisplayListener,View.OnClickListener,LocalServerListener{
    private static final String tag = "MainActivity";
    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private ScanSensor scanSensor;
    private SensorData data;
    private TextView tvDebug;
    private LocalServerManger localServerManger;
    private DialogProcessFragmentBarStyle dialog;
    private String stringToast;
    private boolean scanResult;
    private static final int msgDismissDialog = 1,msgRealTimeData = 2,msgToast = 3;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgDismissDialog:
                    if(scanResult){
                        Toast.makeText(MainActivity.this,"连接成功!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this,"连接失败!",Toast.LENGTH_SHORT).show();
                    }
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
                case msgRealTimeData:
                    tvDebug.setText("Real time data is: TVoc="+String.valueOf(data.getTvocData())+"Lng="+String.valueOf(data.getLng())+"Lat="+String.valueOf(data.getLat()));
                    break;
                case msgToast:
                    Toast.makeText(MainActivity.this,stringToast,Toast.LENGTH_SHORT).show();
                    break;
                default:

                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止自动锁屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        setContentView(R.layout.activity_main);
        initView();

        localServerManger = new LocalServerManger(this,this);
        localServerManger.scanServer();
        mMapView = (MapView) findViewById(R.id.bmapView);
        scanSensor = new ScanSensor(this, GetProtocols.getInstance().getProtocolState());
        GetProtocols.getInstance().setProtocolInfo(scanSensor);

        baiduMap = mMapView.getMap();

        setLocalCity();
        markPoint(30.372844,120.155479);
    }

    private void setLocalCity(){

        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(30.372844,120.155479));

        baiduMap.setMapStatus(statusUpdate);
    }

    private void initView(){
        tvDebug = (TextView) findViewById(R.id.tvDebug);
        findViewById(R.id.btnDebug).setOnClickListener(this);
    }

    private LatLng conversionCoordinate(double lat,double lng){
        CoordinateConverter converter = new CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(new LatLng(lat,lng));
        return converter.convert();
    }

    private void markPoint(double lat,double lng){
        LatLng point = new LatLng(lat,lng);
        CoordinateConverter converter = new CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(point);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
        OverlayOptions options = new MarkerOptions().position(converter.convert()).icon(bitmap).draggable(true).flat(true).alpha(0.5f);
        baiduMap.addOverlay(options);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onRealTimeResult(SensorData data) {
        this.data = data;
        handler.sendEmptyMessage(msgRealTimeData);
    }

    @Override
    public void onToast(String string) {
        stringToast = string;
        handler.sendEmptyMessage(msgToast);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDebug:
                dialog = new DialogProcessFragmentBarStyle();
                dialog.show(getFragmentManager(),"show local server manger");
                localServerManger.startConnectToServer(dialog);
                break;
            default:

                break;
        }
    }

    @Override
    public void onComplete(boolean result) {
        this.scanResult = result;
        handler.sendEmptyMessage(msgDismissDialog);
    }

    @Override
    public void readyToScan() {
        scanSensor.starScan();
    }
}
