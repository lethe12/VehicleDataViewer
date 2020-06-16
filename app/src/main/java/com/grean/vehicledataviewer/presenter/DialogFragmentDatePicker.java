package com.grean.vehicledataviewer.presenter;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.grean.vehicledataviewer.R;
import com.tools;

/**
 * Created by weifeng on 2020/6/16.
 */

public class DialogFragmentDatePicker extends DialogFragment implements View.OnClickListener{
    private static final String tag = "DialogFragmentDatePicker";
    public static final int FUN_SEARCH=1,FUN_EXPORT=2;
    private DatePickerListener listener;
    private TextView tvTitle,tvStart,tvEnd;
    private Button btnConfirm,btnCancel;
    private String titleString,startSting,endString;
    private long startDate,endDate;
    private int fun;


    public DialogFragmentDatePicker(DatePickerListener listener,String titleString,String start,String end,int fun){
        super();
        this.listener = listener;
        this.titleString = titleString;
        this.startSting = start;
        this.endString = end;
        startDate = tools.string2timestamp(start.substring(5));
        endDate = tools.string2timestamp(end.substring(5));
        this.fun = fun;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_date_picker,container);
        tvTitle = view.findViewById(R.id.tvDatePickerTitle);
        tvStart = view.findViewById(R.id.tvDatePickerStart);
        tvEnd = view.findViewById(R.id.tvDatePickerEnd);
        btnCancel = view.findViewById(R.id.btnDatePickerCancel);
        btnConfirm = view.findViewById(R.id.btnDatePickerComfirm);
        tvEnd.setOnClickListener(this);
        tvStart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        tvTitle.setText(titleString);
        tvStart.setText(startSting);
        tvEnd.setText(endString);
        return view;
    }


    private String getDateString(int year,int month,int dayOfMonth){
        String dateString = String.valueOf(year)+"-";
        if(month < 9){
            dateString += "0"+String.valueOf(month+1)+"-";
        }else{
            dateString += String.valueOf(month+1)+"-";
        }

        if(dayOfMonth < 10){
            dateString += "0"+String.valueOf(dayOfMonth);
        }else {
            dateString += String.valueOf(dayOfMonth);
        }
        return dateString+" 00:00";
    }

    @Override
    public void onClick(View v) {
        Calendar ca;
        switch (v.getId()){
            case R.id.btnDatePickerCancel:
                dismiss();
                break;
            case R.id.btnDatePickerComfirm:
                dismiss();
                listener.onComplete(startDate,endDate+24*3600*1000l,fun);
                break;
            case R.id.tvDatePickerStart:
                ca = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvStart.setText("起始时间:"+String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth)+" 00:00");

                        startDate = tools.string2timestamp(getDateString(year,month,dayOfMonth));
                    }
                },ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.tvDatePickerEnd:
                ca = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvEnd.setText("终止时间:"+String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth)+" 23:59");
                        endDate = tools.string2timestamp(getDateString(year,month,dayOfMonth));
                    }
                },ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH)).show();
                break;
            default:
                break;
        }
    }
}
