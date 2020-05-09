package com.grean.vehicledataviewer.presenter;

import com.grean.vehicledataviewer.Sensor.SensorData;

/**
 * Created by weifeng on 2020/5/8.
 */

public interface MainDisplayListener {
    void onRealTimeResult(SensorData data);
    void onToast(String string);
}
