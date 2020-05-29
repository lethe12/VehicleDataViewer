package com.ScrollablePanel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.grean.vehicledataviewer.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by weifeng on 2017/9/14.
 */

public class HistoryDataPanelAdapter extends PanelAdapter{
    private static final int TITLE_TYPE = 4;
    private static final int ELEMENT_TYPE = 0;
    private static final int DATE_TYPE = 1;
    private static final int DATA_TYPE = 2;
    private List<ElementInfo> element = new ArrayList<>();
    private List<String> date = new ArrayList<>();
    private List<List<String>> data = new ArrayList<>();
    @Override
    public int getRowCount() {
        return data.size()+1;
    }

    public int getItemViewType(int row, int column) {
        if((row==0)&&(column==0)){
            return TITLE_TYPE;
        }else if(row == 0){
            return ELEMENT_TYPE;
        }else if(column == 0){
            return DATE_TYPE;
        }else{
            return DATA_TYPE;
        }
    }

    public void setElement(List<ElementInfo> element) {
        this.element = element;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    @Override
    public int getColumnCount() {
        return data.get(0).size()+1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
        int viewType = getItemViewType(row, column);
        switch (viewType){
            case DATA_TYPE:
                setDataView(row,column, (DataViewHolder) holder);
                break;
            case DATE_TYPE:
                setDateView(row, (DateViewHolder) holder);
                break;
            case ELEMENT_TYPE:
                setElementView(column, (ElementViewHolder) holder);
                break;
            case TITLE_TYPE:

                break;
            default:
                setDataView(row,column, (DataViewHolder) holder);
                break;

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case DATE_TYPE:
                return new HistoryDataPanelAdapter.DateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listlayout_date, parent, false));
            case DATA_TYPE:
                return new HistoryDataPanelAdapter.DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listlayout_data,parent,false));
            case TITLE_TYPE:
                return new HistoryDataPanelAdapter.TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listlayout_title,parent,false));
            case ELEMENT_TYPE:
                return new HistoryDataPanelAdapter.ElementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listlayout_element,parent,false));
            default:
                return new HistoryDataPanelAdapter.DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listlayout_data,parent,false));
        }
        /*return new HistoryDataPanelAdapter.TitleViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_title, parent, false));*/
    }

    private void setDateView(int pos, HistoryDataPanelAdapter.DateViewHolder viewHolder) {
        String dateInfo = date.get(pos - 1);
        if (dateInfo != null && pos > 0) {
            viewHolder.dateTextView.setText(dateInfo);
        }
    }

    private void setDataView(final int row, final int column, HistoryDataPanelAdapter.DataViewHolder viewHolder) {
        String dataInfo = data.get(row - 1).get(column - 1);
        if (dataInfo != null ) {
            viewHolder.dataTextView.setText(dataInfo);
        }
    }

    private void setElementView(int pos, HistoryDataPanelAdapter.ElementViewHolder viewHolder) {
        ElementInfo elementInfoInfo = element.get(pos - 1);
        if (elementInfoInfo != null && pos > 0) {
            viewHolder.nameTextView.setText(elementInfoInfo.getName());
            viewHolder.unitTextView.setText(elementInfoInfo.getUnit());
        }
    }


    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public TitleViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.listItemTitle);
        }
    }

    private static class DateViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;

        public DateViewHolder(View itemView) {
            super(itemView);
            this.dateTextView = (TextView) itemView.findViewById(R.id.listItemDate);
        }

    }

    private static class ElementViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView unitTextView;

        public ElementViewHolder(View itemView) {
            super(itemView);
            this.nameTextView = (TextView) itemView.findViewById(R.id.listItemElementName);
            this.unitTextView = (TextView) itemView.findViewById(R.id.listItemElementUnit);
        }
    }

    private static class DataViewHolder extends RecyclerView.ViewHolder {
        public TextView dataTextView;

        public DataViewHolder(View itemView) {
            super(itemView);
            this.dataTextView = (TextView) itemView.findViewById(R.id.listItemData);
        }

    }
}
