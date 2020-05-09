package com.grean.vehicledataviewer.protocol;

/**
 * Created by weifeng on 2020/5/8.
 */

public class GetProtocols {
    private static GetProtocols instance = new GetProtocols();
    private ProtocolState protocolState;
    private ProtocolInfo protocolInfo;
    private GetProtocols(){

    }

    public static GetProtocols getInstance() {
        return instance;
    }

    synchronized public ProtocolState getProtocolState() {
        if(protocolState == null){
            protocolState = new DataAcquisitionState(ProtocolTcpServer.getInstance());
        }
        return protocolState;
    }

    public void setProtocolInfo(ProtocolInfo info){
        this.protocolInfo = info;
    }

    public ProtocolInfo getProtocolInfo() {
        return protocolInfo;
    }
}
