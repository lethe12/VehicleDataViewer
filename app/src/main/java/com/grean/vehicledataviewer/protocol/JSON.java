package com.grean.vehicledataviewer.protocol;

import com.grean.vehicledataviewer.Sensor.SensorData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weifeng on 2020/5/8.
 */

public class JSON {
    private static final String tag= "JSON";

    public static boolean isFrameRight(String content){
        if(content.length() <14){
            return false;
        }

        if(!content.substring(0,2).equals("##")){
            return false;
        }

        if(!content.substring(content.length()-4,content.length()).equals("$#$#")){
            return false;
        }

        String string = content.substring(2,content.indexOf("$$"));
        try{
            int len = Integer.valueOf(string);
            if(len!=(content.length()-14)){
                return false;
            }
        }catch (NumberFormatException e){
            return false;
        }

        return true;
    }

    private static byte[] insertFrame(String content){
        String lenString = String.format("%06d",content.length());
        return ("##"+lenString+"$$"+content+"$#$#").getBytes();
    }

    public static byte[] getRealTimeCommand() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("Function","RealTimeData");
        return insertFrame(object.toString());
    }

    private static void handleRealTimeData(JSONObject object,ProtocolInfo info) throws JSONException {
        SensorData data= info.getSensorData();
        if(object.has("TVoc")){
            data.setTvocData((float) object.getDouble("TVoc"));
        }
        if(object.has("Lng")){
            data.setLng(object.getDouble("Lng"));
        }
        if(object.has("Lat")){
            data.setLat(object.getDouble("Lat"));
        }

    }


    public static void handleJsonString(String string,ProtocolInfo info) throws JSONException {
        JSONObject object = new JSONObject(string);
        if(object.has("Function")){
            if(object.getString("Function").equals("RealTimeData")){
                handleRealTimeData(object,info);
            }else{

            }
        }
    }
}
