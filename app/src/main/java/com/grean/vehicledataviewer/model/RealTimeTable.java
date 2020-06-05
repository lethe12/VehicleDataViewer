package com.grean.vehicledataviewer.model;

import com.ScrollablePanel.ElementInfo;
import com.ScrollablePanel.HistoryDataPanelAdapter;
import com.ScrollablePanel.ScrollablePanel;
import com.grean.vehicledataviewer.Sensor.SensorData;
import com.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2020/5/29.
 */

public class RealTimeTable {
    private static final String[] elementNames ={"TVoc","经度","纬度"};
    private static final String[] elementUnit={"ppm"," "," "};
    private List<String> dateList = new ArrayList<>();
    private List<List<String>> dataList = new ArrayList<>();


    public static void setDefaultElement(HistoryDataPanelAdapter adapter){
        List<ElementInfo> elementInfoList = new ArrayList<>();
        for (int i=0;i<3;i++){
            ElementInfo info = new ElementInfo();
            info.setName(elementNames[i]);
            info.setUnit(elementUnit[i]);
            elementInfoList.add(info);
        }
        adapter.setElement(elementInfoList);

        List<String> date = new ArrayList<>();
        date.add("-");
        adapter.setDate(date);

        List<List<String>> data = new ArrayList<>();
        List<String> item = new ArrayList<>();
        for(int i=0;i<3;i++){
            item.add("-");
        }
        data.add(item);
        adapter.setData(data);
    }

    public void setFirstItem(HistoryDataPanelAdapter adapter ,long date, SensorData data){
        dateList.clear();
        dataList.clear();
        dateList.add(tools.timeToChartString(date));
        List<String> item = new ArrayList<>();
        item.add(tools.float2String4((float) data.getMeanTVoc()));
        item.add(tools.float2String4((float) data.getLat()));
        item.add(tools.float2String4((float) data.getLng()));
        dataList.add(item);
        adapter.setData(dataList);
        adapter.setDate(dateList);
    }

    public void addOneItem(HistoryDataPanelAdapter adapter ,long date,SensorData data){
        dateList.add(0,tools.timeToChartString(date));
        //dateList.add(tools.timeToChartString(date));
        List<String> item = new ArrayList<>();
        item.add(tools.float2String4((float) data.getMeanTVoc()));
        item.add(tools.float2String4((float) data.getLat()));
        item.add(tools.float2String4((float) data.getLng()));
        dataList.add(0,item);
        //dataList.add(item);
        adapter.setData(dataList);
        adapter.setDate(dateList);
    }

}
