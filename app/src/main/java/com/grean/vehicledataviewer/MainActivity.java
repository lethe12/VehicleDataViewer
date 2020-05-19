package com.grean.vehicledataviewer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.grean.vehicledataviewer.Sensor.SensorData;
import com.grean.vehicledataviewer.model.DrawTracks;
import com.grean.vehicledataviewer.model.LocalServerManger;
import com.grean.vehicledataviewer.model.ScanSensor;
import com.grean.vehicledataviewer.model.TrackFormat;
import com.grean.vehicledataviewer.presenter.DialogProcessFragmentBarStyle;
import com.grean.vehicledataviewer.presenter.LocalServerListener;
import com.grean.vehicledataviewer.presenter.MainDisplayListener;
import com.grean.vehicledataviewer.protocol.GetProtocols;
import com.tools;

public class MainActivity extends AppCompatActivity implements MainDisplayListener,View.OnClickListener,LocalServerListener{
    private static final String tag = "MainActivity";
    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private ScanSensor scanSensor;
    private SensorData data;
    private TextView tvDebug;
    private LocalServerManger localServerManger;
    private DrawTracks drawTracks;

    private DialogProcessFragmentBarStyle dialog;
    private String stringToast,stringTableName;
    private Button btnOperateScan,btnSearchData,btnDeleteAllData,btnExportData;
    private boolean scanResult;
    private static final int msgDismissDialog = 1,msgRealTimeData = 2,
            msgToast = 3,msgNewPoint=4,msgNewTrack=5,msgSetFirstPoint=6,
    msgNewConnect=7;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgDismissDialog:
                    if(scanResult){
                        Toast.makeText(MainActivity.this,"连接成功!",Toast.LENGTH_SHORT).show();
                        btnOperateScan.setText("停止走航");
                        btnDeleteAllData.setEnabled(false);
                        btnSearchData.setEnabled(false);
                        btnExportData.setEnabled(false);
                    }else{
                        Toast.makeText(MainActivity.this,"连接失败!",Toast.LENGTH_SHORT).show();

                    }
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
                case msgRealTimeData:
                    tvDebug.setText("Real time data\r\nTVoc="+ tools.float2String4(data.getTvocData())
                            +"\r\nLng="+String.valueOf(data.getLng())+"\r\nLat="+String.valueOf(data.getLat()));
                    break;
                case msgToast:
                    Toast.makeText(MainActivity.this,stringToast,Toast.LENGTH_SHORT).show();
                    break;
                case msgSetFirstPoint:
                    drawTracks.newLocalPoint(data.getLatLng());
                    break;
                case msgNewPoint:
                    drawTracks.addNewLine(data);
                    break;
                case msgNewTrack:
                    TrackFormat format = scanSensor.getTrack(stringTableName);
                    drawTracks.drawTracks(format);
                    break;
                case msgNewConnect:
                    dialog = new DialogProcessFragmentBarStyle();
                    dialog.show(getFragmentManager(), "show local server manger");
                    localServerManger.startConnectToServer(dialog);
                    break;
                default:
                    break;
            }
        }
    };

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {
        new android.support.v7.app.AlertDialog.Builder(this).setTitle("系统权限不可用")
                .setMessage("由于在线扬尘需要获权限；\n否则，您将无法正常使用")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        PackageManager pm = getPackageManager();
        PackageInfo pack = null;
        try {
            pack = pm.getPackageInfo("com.grean.vehicledataviewer", PackageManager.GET_PERMISSIONS);
            String[] permissionStrings = pack.requestedPermissions;
            ActivityCompat.requestPermissions(this, permissionStrings, 321);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止自动锁屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏
        setContentView(R.layout.activity_main);
        initView();

        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.ACCESS_COARSE_LOCATION",
                        "com.grean.vehicledataviewer"));
        if (!permission) {
            showDialogTipUserRequestPermission();
        }

        localServerManger = new LocalServerManger(this,this);
        localServerManger.scanServer();
        mMapView = (MapView) findViewById(R.id.bmapView);
        scanSensor = new ScanSensor(this, GetProtocols.getInstance().getProtocolState(),this);
        GetProtocols.getInstance().setProtocolInfo(scanSensor);

        baiduMap = mMapView.getMap();
        drawTracks = new DrawTracks(baiduMap);
        drawTracks.newLocalPoint(conversionCoordinate(30.372844,120.155479));

        if(scanSensor.getTrackCacheSize()>0){//有未完成走航
            new AlertDialog.Builder(this).setTitle("存在未完成走航,是否继续?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.sendEmptyMessage(msgNewConnect);
                }
            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scanSensor.saveTrackCache();
                }
            }).show();
        }

    }



    private void initView(){
        tvDebug = (TextView) findViewById(R.id.tvDebug);
        findViewById(R.id.btnDebug).setOnClickListener(this);
        btnSearchData = (Button) findViewById(R.id.btnSearchDatabase);
        btnSearchData.setOnClickListener(this);
        btnOperateScan = (Button) findViewById(R.id.btnOperateScan);
        btnOperateScan.setOnClickListener(this);
        btnDeleteAllData = (Button) findViewById(R.id.btnDeleteAllData);
        btnDeleteAllData.setOnClickListener(this);
        btnExportData = (Button) findViewById(R.id.btnExportData);
        btnExportData.setOnClickListener(this);
    }

    private LatLng conversionCoordinate(double lat,double lng){
        CoordinateConverter converter = new CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(new LatLng(lat,lng));
        return converter.convert();
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
    public void setFirstPoint(SensorData data) {
        this.data = data;
        handler.sendEmptyMessage(msgSetFirstPoint);
    }

    @Override
    public void addPoint(SensorData data) {
        this.data = data;
        handler.sendEmptyMessage(msgNewPoint);
    }

    @Override
    public void onClick(View v) {
        String[] strings;
        switch (v.getId()){
            case R.id.btnOperateScan:
                if(scanSensor.isRun()){
                    Toast.makeText(this,"停止走航",Toast.LENGTH_SHORT).show();
                    scanSensor.stopScan();
                    btnOperateScan.setText("开始走航");
                    btnDeleteAllData.setEnabled(true);
                    btnSearchData.setEnabled(true);
                    btnExportData.setEnabled(true);
                }else {
                    drawTracks.clearMap();
                    dialog = new DialogProcessFragmentBarStyle();
                    dialog.show(getFragmentManager(), "show local server manger");
                    localServerManger.startConnectToServer(dialog);
                }
                break;
            case R.id.btnExportData:
                strings = scanSensor.getTrackListStrings();
                new AlertDialog.Builder(this).setTitle("选择导出走航数据").setSingleChoiceItems(strings, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stringTableName = strings[which];
                        String fileName = "走航数据"+stringTableName+"_Export"+tools.nowTime2FileString();
                        if(scanSensor.exportDataToFile(stringTableName,fileName)){
                            Toast.makeText(MainActivity.this,"导出成功,路径为 /Grean/"+fileName+".txt",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"导出失败!",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.btnDebug:

                break;
            case R.id.btnDeleteAllData:
                new AlertDialog.Builder(this).setTitle("是否清空历史数据?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] strings = scanSensor.getTrackListStrings();
                        if(strings.length>0) {
                            scanSensor.deleteTracks(strings);
                            Toast.makeText(MainActivity.this,"已清空所有数据",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"无历史数据",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("否", null).show();
                break;
            case R.id.btnSearchDatabase:
                strings = scanSensor.getTrackListStrings();
                new AlertDialog.Builder(this).setTitle("历史走航数据").setSingleChoiceItems(strings, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stringTableName = strings[which];
                        handler.sendEmptyMessage(msgNewTrack);
                        dialog.dismiss();
                    }
                }).show();

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
        scanSensor.starScan(this);

    }
}
