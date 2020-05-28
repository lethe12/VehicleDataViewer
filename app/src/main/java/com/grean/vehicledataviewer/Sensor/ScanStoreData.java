package com.grean.vehicledataviewer.Sensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grean.vehicledataviewer.DataBaseTask;
import com.grean.vehicledataviewer.model.TrackFormat;
import com.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 存储，读取数据
 * Created by weifeng on 2020/5/11.
 */

public class ScanStoreData {
    private static final String tag = "ScanStoreData";
    private Context context;
    private DataBaseTask dataBaseTask;
    private static final String createNewCacheTable = "CREATE TABLE track_cache(id INTEGER PRIMARY KEY AUTOINCREMENT,date LONG,tvoc FLOAT,lat DOUBLE,lng DOUBLE)";//走航数据
    private static final String reNameTable = "alter table track_cache rename to ";


    public ScanStoreData (Context context){
        this.context = context;
        dataBaseTask = new DataBaseTask(context,1);
    }

    /**
     * 存储当前点位至缓存
     * @param data
     */
    public void savePoint(long timestamp,SensorData data){
        SQLiteDatabase db = dataBaseTask.getWritableDatabase();
        if(data!=null){
            //data.converterCoordinate();
            ContentValues values = new ContentValues();
            values.put("date",timestamp);
            values.put("tvoc",data.getMeanTVoc());
            values.put("lat",data.getLatLng().latitude);
            values.put("lng",data.getLatLng().longitude);
            db.beginTransaction();
            try {
                db.insert("track_cache", null, values);
                db.setTransactionSuccessful();
            }catch (Exception e){

            }finally {
                db.endTransaction();
            }
            db.close();
        }
    }

    /**
     * 将当前缓存存储至新表，并删除缓存
     */
    public void saveTrack(long timestamp){
        SQLiteDatabase db = dataBaseTask.getWritableDatabase();
        String tableName = "track_"+ tools.timeStamp2TcpStringWithoutMs(timestamp);
        String sqString = reNameTable+tableName;
        db.execSQL(sqString);
        db.execSQL(createNewCacheTable);
        db.beginTransaction();
        try {
            db.setTransactionSuccessful();
        }catch (Exception e){

        }
        finally {
            db.endTransaction();
        }
    }

    public List<String> getTrackList(){
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = dataBaseTask.getReadableDatabase();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name",null);
        while (cursor.moveToNext()){
            String string = cursor.getString(0).substring(0,8);
            //Log.d(tag,string);
            if(string.equals("track_20")) {
                list.add(cursor.getString(0));
            }
        }
        cursor.close();
        return list;
    }

    public int getTrackCacheSize(){
        SQLiteDatabase db = dataBaseTask.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM track_cache ORDER BY id asc",null);
        int count=0;
        while (cursor.moveToNext()){
            count++;
        }
        return count;
    }

    public void deleteTrack(String tableName){
        SQLiteDatabase db = dataBaseTask.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+tableName,new String[]{});
        db.beginTransaction();
        try {
            db.setTransactionSuccessful();
        }catch (Exception e){

        }
        finally {
            db.endTransaction();
        }
    }

    private String getHistoryDataString(SensorHistoryDataFormat format,String trackName) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("TrackName",trackName);
        JSONArray  array = new JSONArray();
        int size = format.getSize();
        for(int i=0;i<size;i++){
            JSONObject point = new JSONObject();
            point.put("Id",format.getId().get(i));
            point.put("Date",format.getTime().get(i));
            point.put("TVoc",format.getTVoc().get(i));
            point.put("Lat",format.getLat().get(i));
            point.put("Lng",format.getLng().get(i));
            array.put(point);
        }
        object.put("TrackArray",array);
        return object.toString();

    }

    public boolean exportToFile(String trackName,String fileNameString){
        String pathName = "/storage/emulated/0/GREAN/"; // /storage/sdcard0/GREAN/
        String fileName = fileNameString+".txt";
        File path = new File(pathName);
        File file = new File(path,fileName);
        try {
            if (!path.exists()) {
                //Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if (!file.exists()) {
                //Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            SensorHistoryDataFormat format = getTrackData(trackName);
            osw.write(getHistoryDataString(format,trackName));
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }



    private SensorHistoryDataFormat getTrackData(String name){
        SensorHistoryDataFormat format = new SensorHistoryDataFormat();
        SQLiteDatabase db = dataBaseTask.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM "+name+" ORDER BY id asc",null);
        int id;
        long time;
        double lat,lng;
        float tVoc;
        while (cursor.moveToNext()){
            id = cursor.getInt(0);
            time = cursor.getLong(1);
            tVoc = cursor.getFloat(2);
            lat = cursor.getDouble(3);
            lng = cursor.getDouble(4);
            format.addOnePoint(id,time,tVoc,lat,lng);
        }
        cursor.close();
        return format;
    }

    public TrackFormat getTrack(String tableName){
        TrackFormat format = new TrackFormat();
        SQLiteDatabase db = dataBaseTask.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM "+tableName+" ORDER BY id asc",null);
        if (cursor.moveToNext()){
            format.setStartPoint(cursor.getDouble(3),cursor.getDouble(4));
        }
        while (cursor.moveToNext()){
            format.addOnePoint(cursor.getDouble(3),cursor.getDouble(4), (float) cursor.getDouble(2),
                    cursor.getLong(1),cursor.getInt(0));
        }
        cursor.close();
        return format;
    }
}
