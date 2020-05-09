package com.grean.vehicledataviewer.protocol;

/**
 * Created by weifeng on 2020/5/8.
 */

public class UploadingConfigFormat {
    private String serverAddress;
    private int serverPort;

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
