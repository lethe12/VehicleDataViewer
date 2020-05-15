package com.grean.vehicledataviewer.protocol;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by weifeng on 2018/6/28.
 */

public class ProtocolTcpServer extends Observable implements  ProtocolCommand{
    private static final String tag = "ProtocolTcpServer";
    private UploadingConfigFormat format;
    private static ProtocolTcpServer instance = new ProtocolTcpServer();

    private TcpServerInfo mainServerInfo;
    private ConcurrentLinkedQueue<byte[]> sendBuff = new ConcurrentLinkedQueue<>();

    private ProtocolTcpServer(){

    }

    public UploadingConfigFormat getFormat() {
        return format;
    }

    public static ProtocolTcpServer getInstance() {
        return instance;
    }

    public void setConfig(UploadingConfigFormat configFormat){

        format = configFormat;

        if(mainServerInfo!=null){
            mainServerInfo.getProtocolState().setConfig(format);
        }

        mainServerInfo = new TcpServerInfo("main",format.getServerAddress(),
                format.getServerPort(),sendBuff);
    }

    public void setState(ProtocolState state) {
        mainServerInfo.setProtocolState(state);
        if(format!=null){
            mainServerInfo.getProtocolState().setConfig(format);
        }
    }



    synchronized public void connectServer(Context context){
        if(mainServerInfo!=null) {
            Log.d(tag,"main server start");
            if ((format != null) && (!mainServerInfo.isRun())) {
                new ConnectThread(context, mainServerInfo).start();

            }
        }
    }



    private class TcpServerInfo {
        private boolean connected = false,run = false;
        private String ip,name;
        private int port;
        private ProtocolState protocolState;
        private ConcurrentLinkedQueue<byte[]> send;
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private ReceiverThread receiverThread;
        public TcpServerInfo(String name,String ip,int port,ConcurrentLinkedQueue<byte[]> buff){
            this.ip = ip;
            this.name = name;
            this.port = port;
            send = buff;
        }


        public void setReceiverThread(ReceiverThread receiverThread) {
            this.receiverThread = receiverThread;
        }

        public ReceiverThread getReceiverThread() {
            return receiverThread;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public OutputStream getOutputStream() {
            return outputStream;
        }

        public Socket getSocket() {
            return socket;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void setOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void setRun(boolean run) {
            this.run = run;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setProtocolState(ProtocolState protocolState) {
            this.protocolState = protocolState;
        }

        public boolean isConnected() {
            return connected;
        }

        public boolean isRun() {
            return run;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public ConcurrentLinkedQueue<byte[]> getSend() {
            return send;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public ProtocolState getProtocolState() {
            return protocolState;
        }
    }




    @Override
    public boolean executeSendTask(byte[] buff) {
        if(mainServerInfo.isConnected()) {
            //Log.d(tag,mainServerInfo.name+"executeSendTask");
            mainServerInfo.getSend().add(buff);
        }
        return mainServerInfo.isConnected();
    }

    @Override
    public boolean isConnected() {
        return mainServerInfo.isConnected();
    }

    @Override
    public void reconnect() {
        if(mainServerInfo.isRun()){
            if(mainServerInfo.getSocket()!=null){
                if(mainServerInfo.getSocket().isConnected()){
                    try {
                        mainServerInfo.getSocket().shutdownInput();
                        mainServerInfo.getSocket().shutdownOutput();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }else{
            /*connectThread = new ConnectThread(context);
            connectThread.start();*/

        }
    }


    private class ConnectThread extends Thread{
        private Context context;
        private TcpServerInfo info;
        public ConnectThread(Context context,TcpServerInfo info){
            this.context = context;
            this.info = info;
        }

        @Override
        public void run() {
            info.setRun(true);
            int times  =0;
            while (!interrupted()&&info.isRun()){
                if(isOnline()){
                    break;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(times == 6){

                }else{
                    times++;
                }
            }

            while ((!interrupted())&&info.isRun()){
                if (info.isConnected()){//已连接服务器
                    try {
                        if(!info.getSend().isEmpty()){
                            //Log.d(tag,info.name+" send buff rest size "+String.valueOf(sendBuff.size())
                            //        +"ID="+String.valueOf(getId()));
                            info.getSocket().sendUrgentData(0xFF);
                            info.getOutputStream().write(info.getSend().poll());
                            info.getOutputStream().flush();

                        }
                    } catch (IOException e) {
                        Log.d(tag,"发送失败");
                        try {
                            info.getSocket().close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }else{
                    info.setSocket(new Socket());
                    info.setReceiverThread(new ReceiverThread(info));
                    info.getReceiverThread().start();

                    /**
                     * 等待连接
                     */
                    int waitTimes = 0;
                    do {
                        waitTimes++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }while (!info.isConnected()&&(waitTimes < 29));

                    try {
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /*try {
                        Thread.sleep(29900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean isOnline(){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info!=null && info.isAvailable()){
                Log.d(tag,"is online");

                return true;
            }else{
                return false;
            }
        }
    }

    private class ReceiverThread extends Thread{
        TcpServerInfo info;
        public ReceiverThread (TcpServerInfo info){
            this.info = info;
        }

        @Override
        public void run() {
            super.run();
            try {

                Log.d(tag,info.name+"IP:"+info.getIp()+" Port:"+String.valueOf(info.getPort())
                        +"Thread ID="+String.valueOf(getId()));
                info.getSocket().connect(new InetSocketAddress(info.getIp(),info.getPort()),5000);
                info.getSocket().setTcpNoDelay(true);
                info.getSocket().setSoLinger(true,30);
                info.getSocket().setSendBufferSize(10240);
                info.getSocket().setKeepAlive(true);
                info.setInputStream(info.getSocket().getInputStream());
                info.setOutputStream(info.getSocket().getOutputStream());
                info.getSocket().setOOBInline(true);

                int count;
                byte[] readBuff = new byte[4096];


                info.setConnected(true);
                setChanged();
                Log.d(tag,info.name+"已连接服务器" +"Thread ID="+String.valueOf(getId()));

                info.getProtocolState().handleNewConnect();
                while (info.isConnected()){
                    if (info.getSocket().isConnected()&&(!info.getSocket().isClosed())){
                        while ((count = info.getInputStream().read(readBuff))!=-1 && info.isConnected()){
                            info.getProtocolState().handleReceiveBuff(readBuff,count);
                        }
                        info.setConnected(false);
                        break;
                    }else {
                        info.setConnected(false);
                    }
                    Log.d(tag,"one turn");
                }
            } catch (IOException e) {
                info.setConnected(false);
                Log.d(tag,"找不到服务器" +"Thread ID="+String.valueOf(getId()));
                info.getProtocolState().handleNetError();

                e.printStackTrace();
            }
            finally {
                info.setConnected(false);
                try {
                    info.getSocket().close();
                    Log.d(tag,info.name+"关闭链接" +"Thread ID="+String.valueOf(getId()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setChanged();
        }
    }

    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }


}
