package com.grean.vehicledataviewer.protocol;

/**
 * Created by weifeng on 2018/6/28.
 */

public interface ProtocolCommand {
    /**
     * 发送数据接口
     * @param buff
     * @return
     */
    boolean executeSendTask(byte[] buff);

    boolean isConnected();

    /**
     * 重连
     */
    void reconnect();
}
