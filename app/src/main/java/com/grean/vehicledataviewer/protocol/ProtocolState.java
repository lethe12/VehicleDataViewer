package com.grean.vehicledataviewer.protocol;


import com.grean.vehicledataviewer.Sensor.SensorData;

/**
 * Created by weifeng on 2018/6/28.
 */

public interface ProtocolState {
    /**
     * 接收处理方法
     * @param buff
     * @param length
     */
    void handleReceiveBuff(byte[] buff,int length);

    /**
     * 是否已经连接
     * @return
     */
    boolean isConnect();

    /**
     * 获取实时数据
     */
    void getRealTimeData();


    /**
     * 设置协议因子编码
     * @param format
     */
    void setConfig(UploadingConfigFormat format);



    /**
     * 处理网络异常
     */
    void handleNetError();

    /**
     * 处理新建网络链接
     */
    void handleNewConnect();


    void setSensorData(SensorData data);
}
